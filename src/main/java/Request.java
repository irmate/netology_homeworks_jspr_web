import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    protected String method;
    protected String path;
    protected List<String> headers;
    protected String body;
    protected List<NameValuePair> nameValuePairList;

    public Request(String method, String path, List<String> headers) {
        this.method = method;
        this.path = path;
        this.headers = headers;
    }

    public Request(String method, String path, List<String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
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

    public List<String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public List<String> getQueryParam(String name) {
        return nameValuePairList.stream()
                .filter(x -> x.getName().equals(name))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList());
    }

    public void getQueryParams() {
        nameValuePairList = URLEncodedUtils.parse(path, StandardCharsets.UTF_8, '?', '&', ';');
    }
}