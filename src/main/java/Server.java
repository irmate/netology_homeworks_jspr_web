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
    private final Map<String, Handler> handlers;

    public Server() {
        threadPool = Executors.newFixedThreadPool(POOL_SIZE);
        handlersStorage = new ConcurrentHashMap<>();
        handlers = new ConcurrentHashMap<>();
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
            handlersStorage
                    .get(request.getRequestLine().getMethod())
                    .get(request.getRequestLine().getPath())
                    .handle(request, out);
//            request.getRequestLine().getNameValuePairList().stream()
//                    .filter(value -> !value.toString().startsWith("/"))
//                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        handlersStorage.put(method, handlers);
        Map<String, Handler> list = handlersStorage.get(method);
        list.put(path, handler);
    }
}