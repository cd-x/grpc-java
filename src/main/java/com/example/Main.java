package com.example;

import com.example.services.CalculatorServiceImpl;
import com.example.services.GreetingServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {
    private static final int PORT = 50051;
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(PORT)
                .addService(new CalculatorServiceImpl())
                .addService(new GreetingServiceImpl())
                .build();
        try{
            server.start();
            log.info("Server started listening on port: {}", PORT);
        }catch (IOException exception){
            log.error("Error occurred while starting the server.");
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            log.info("Shutting down server.");
            server.shutdown();
            log.info("Server stopped.");
        }));

        server.awaitTermination();
    }
}