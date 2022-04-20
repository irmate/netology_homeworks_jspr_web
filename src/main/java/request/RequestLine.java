package request;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RequestLine {
    private final String method;
    private final String protocol;
    private final String path;
    private List<NameValuePair> nameValuePairList;

    public RequestLine(String[] requestLine) {
        method = requestLine[0];
        path = requestLine[1];
        protocol = requestLine[2];
        nameValuePairList = getQueryParams();
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return nameValuePairList.get(0).getName();
    }

    private List<NameValuePair> getQueryParams() {
        return URLEncodedUtils.parse(path, StandardCharsets.UTF_8, '?', '&', ';');
    }

    public void getQueryParam(String name) {
        nameValuePairList.stream()
                .filter(x -> x.getName().equals(name))
                .map(NameValuePair::getValue)
                .forEach(System.out::println);
    }
}