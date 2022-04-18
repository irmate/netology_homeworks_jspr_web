package request;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Body {

    private String contentBody;
    private String contentTypeBody;

    public Body(List<String> headers, BufferedInputStream in) {
        parseBody(headers, in);
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
            final var contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                final var length = Integer.parseInt(contentLength.get());
                final var bodyBytes = in.readNBytes(length);
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