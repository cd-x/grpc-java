package setup;

import com.example.blog.impl.BlogService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.grpc.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.Objects;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoDbTestSetup {
    protected Server server;
    protected ManagedChannel managedChannel;
    protected static final int PORT = 50053;
    protected MongoClient mongoClient;
    @BeforeAll
    public void setupGrpcServer() throws IOException {
        mongoClient = MongoClients.create("mongodb://root:root@localhost:27017/");
        server = ServerBuilder.forPort(PORT)
                .addService(new BlogService(mongoClient))
                .build().start();
        managedChannel = ManagedChannelBuilder.forAddress("localhost", PORT).usePlaintext().build();
    }

    @AfterAll
    public void shutDownServer(){
        mongoClient.close();
        if(Objects.nonNull(managedChannel)) managedChannel.shutdown();
        if(Objects.nonNull(server)) server.shutdown();
    }
}
