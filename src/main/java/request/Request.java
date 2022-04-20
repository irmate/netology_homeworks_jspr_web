package request;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Request {
    private final String GET = "GET";
    private final String POST = "POST";
    private final RequestLine requestLine;
    private List<String> headers;
    private Body body;

    public Request(BufferedInputStream in, BufferedOutputStream out) {
        this.requestLine = new RequestLine(parseRequestLineAndHeaders(in, out));
        if (!this.requestLine.getMethod().equals("GET")) {
            body = new Body(headers, in);
        }
    }

    private void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad service.Request\r\n" +
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

    private String[] parseRequestLineAndHeaders(BufferedInputStream in, BufferedOutputStream out) {
        String[] requestLine = null;
        final var allowedMethods = List.of(GET, POST);
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
            }
            requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
            if (requestLine.length != 3) {
                badRequest(out);
            }
            final var method = requestLine[0];
            if (!allowedMethods.contains(method)) {
                badRequest(out);
            }
            // ищем заголовки
            final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
            final var headersStart = requestLineEnd + requestLineDelimiter.length;
            final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
            if (headersEnd == -1) {
                badRequest(out);
            }
            // отматываем на начало буфера
            in.reset();
            // пропускаем requestLine
            in.skip(headersStart);
            final var headersBytes = in.readNBytes(headersEnd - headersStart);
            headers = Arrays.asList(new String(headersBytes).split("\r\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestLine;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Body getBody() {
        return body;
    }
}