import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ConnectionTask implements Runnable {
    private final Server server;
    BufferedReader in;
    BufferedOutputStream out;

    public ConnectionTask(Server server, Socket socket) {
        this.server = server;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
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
    }
}