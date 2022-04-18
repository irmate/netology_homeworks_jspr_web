package request;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Body {

    private String contentBody;
    private String contentTypeBody;
    private List<NameValuePair> nameValuePairList;

    public Body(List<String> headers, BufferedInputStream in) {
        parseBody(headers, in);
        if (contentTypeBody.equals("application/x-www-form-urlencoded")) {
            nameValuePairList = getPostParams();
        }
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

    public List<NameValuePair> getNameValuePairList() {
        return nameValuePairList;
    }

    public List<String> getPostParam(String name) {
        return getPostParams().stream()
                .filter(x -> x.getName().equals(name))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getPostParams() {
        return URLEncodedUtils.parse(contentBody, StandardCharsets.UTF_8, '&', ';');
    }
}