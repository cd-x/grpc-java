package services;

import com.example.greetings.GreetRequest;
import com.example.greetings.GreetResponse;
import com.example.greetings.GreetingServiceGrpc;
import io.grpc.Deadline;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import setup.TestSetup;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class GreetingServiceTest extends TestSetup {
    private GreetingServiceGrpc.GreetingServiceBlockingStub stub;
    private GreetingServiceGrpc.GreetingServiceStub nbStub;
    @BeforeAll
    public void init(){
        stub = GreetingServiceGrpc.newBlockingStub(managedChannel);
        nbStub = GreetingServiceGrpc.newStub(managedChannel);
    }

    @Test
    public void testGreetManyTimes(){
        Iterator<GreetResponse> response = stub.greetManyTimes(GreetRequest.newBuilder().setName("rishi").build());
        while (response.hasNext()){
            log.info("stream output: {}", response.next());
        }
    }

    // bidirectional streams
    @Test
    public void test_greetAll() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<GreetRequest> stream = nbStub.greetAll(new StreamObserver<GreetResponse>() {
            @Override
            public void onNext(GreetResponse greetResponse) {
                log.info("[Server]: {}", greetResponse.getGreeting());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Stream.of("ramesh", "suresh", "monu", "sonu").forEach(name -> stream.onNext(GreetRequest.newBuilder().setName(name).build()));
        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    // request with deadline
    @Test
    public void test_greet_with_deadline(){
       GreetResponse response =  stub.withDeadline(Deadline.after(2, TimeUnit.SECONDS)).greetWithDeadline(GreetRequest.newBuilder().setName("rishi").build());
       log.info("[server]: {}", response.getGreeting());
       try {
           GreetResponse response1 = stub.withDeadline(Deadline.after(150, TimeUnit.MILLISECONDS))
                   .greetWithDeadline(GreetRequest.newBuilder().setName("rishi").build());
       }catch (StatusRuntimeException e){
           log.error(e.getMessage());
           Status status = Status.fromThrowable(e);
           assertEquals(Status.Code.DEADLINE_EXCEEDED, status.getCode());
       }
    }
}
