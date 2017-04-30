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

    if(line.startsWith("insertExamination")) {

    }
    else if(line.startsWith("getAllExaminationWithGivenParmameterNameAndRange")) {

    } else if(line.startsWith("getAllExaminationWithGivenParamaterName")) {

    } else if(line.startsWith("getAllExaminationByPatient")) {

    } else if(line.startsWith("getLastExaminationByPatient")) {
        getLastExaminationByPatient(words[1], words[2]);
    } else if(line.startsWith("getAllExamination")) {
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
      console.log("All Examinations had been sent");
    });
    call.on('status', function(status) {
    });
}

function getLastExaminationByPatient(first_name, last_name) {
    var patient = {first_name: first_name, last_name: last_name};
    console.log(patient);
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