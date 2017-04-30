const grpc = require('grpc');
const proto = grpc.load('services.proto');
const readline = require('readline');
const client = new proto.service.Service('localhost:50051', grpc.credentials.createInsecure());

function printHelp() {
    console.log("-help");
    console.log("-exit");
    console.log("-getAllExaminations");
    console.log("-getLastExaminationByPatient first_name last_name");
    console.log("-getAllExaminationByPatient first_name last_name");
    console.log("-getAllExaminationWithGivenParameterName paramater_name");
    console.log("-getAllExaminationWithGivenParameterNameAndRange parameter_name lower_bound upper_bound");
    console.log("-insertExamination doctor_first_name doctor_last_name patient_first_name patient_last_name parameter_name value");
}

var rl = readline.createInterface(process.stdin, process.stdout);
rl.setPrompt('>> ');
rl.prompt();
printHelp();
rl.on('line', function(line) {
    var words = line.split(' ');
    if (line === "exit") rl.close();
    if (line === "help") {
        printHelp();
    }

    if(words[0] === "insertExamination") {
        insertExamination(words[1], words[2], words[3], words[4], words[5], words[6]);
    }
    else if(words[0] === "getAllExaminationWithGivenParameterNameAndRange") {
        getAllExaminationWithGivenParamaterNameAndRange(words[1], words[2], words[3])
    } else if(words[0] === "getAllExaminationWithGivenParameterName") {
        getAllExaminationWithGivenParamaterName(words[1]);
    } else if(words[0]==="getAllExaminationByPatient") {
        getAllExaminationByPatient(words[1], words[2]);
    } else if(words[0] === "getLastExaminationByPatient") {
        getLastExaminationByPatient(words[1], words[2]);
    } else if(words[0] === "getAllExaminations") {
        handleAllExaminations();
    } else {
        console.log("Unknown command");
        printHelp();
    }
    rl.prompt();
}).on('close',function(){
    process.exit(0);
});

function handleAllExaminations() {
    var call = client.getAllExaminations();
    call.on('data', function(feature) {
    console.log("id:", feature.id);
    console.log("date:", feature.date);
    console.log("doctor:", feature.doctor);
    console.log("parameters:", feature.results.parameters);
    console.log("")
    });
    call.on('end', function() {
      console.log("All Examinations had been received");
    });
    call.on('status', function(status) {
    });
}

function getLastExaminationByPatient(first_name, last_name) {
    var patient = {first_name: first_name, last_name: last_name};
    client.getLastExaminationByPatient(patient, function (err, examination) {
        if(err) {
            console.log(err);
        } else {
            if (examination.id === -1) {
                console.log("Patient with given first_name and last_name doesn't exist in database.")
            } else {
                console.log(examination);
            }
        }
    });
}

function getAllExaminationByPatient(first_name, last_name) {
    var patient = {first_name: first_name, last_name: last_name};
    var call = client.getAllExaminationByPatient(patient);
    call.on('data', function(feature) {
        console.log("id:", feature.id);
        console.log("date:", feature.date);
        console.log("doctor:", feature.doctor);
        console.log("parameters:", feature.results.parameters);
        console.log("")
        });
        call.on('end', function() {
            console.log("All Examinations had been received");
        });
        call.on('status', function(status) {
        });
}


function getAllExaminationWithGivenParamaterName(parameter) {
    var request = {
            "name": parameter
    };
    console.log(request);
    var call = client.getAllExaminationWithGivenParameterName(request);
    call.on('data', function(feature) {
        console.log("id:", feature.id);
        console.log("date:", feature.date);
        console.log("doctor:", feature.doctor);
        console.log("parameters:", feature.results.parameters);
        console.log("")
    });
    call.on('end', function() {
        console.log("All Examinations had been received");
    });
    call.on('status', function(status) {
    });
}

function getAllExaminationWithGivenParamaterNameAndRange(word, lower_bound, upper_bound) {
    var query = {
        "parameter_name": {
            "name": word
        },
        "lwbound": lower_bound,
        "upbound": upper_bound
    };
    var call = client.getAllExaminationWithGivenParameterNameAndRange(query);
    call.on('data', function(feature) {
        console.log("id:", feature.id);
        console.log("date:", feature.date);
        console.log("doctor:", feature.doctor);
        console.log("parameters:", feature.results.parameters);
        console.log("")
    });
    call.on('end', function() {
        console.log("All Examinations had been sent");
    });
    call.on('status', function(status) {
    });
}


function insertExamination(doctor_first_name, doctor_last_name, patient_first_name,
                           patient_last_name, parameter_name, value) {
    var query = {
        "doctor_first_name": doctor_first_name,
        "doctor_last_name": doctor_last_name,
        "patient_first_name": patient_first_name,
        "patient_last_name": patient_last_name,
        "results": {
            "parameters":
                [
                    {
                    "parameter_name": {
                        "name": parameter_name
                    },
                    "value": value
                }
            ]}
        };
    client.insertExamination(query, function (err, examination) {
        if(err) {
            console.log(err);
        } else {
            console.log(examination);
        }
    });
}