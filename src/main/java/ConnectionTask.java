import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.portlet.PortletRequestContext;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ConnectionTask implements Runnable {
    private final Server server;
    InputStream inputStream;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private final String GET = "GET";
    private final String POST = "POST";

    int bodyLength;

    public ConnectionTask(Server server, Socket socket) {
        this.server = server;
        try {
            inputStream = socket.getInputStream();
            in = new BufferedInputStream(inputStream);
            out = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    @Override
    public void run() {
        final var allowedMethods = List.of(GET, POST);
        RequestBuilder builder = new RequestBuilder();

        try {
            final var limit = 4096;
            in.mark(limit);
            final var buffer = new byte[limit];
            final var read = in.read(buffer);

            // ищем request line
            final var requestLineDelimiter = new byte[]{'\r', '\n'};
            final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
            if (requestLineEnd == -1) {
                badRequest(out);
                return;
            }

            // читаем request line
            final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
            if (requestLine.length != 3) {
                badRequest(out);
                return;
            }

            final var method = requestLine[0];
            if (!allowedMethods.contains(method)) {
                badRequest(out);
                return;
            }
            builder.setMethod(method);

            final var path = requestLine[1];
            if (!path.startsWith("/")) {
                badRequest(out);
                return;
            }
            builder.setPath(path);

            // ищем заголовки
            final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
            final var headersStart = requestLineEnd + requestLineDelimiter.length;
            final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
            if (headersEnd == -1) {
                badRequest(out);
                return;
            }

            // отматываем на начало буфера
            in.reset();
            // пропускаем requestLine
            in.skip(headersStart);

            final var headersBytes = in.readNBytes(headersEnd - headersStart);
            final var headers = Arrays.asList(new String(headersBytes).split("\r\n"));
            builder.setHeaders(headers);

            // для GET тела нет
            if (!method.equals(GET)) {
                in.skip(headersDelimiter.length);
                // вычитываем Content-Length, чтобы прочитать body
                final var contentLength = extractHeader(headers, "Content-Length");
                if (contentLength.isPresent()) {
                    final var length = Integer.parseInt(contentLength.get());
                    final var bodyBytes = in.readNBytes(length);
                    final var body = new String(bodyBytes);
                    builder.setBody(body);

                    bodyLength = length;
                }

                final var contentType = extractHeader(headers, "Content-Type");
                if (contentType.isPresent()) {
                    var contentTypeBody = contentType.get();
                    builder.setContentTypeBody(contentTypeBody);
                }
            }

            var request = builder.build();

            request.setInputStream(inputStream);
            request.setBodyLength(bodyLength);

            System.out.println(request.getParts().isEmpty());

            server.getHandlersStorage().get(request.getMethod()).get(request.getPath()).handle(request, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }
    }
}