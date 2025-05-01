package com.example.services;

import com.example.calculator.Response;
import com.example.greetings.GreetRequest;
import com.example.greetings.GreetResponse;
import com.example.greetings.GreetingServiceGrpc;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.IntStream;

@Slf4j
public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void greetManyTimes(GreetRequest request, StreamObserver<GreetResponse> streamObserver){
        IntStream.range(1, 10).forEach(i ->{
            streamObserver.onNext(GreetResponse.newBuilder().setGreeting(String.format(">%d# Welcome %s", i, request.getName())).build());
        });
        streamObserver.onCompleted();
    }

    @Override
    public StreamObserver<GreetRequest> greetAll(StreamObserver<GreetResponse> streamObserver){
        return new StreamObserver<GreetRequest>() {
            @Override
            public void onNext(GreetRequest request) {
                streamObserver.onNext(GreetResponse.newBuilder().setGreeting(String.format("Hello, %s", request.getName())).build());
            }

            @Override
            public void onError(Throwable throwable) {
                streamObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                streamObserver.onCompleted();
            }
        };

    }

    @Override
    public void greetWithDeadline(GreetRequest request, StreamObserver<GreetResponse> streamObserver){
        Context context = Context.current();
        try{
            Thread.sleep(300);
            if(context.isCancelled()) return;
            streamObserver.onNext(GreetResponse.newBuilder().setGreeting("Hello, "+request.getName()).build());
        }catch (InterruptedException exception){
            streamObserver.onError(Status.RESOURCE_EXHAUSTED.asRuntimeException());
        }
        streamObserver.onCompleted();
    }

}
