package services;

import com.example.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import setup.TestSetup;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class CalculatorServiceTest extends TestSetup {
    private CalculatorServiceGrpc.CalculatorServiceBlockingStub stub;
    private CalculatorServiceGrpc.CalculatorServiceStub nbStub;

    @BeforeAll
    public void init(){
        stub = CalculatorServiceGrpc.newBlockingStub(managedChannel);
        nbStub = CalculatorServiceGrpc.newStub(managedChannel);
    }
    @Test
    public void testSum(){
        Response res = stub.getSum(Operands.newBuilder().setLop(23).setRop(34).build());
        assertEquals(57, res.getRes());
    }
    // unary request
    @Test
    public void testMultiply(){
        Response response = stub.multiply(Operands.newBuilder().setRop(2).setLop(3).build());
        assertEquals(6, response.getRes());
    }

    // Server side streams
    @Test
    public void testPrimes(){
        Iterator<Response> primes = stub.primes(PrimesRequest.newBuilder().setFrom(3).setTo(100).build());
        assertTrue(primes.hasNext());
        while (primes.hasNext()) System.out.printf("%d ", primes.next().getRes());
    }

    // client side streams
    @Test
    public void testAverage(){
        int[] arr = new int[]{2,3,4,5,6,7,8};
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<AverageRequest> stream =  nbStub.average(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse averageResponse) {
                log.info("Average of all is: {}", averageResponse.getAvg());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Error Occurred.");
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.stream(arr).forEach(x -> stream.onNext(AverageRequest.newBuilder().setNum(x).build()));
        stream.onCompleted();
        try {
            latch.await(3, TimeUnit.SECONDS);
        }catch (InterruptedException exception){
            log.error(exception.getMessage());
        }
    }

    @Test
    public void test_max() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<MaxRequest> stream = nbStub.max(new StreamObserver<MaxResponse>() {
            @Override
            public void onNext(MaxResponse maxResponse) {
                log.info("[server]: max value: {}", maxResponse.getMax());
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
        Stream.of(3,5,1,-1, 23,95,445,9,7).forEach(num -> stream.onNext(MaxRequest.newBuilder().setNum(num).build()));
        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    @Test
    public void test_divide(){
        try {
            Response response = stub.divide(Operands.newBuilder().setLop(3).setRop(0).build());
            log.error("After divide: {}", response.getRes());
        }catch (Exception e) {
            Status status = Status.fromThrowable(e);
            assertEquals(Status.INVALID_ARGUMENT.getCode(), status.getCode());
        }
    }
}
