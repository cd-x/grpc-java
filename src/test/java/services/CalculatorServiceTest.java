package services;

import com.example.calculator.CalculatorServiceGrpc;
import com.example.calculator.Operands;
import com.example.calculator.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import setup.TestSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorServiceTest extends TestSetup {
    private CalculatorServiceGrpc.CalculatorServiceBlockingStub stub;

    @BeforeEach
    public void init(){
        stub = CalculatorServiceGrpc.newBlockingStub(managedChannel);
    }
    @Test
    public void testSum(){
        Response res = stub.getSum(Operands.newBuilder().setLop(23).setRop(34).build());
        assertEquals(57, res.getRes());
    }

    @Test
    public void testMultiply(){
        Response response = stub.multiply(Operands.newBuilder().setRop(2).setLop(3).build());
        assertEquals(6, response.getRes());
    }
}
