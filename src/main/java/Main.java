import java.io.*;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        final var threadPool = Executors.newFixedThreadPool(64);
        final var server = new Server();

        server.addHandler("GET", "/message", (Handler) (request, responseStream) -> {
            try {
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.addHandler("POST", "/message", (Handler) (request, responseStream) -> {
            try {
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.listen(9999);

        while (true) {
            try {
                threadPool.submit(new ConnectionTask(server, server.toAccept()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}