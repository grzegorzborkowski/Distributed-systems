const grpc = require('grpc');
const proto = grpc.load('services.proto');
const readline = require('readline');
const client = new proto.service.Service('localhost:50051', grpc.credentials.createInsecure());

var rl = readline.createInterface(process.stdin, process.stdout);
rl.setPrompt('enter> ');
rl.prompt();
rl.on('line', function(line) {
    if (line === "exit") rl.close();
    if (line === "help") {
        console.log("-getAllExaminations");
        console.log("-getLastExaminationByPatient first_name last_name");
        console.log("-getAllExaminationByPatient first_name last_name");
        console.log("-getAllExaminationWithGivenParameterName paramater_name");
        console.log("-getAllExaminationWithGivenParameterNameAndRange parameter_name lower_bound upper_bound");
        console.log("-insertExamination doctor_first_name doctor_last_name patient_first_name patient_last_name parameter_name value");
    }
    rl.prompt();
}).on('close',function(){
    process.exit(0);
});


// var call = client.getAllExaminations();
// call.on('data', function(feature) {
//     console.log(feature.results.parameters);
// });
// call.on('end', function() {
//     // The server has finished sending
// });
// call.on('status', function(status) {
//     // process status
// });

