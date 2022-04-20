package request;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class Body {
    private String contentBody;
    private String contentTypeBody;

    public Body(List<String> headers, BufferedInputStream in) {
        if (extractHeader(headers, "Content-Type").isPresent()) {
            parseBody(headers, in);
        }
    }

    public String getContentTypeBody() {
        return contentTypeBody;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}