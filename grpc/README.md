This is application which simulates hospital system for saving and retriving patients' examination results. It uses grpc.

Install required dependencies:
```
pip3 install grpcio
pip3 install grpcio-tools
```

To generate required code:
```
    python3 -m grpc_tools.protoc -I . --python_out=. services.proto --grpc_python_out=.
```

Then:
```
    python3 server.py
    python3 client.py
```