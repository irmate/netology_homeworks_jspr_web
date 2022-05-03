import request.Request;
import utils.Handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int POOL_SIZE = 64;
    private final ExecutorService threadPool;
    private final Map<String, Map<String, Handler>> handlersStorage;

    public Server() {
        threadPool = Executors.newFixedThreadPool(POOL_SIZE);
        handlersStorage = new ConcurrentHashMap<>();
    }

    public void listen(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.submit(() -> connect(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect(Socket socket) {
        try (var in = new BufferedInputStream(socket.getInputStream());
             var out = new BufferedOutputStream(socket.getOutputStream())) {
            var request = new Request(in, out);
            if (handlersStorage.get(request.getRequestLine().getMethod()).get(request.getUri().getPath()) == null) {
                out.write((
                        "HTTP/1.1 404 Not Found.Handlers\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
            }
            handlersStorage
                    .get(request.getRequestLine().getMethod())
                    .get(request.getUri().getPath())
                    .handle(request, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        if (!handlersStorage.containsKey(method)) {
            handlersStorage.put(method, new ConcurrentHashMap<>());
            handlersStorage.get(method).put(path, handler);
        }
    }
}