package request;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class Body {
    private String contentBody;
    private String contentTypeBody;
    private RequestContextIml requestContextIml;

    public Body(List<String> headers, BufferedInputStream in) {
        if (extractHeader(headers, "Content-Type").isPresent()) {
            parseBody(headers, in);
        }
    }

    public String getContentTypeBody() {
        return contentTypeBody;
    }

    public String getContentBody() {
        return contentBody;
    }

    public RequestContextIml getRequestContextIml() {
        return requestContextIml;
    }

    private Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private void parseBody(List<String> headers, BufferedInputStream in) {
        try {
            var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
            in.skip(headersDelimiter.length);
            int length = 0;
            byte[] bodyBytes = null;
            final var contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                length = Integer.parseInt(contentLength.get());
                bodyBytes = in.readNBytes(length);
                contentBody = new String(bodyBytes);
            }
            final var contentType = extractHeader(headers, "Content-Type");
            if (contentType.isPresent()) {
                contentTypeBody = contentType.get();
            }
            requestContextIml = new RequestContextIml(new ByteArrayInputStream(bodyBytes), length, contentTypeBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}