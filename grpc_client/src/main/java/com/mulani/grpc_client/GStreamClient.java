package com.mulani.grpc_client;

import com.mulani.grpc_project.GData;
import com.mulani.grpc_project.GStreamServiceGrpc;
import com.mulani.grpc_project.InputNum;
import com.mulani.grpc_project.ResultNum;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

public class GStreamClient {

    private Channel channel;
    private GStreamServiceGrpc.GStreamServiceStub asyncStub;
    private GStreamServiceGrpc.GStreamServiceBlockingStub blockingStub;

    public GStreamClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        asyncStub = GStreamServiceGrpc.newStub(channel);
        blockingStub = GStreamServiceGrpc.newBlockingStub(channel);
    }

    private InputNum getInputNum(int integer) {
        return InputNum.newBuilder().setNumber(integer).build();
    }

    public void makeBlockingRequestForLargeDataStream() {
        System.out.println("Making blocking request");

        // Note here we get an iterator as response. Server is streaming response on each iteration in forEachRemaining
        Iterator<GData> gDataIterator = blockingStub.getLargeDataset(getInputNum(25));
        gDataIterator.forEachRemaining(gData -> System.out.println(gData));
    }

    public void makeAsyncRequestForLargeDataStream() {
        System.out.println("Making async request");

        asyncStub.getLargeDataset(getInputNum(25), new StreamObserver<GData>() {
            @Override
            public void onNext(GData value) {
                System.out.println(value);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Completed reading response from server");
            }
        });

        // below is to ensure program does not exit and we see the output from onNext method
        sleep(30_000L);
    }

    private void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void makeStreamingRequest(Function<StreamObserver<ResultNum> , StreamObserver<InputNum>> function) {
        StreamObserver<ResultNum> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(ResultNum value) {
                System.out.println("Received output from server : " + value.getNumber());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Request completed successfully");
            }
        };

        StreamObserver<InputNum> inputNumStreamObserver = function.apply(responseObserver);

        for (int i = 0; i <= 30; i++) {
            inputNumStreamObserver.onNext(getInputNum(i));
        }
        inputNumStreamObserver.onCompleted();

        // below is to be able to see the response of the async request
        sleep(10_000L);
    }

    public void makeStreamingRequestForSum() {
        makeStreamingRequest(asyncStub::getSum);
    }
    public void makeStreamingRequestForBatchedSum() {
        makeStreamingRequest(asyncStub::getSumStream);
    }

    public static void main(String[] args) throws Exception {
        //new GStreamClient("localhost", 8080).makeBlockingRequestForLargeDataStream();
        //new GStreamClient("localhost", 8080).makeAsyncRequestForLargeDataStream();
        //new GStreamClient("localhost", 8080).makeStreamingRequestForSum();
        new GStreamClient("localhost", 8080).makeStreamingRequestForBatchedSum();

        // Observation : for the first two calls - behavior is same.
        // Iterable hasNext and next are getting called every 2 seconds once server responds

        // Observation : server is always assumed to stream the response
        // hence responseObserver is always present
        // if client does not stream the request, then input is just the simple request object, else StreamObserver
    }
}
