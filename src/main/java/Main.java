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
            threadPool.submit(() -> {
                try (
                        var socket = server.toAccept();
                        var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        var out = new BufferedOutputStream(socket.getOutputStream())
                ) {
                    final var requestLine = in.readLine();
                    final var parts = requestLine.split(" ");
                    if (parts.length != 3) {
                        return;
                    }
                    var request = new Request(parts[0], parts[1], parts[2]);
                    server.getHandlersStorage().get(request.getMethod()).get(request.getPath()).handle(request, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}