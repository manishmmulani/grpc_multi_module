package com.mulani.grpc_server;

import com.mulani.grpc_project.GreetingRequest;
import com.mulani.grpc_project.GreetingResponse;
import com.mulani.grpc_project.GreetingServiceGrpc;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void sayHello(GreetingRequest request, io.grpc.stub.StreamObserver<GreetingResponse> responseObserver) {
        GreetingResponse response = GreetingResponse.newBuilder().setMessage("Hello world!! Mr. " + request.getName()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
