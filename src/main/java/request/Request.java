package request;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private final String GET = "GET";
    private final String POST = "POST";
    private RequestLine requestLine;
    private URI uri;
    private List<String> headers;
    private Body body;

    public Request(BufferedInputStream in, BufferedOutputStream out) {
        try {
            requestLine = new RequestLine(parseRequestLineAndHeaders(in, out));
            uri = new URI(requestLine.getPath());
            if (!requestLine.getMethod().equals("GET")) {
                body = new Body(headers, in);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
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

    public URI getUri() {
        return uri;
    }

    private List<NameValuePair> getQueryParams() {
        return URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
    }

    public List<NameValuePair> getQueryParam(String name) {
        return getQueryParams().stream()
                .filter(x -> x.getName().equals(name))
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getPostParam(String name) {
        return getPostParams().stream()
                .filter(x -> x.getName().equals(name))
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getPostParams() {
        return URLEncodedUtils.parse(body.getContentBody(), StandardCharsets.UTF_8, '&', ';');
    }

    public FileItem getPart(String name) {
        return getParts().stream()
                .filter(item -> item.getFieldName().equals(name))
                .findFirst().get();
    }

    public List<FileItem> getParts() {
        List<FileItem> fileItemList = null;
        try {
            var fileUpload = new FileUpload(new DiskFileItemFactory());
            fileItemList = fileUpload.parseRequest(body.getRequestContextIml());
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        return fileItemList;
    }
}