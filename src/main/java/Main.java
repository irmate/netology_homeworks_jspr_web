import java.io.*;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        final var threadPool = Executors.newFixedThreadPool(64);
        final var server = new Server();
        server.listen(9999);
        try {
            while (true) {
                threadPool.submit(new NewConnection(server.toAccept()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}