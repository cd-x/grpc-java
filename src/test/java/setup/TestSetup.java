package setup;

import com.example.services.CalculatorServiceImpl;
import com.example.services.GreetingServiceImpl;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.Objects;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSetup {
    protected Server server;
    protected ManagedChannel managedChannel;
    protected static final int PORT = 50053;

    @BeforeAll
    public void setupGrpcServer() throws IOException {
        server = ServerBuilder.forPort(PORT)
                .addService(new CalculatorServiceImpl())
                .addService(new GreetingServiceImpl())
                .build().start();
        managedChannel = ManagedChannelBuilder.forAddress("localhost", PORT).usePlaintext().build();
    }
    @AfterAll
    public void shutDownServer(){
        if(Objects.nonNull(managedChannel)) managedChannel.shutdown();
        if(Objects.nonNull(server)) server.shutdown();
    }

}
