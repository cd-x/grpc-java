package setup;

import com.example.services.CalculatorServiceImpl;
import com.example.services.GreetingServiceImpl;
import io.grpc.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSetup {
    protected Server server;
    protected ManagedChannel managedChannel;
    protected static final int PORT = 50053;
    private static final String CERT_PATH = "ssl/";
    @BeforeAll
    public void setupGrpcServer() throws IOException {
        server = ServerBuilder.forPort(PORT)
                .useTransportSecurity(getCertificate("server.crt"), getCertificate("server.pem"))
                .addService(new CalculatorServiceImpl())
                .addService(new GreetingServiceImpl())
                .build().start();
        ChannelCredentials credentials = TlsChannelCredentials.newBuilder().trustManager(getCertificate("ca.crt")).build();
        managedChannel = Grpc.newChannelBuilderForAddress("localhost", PORT, credentials).build();
        //managedChannel = ManagedChannelBuilder.forAddress("localhost", PORT).usePlaintext().build();
    }
    @AfterAll
    public void shutDownServer(){
        if(Objects.nonNull(managedChannel)) managedChannel.shutdown();
        if(Objects.nonNull(server)) server.shutdown();
    }

    private File getCertificate(String name){
        return new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource(CERT_PATH + name)).getFile());
    }
}
