//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.io.IOException;
//import java.util.concurrent.Executors;
//
//public class HttpServerTests {
//    @BeforeAll
//    static void initSuite() {
//        System.out.println("Running http-server tests");
//    }
//
//    @AfterAll
//    static void completeSuite() {
//        System.out.println("http-server tests completed");
//    }
//
//    @Test
//    public void server_listen_test() {
//        var server = Mockito.mock(Server.class);
//        server.listen(9999);
//
//        Mockito.verify(server, Mockito.atLeastOnce()).listen(9999);
//    }
//
//    @Test
//    public void server_toAccept_test(){
//        try {
//            var server = Mockito.mock(Server.class);
//            server.listen(9999);
//            server.toAccept();
//
//            Mockito.verify(server, Mockito.atLeastOnce()).toAccept();
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void newConnection_run_test() throws InterruptedException {
//        var threadPool = Executors.newSingleThreadExecutor();
//        var newConnection = Mockito.mock(Request.class);
//        threadPool.submit(newConnection);
//        threadPool.shutdown();
//
//        Thread.sleep(1000);
//        Mockito.verify(newConnection, Mockito.atLeastOnce()).run();
//    }
//}