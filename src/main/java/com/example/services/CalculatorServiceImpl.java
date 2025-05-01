package com.example.services;

import com.example.calculator.CalculatorServiceGrpc;
import com.example.calculator.Operands;
import com.example.calculator.Response;
import io.grpc.stub.StreamObserver;

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
}
