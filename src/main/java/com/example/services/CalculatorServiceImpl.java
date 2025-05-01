package com.example.services;

import com.example.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.IntStream;

@Slf4j
public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void getSum(Operands request, StreamObserver<Response> responseStreamObserver){
        responseStreamObserver.onNext(Response.newBuilder().setRes(
                request.getLop() + request.getRop()
        ).build());
        responseStreamObserver.onCompleted();
    }

    @Override
    public void multiply(Operands request, StreamObserver<Response> responseStreamObserver){
        responseStreamObserver.onNext(Response.newBuilder().setRes(
                request.getLop() * request.getRop()
        ).build());
        responseStreamObserver.onCompleted();
    }

    @Override
    public void primes(PrimesRequest request, StreamObserver<Response> streamObserver){
        int from = request.getFrom(), to = request.getTo();
        boolean[] prime = new boolean[to+1];
        Arrays.fill(prime, true);
        for(int i=2;i*i<=to;i++){
            if(!prime[i]) continue;
            for(int j=2*i;j<=to;j+=i){
                prime[j] = false;
            }
        }
        IntStream.range(from, to).forEach(x ->{
            if (prime[x]) streamObserver.onNext(Response.newBuilder().setRes(x).build());
        });
        streamObserver.onCompleted();
    }

    @Override
    public StreamObserver<AverageRequest> average(StreamObserver<AverageResponse> streamObserver){
        return new StreamObserver<AverageRequest>() {
            int sum = 0, count = 0;
            @Override
            public void onNext(AverageRequest averageRequest) {
                sum += averageRequest.getNum();
                count++;
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Error occurred.");
                streamObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                streamObserver.onNext(AverageResponse.newBuilder().setAvg(
                        (double) sum/count
                ).build());
                streamObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<MaxRequest> max(StreamObserver<MaxResponse> responseObserver) {
        return new StreamObserver<MaxRequest>() {
            Integer mx = Integer.MIN_VALUE;
            @Override
            public void onNext(MaxRequest maxRequest) {
                mx = Math.max(mx, maxRequest.getNum());
                responseObserver.onNext(MaxResponse.newBuilder().setMax(mx).build());
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
    @Override
    public void divide(Operands operands, StreamObserver<Response> streamObserver){
        try{
            long res =  operands.getLop()/operands.getRop();
            streamObserver.onNext(Response.newBuilder().setRes(res).build());
        }catch (ArithmeticException exception){
            streamObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("right operand should be greater than zero").asRuntimeException());
        }
    }
}
