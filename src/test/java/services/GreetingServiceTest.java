package services;

import com.example.greetings.GreetRequest;
import com.example.greetings.GreetResponse;
import com.example.greetings.GreetingServiceGrpc;
import io.grpc.Server;
import io.grpc.internal.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import setup.TestSetup;

import java.util.Iterator;

@Slf4j
public class GreetingServiceTest extends TestSetup {
    private GreetingServiceGrpc.GreetingServiceBlockingStub stub;
    @BeforeAll
    public void init(){
        stub = GreetingServiceGrpc.newBlockingStub(managedChannel);
    }

    @Test
    public void testGreetManyTimes(){
        Iterator<GreetResponse> response = stub.greetManyTimes(GreetRequest.newBuilder().setName("rishi").build());
        while (response.hasNext()){
            log.info("stream output: {}", response.next());
        }
    }
}
