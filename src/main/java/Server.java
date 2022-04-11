import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private final Map<String, Map<String, Handler>> handlersStorage = new ConcurrentHashMap<>();
    private final Map<String, Handler> handlers = new ConcurrentHashMap<>();

    public void listen(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket toAccept() throws IOException {
        return serverSocket.accept();
    }

    public void addHandler(String method, String path, Handler handler){
        handlersStorage.put(method, handlers);
        Map<String, Handler> list = handlersStorage.get(method);
        list.put(path, handler);
    }

    public Map<String, Map<String, Handler>> getHandlersStorage(){
        return handlersStorage;
    }
}