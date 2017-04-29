import services_pb2

examinations = []
doctors = []
patients = []
results = []
examination_id = 0


def create_results():
    whiteblood_parameter = services_pb2.Parameter(
        parameter_name=
        services_pb2.ParameterName(
            name="WhiteBloodCells"), value=20)
    redblood_paramter = services_pb2.Parameter(
        parameter_name=
        services_pb2.ParameterName(
            name="RedBloodCells",), value=30)
    first_results = services_pb2.Results()
    first_results.parameters.extend([whiteblood_parameter, redblood_paramter])
    results.append(first_results)
    # -------------------------------------------------
    bloodpresure_parameter = services_pb2.Parameter(
        parameter_name=
        services_pb2.ParameterName(
            name="BloodPresure"), value=120)
    redblood_paramter_2 = services_pb2.Parameter(
        parameter_name =
        services_pb2.ParameterName(
            name="RedBloodCells"), value=50)
    second_results = services_pb2.Results()
    second_results.parameters.extend([
        bloodpresure_parameter, redblood_paramter_2
    ])
    results.append(second_results)
    # -------------------------------------------
    hdl_paramter = services_pb2.Parameter(
        parameter_name=
        services_pb2.ParameterName(
            name="HDL"), value=500)
    ldl_parameter = services_pb2.Parameter(
        parameter_name=
        services_pb2.ParameterName(
            name="LDL"), value=250)
    third_results = services_pb2.Results()
    third_results.parameters.extend([
        hdl_paramter, ldl_parameter
    ])
    results.append(third_results)


def create_doctors():
        doctors.extend([
            services_pb2.Doctor(first_name = "Alexander", last_name="Fleming", specialization="Bacterilogy"),
            services_pb2.Doctor(first_name="Sigmund", last_name="Freud", specialization="Psychiatrist"),
            services_pb2.Doctor(first_name="Gregory", last_name="House", specialization="GP")])


def create_examinations():
        examinations.extend([
            services_pb2.Examination(
                id=examination_id, date="2012-12-12", doctor=doctors[0],
                results=results[0]
            ),
            services_pb2.Examination(
                id=examination_id+1, date="2012-12-14", doctor=doctors[1],
                results=results[1]
            ),
            services_pb2.Examination(
                id=examination_id+2, date="2013-01-01", doctor=doctors[2],
                results=results[2]
            )
        ])



def create_patients():
        patients.extend([
            services_pb2.Patient(
                first_name="John", last_name="Kowalsky",
                examinations=[examinations[0], examinations[1]]
            ),
            services_pb2.Patient(
                first_name="Michael", last_name="Corleone",
                examinations=[examinations[2]]
            )
        ])


def insert_patient(patient_first_name, patient_last_name):
    patients.append([
        services_pb2.Patient(
            first_name=patient_first_name,
            last_name=patient_last_name,
            examinations=[]
        )
    ])

create_results()
create_doctors()
create_examinations()
examination_id = examination_id + 3
create_patients()


