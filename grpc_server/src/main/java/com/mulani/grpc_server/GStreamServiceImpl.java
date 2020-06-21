package com.mulani.grpc_server;

import com.mulani.grpc_project.GData;
import com.mulani.grpc_project.GStreamServiceGrpc;
import com.mulani.grpc_project.InputNum;
import com.mulani.grpc_project.ResultNum;
import io.grpc.stub.StreamObserver;

public class GStreamServiceImpl extends GStreamServiceGrpc.GStreamServiceImplBase {

    @Override
    public void getLargeDataset(InputNum request, StreamObserver<GData> responseObserver) {
        // Generate and stream GData via onNext calls. Assumption is that each GData object is huge in memory
        int batchSize = request.getNumber();
        for (int i = 0; i <= batchSize; i++) {
            sleep(2000L);
            System.out.println("Sending data over stream");
            // prepare a batch
            var gData = GData.newBuilder().setId(i).setTitle("title:" + i).build();
            responseObserver.onNext(gData);
        }
        responseObserver.onCompleted();
    }

    private void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class RequestStreamObserver implements StreamObserver<InputNum> {

        private StreamObserver<ResultNum> resultNumObserver = null;
        private long sum = 0;

        public RequestStreamObserver(StreamObserver<ResultNum> responseObserver) {
            resultNumObserver = responseObserver;
        }

        @Override
        public void onNext(InputNum value) {
            sum += value.getNumber();

            System.out.println("Obtained input. Partial sum is " + sum);
        }

        @Override
        public void onError(Throwable t) {
            System.out.println("Error occurred " + t);
            resultNumObserver.onError(t);
        }

        @Override
        public void onCompleted() {
            ResultNum result = ResultNum.newBuilder().setNumber(sum).build();

            System.out.println("Ending connection with final sum : " + sum);

            resultNumObserver.onNext(result);
            resultNumObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<InputNum> getSum(StreamObserver<ResultNum> responseObserver) {
        // take all inputs via stream from client and generate single response
        return new RequestStreamObserver(responseObserver);
    }

    private class BatchInputStreamObserver implements StreamObserver<InputNum> {
        private int batchSize;
        private int currentIndexOfBatch = 0;
        private long result = 0;
        private StreamObserver<ResultNum> resultNumStreamObserver;

        public BatchInputStreamObserver(int batchSize, StreamObserver<ResultNum> responseObserver) {
            this.batchSize = batchSize;
            this.resultNumStreamObserver = responseObserver;
        }

        private ResultNum getResultNum() {
            return ResultNum.newBuilder().setNumber(result).build();
        }

        @Override
        public void onNext(InputNum value) {
            currentIndexOfBatch ++;
            result += value.getNumber();

            if (currentIndexOfBatch == batchSize) {

                System.out.println("Sending batched sum : " + result);

                resultNumStreamObserver.onNext(getResultNum());

                System.out.println("Restting currentIndex and result");

                currentIndexOfBatch = 0;
                result = 0;
            }
        }

        @Override
        public void onError(Throwable t) {
            System.out.println("Error occurred " + t);
            resultNumStreamObserver.onError(t);
        }

        @Override
        public void onCompleted() {
            System.out.println("Ending the connection with result : " + result);

            resultNumStreamObserver.onNext(getResultNum());
            resultNumStreamObserver.onCompleted();
        }
    }
    @Override
    public StreamObserver<InputNum> getSumStream(StreamObserver<ResultNum> responseObserver) {
        // take 3 inputs every time before generating a response. It should be completed once client is done with passing inputs
        return new BatchInputStreamObserver(3, responseObserver);
    }
}
