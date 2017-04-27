import services_pb2

class Data():
    examinations = []
    doctors = []
    patients = []
    results = []

    def create_results(self):
        whiteblood_parameter = services_pb2.Parameter(
            parameter_name="WhiteBloodCells", value="20")
        redblood_paramter = services_pb2.Parameter(
            parameter_name="RedBloodCells", value="30")
        first_results = services_pb2.Results()
        first_results.parameters.extend([whiteblood_parameter, redblood_paramter])
        self.results.append(first_results)
        # -------------------------------------------------
        bloodpresure_parameter = services_pb2.Parameter(
            parameter_name="BloodPresure", value="120")
        redblood_paramter_2 = services_pb2.Parameter(
            parameter_name = "RedBloodCells", value="50")
        second_results = services_pb2.Results()
        second_results.parameters.extend([
            bloodpresure_parameter, redblood_paramter_2
        ])
        self.results.append(second_results)
        # -------------------------------------------
        hdl_paramter = services_pb2.Parameter(
            parameter_name="HDL", value="500")
        ldl_parameter = services_pb2.Parameter(
            parameter_name="LDL", value="250")
        third_results = services_pb2.Results()
        third_results.parameters.extend([
            hdl_paramter, ldl_parameter
        ])
        self.results.append(third_results)

    def create_doctors(self):
            self.doctors.extend([
                services_pb2.Doctor(
                    first_name = "Alexander", last_name="Fleming", specialization="Bacterilogy"
                ),
                services_pb2.Doctor(
                    first_name="Sigmund", last_name="Freud", specialization="Psychiatrist"
                ),
                services_pb2.Doctor(
                    first_name="Gregory", last_name="House", specialization="GP"
                )])

    def create_examinations(self):
        self.examinations.extend([
            services_pb2.Examination(
                id=1, date="2012-12-12", doctor=self.doctors[0],
                results=self.results[0]
            ),
            services_pb2.Examination(
                id=2, date="2012-12-14", doctor=self.doctors[1],
                results=self.results[1]
            ),
            services_pb2.Examination(
                id=3, date="2013-01-01", doctor=self.doctors[2],
                results=self.results[2]
            )
        ])

    def create_patients(self):
        self.patients.extend([
            services_pb2.Patient(
                first_name="John", last_name="Kowalsky",
                examinations=[self.examinations[0], self.examinations[1]]
            ),
            services_pb2.Patient(
                first_name="Michael", last_name="Corleone",
                examinations=[self.examinations[2]]
            )
        ])

    def __init__(self):
        self.create_results()
        self.create_doctors()
        self.create_examinations()
        self.create_patients()