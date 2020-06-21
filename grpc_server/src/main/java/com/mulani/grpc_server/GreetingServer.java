package com.mulani.grpc_server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GreetingServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(8080).addService(new GreetingServiceImpl()).build();
        server.start();
        server.awaitTermination();
    }
}
