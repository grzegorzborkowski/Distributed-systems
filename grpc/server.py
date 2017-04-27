from concurrent import futures
from time import sleep

import grpc
import services_pb2
import services_pb2_grpc
from persistence import Data


class ServiceServicer(services_pb2_grpc.ServiceServicer):

    def __init__(self):
        self.data = Data()

    def GetLastExaminationByPatient(self, request, context):
        first_name = request.first_name
        last_name = request.last_name
        for patient in self.data.patients:
            if patient.first_name == first_name and patient.last_name == last_name:
                print(patient)
                return patient.examinations[-1]
        return services_pb2.Examination(id=-1)

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    services_pb2_grpc.add_ServiceServicer_to_server(
        ServiceServicer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    try:
        while True:
            sleep(1000000)
    except KeyboardInterrupt:
        server.stop(0)

serve()
