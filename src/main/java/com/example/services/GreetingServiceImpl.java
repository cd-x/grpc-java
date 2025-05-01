package com.example.services;

import com.example.calculator.PrimesRequest;
import com.example.calculator.Response;
import com.example.greetings.GreetRequest;
import com.example.greetings.GreetResponse;
import com.example.greetings.GreetingServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.stream.IntStream;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void greetManyTimes(GreetRequest request, StreamObserver<GreetResponse> streamObserver){
        IntStream.range(1, 10).forEach(i ->{
            streamObserver.onNext(GreetResponse.newBuilder().setGreeting(String.format(">%d# Welcome %s", i, request.getName())).build());
        });
        streamObserver.onCompleted();
    }
}
