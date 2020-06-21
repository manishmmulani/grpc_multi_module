package com.mulani.grpc_client;

import com.mulani.grpc_project.GreetingRequest;
import com.mulani.grpc_project.GreetingResponse;
import com.mulani.grpc_project.GreetingServiceGrpc;
import com.mulani.grpc_project.GreetingServiceGrpc.GreetingServiceBlockingStub;
import com.mulani.grpc_project.GreetingServiceGrpc.GreetingServiceStub;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    // Async stub
    private GreetingServiceStub clientStub;
    // blocking stub
    private GreetingServiceBlockingStub blockingClientStub;

    public GreetingClient(String host, int port) {
        Channel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        blockingClientStub = GreetingServiceGrpc.newBlockingStub(channel);
    }

    public String makeRequest(String payload) {
        GreetingRequest request = GreetingRequest.newBuilder().setName(payload).build();
        GreetingResponse response = blockingClientStub.sayHello(request);
        return response.getMessage();
    }

    public static void main(String[] args) {
        System.out.println("Hello world from grpc client");
        System.out.println(new GreetingClient("localhost", 8080).makeRequest("mulani"));
    }
}
