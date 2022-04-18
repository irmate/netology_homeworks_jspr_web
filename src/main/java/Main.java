import java.io.*;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();

        server.addHandler("GET", "/message", (request, responseStream) -> {
            try {
                request.getRequestLine().getNameValuePairList().stream()
                        .filter(value -> !value.toString().startsWith("/"))
                        .forEach(System.out::println);
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

        server.addHandler("POST", "/message", (request, responseStream) -> {
            try {
                request.getBody().getNameValuePairList().forEach(System.out::println);
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
    }
}