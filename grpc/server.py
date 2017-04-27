from concurrent import futures
from time import sleep

import grpc
import services_pb2
import services_pb2_grpc


class ServiceServicer(services_pb2_grpc.ServiceServicer):

    def GetExaminationByPatient(self, request, context):
        return services_pb2.Examination(
            id = 1,
            date = "2010-10-10",
            doctor = services_pb2.Doctor(first_name="XXX", last_name="YYY", specialization="ZZZ"),
            patient = services_pb2.Patient(first_name="AAA", last_name="BBB"),
            results = services_pb2.Results(redBloodCells=10, whiteBloodCells=20))


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
