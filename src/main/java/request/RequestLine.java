package request;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class RequestLine {
    private final String method;
    private final String protocol;
    private final String path;
    private List<NameValuePair> nameValuePairList;

    public RequestLine(String[] requestLine) {
        method = requestLine[0];
        path = requestLine[1];
        protocol = requestLine[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        getQueryParams();
        if (nameValuePairList.get(0) == null) {
            return path;
        }
        return nameValuePairList.get(0).toString();
    }

    public List<NameValuePair> getNameValuePairList() {
        return nameValuePairList;
    }

    private void getQueryParams() {
        nameValuePairList = URLEncodedUtils.parse(path, StandardCharsets.UTF_8, '?', '&', ';');
    }

    public List<String> getQueryParam(String name) {
        return nameValuePairList.stream()
                .filter(x -> x.getName().equals(name))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList());
    }
}