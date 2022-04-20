
package request;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class Body {
    private String contentBody;
    private String contentTypeBody;
    private List<NameValuePair> nameValuePairList;

    public Body(List<String> headers, BufferedInputStream in) {
        if (extractHeader(headers, "Content-Type").isPresent()) {
            parseBody(headers, in);
            if (contentTypeBody.equals("application/x-www-form-urlencoded")) {
                nameValuePairList = getPostParams();
            }
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

    public void getPostParam(String name) {
        nameValuePairList.stream()
                .filter(x -> x.getName().equals(name))
                .map(NameValuePair::getValue)
                .forEach(System.out::println);
    }

    private List<NameValuePair> getPostParams() {
        return URLEncodedUtils.parse(contentBody, StandardCharsets.UTF_8, '&', ';');
    }
}