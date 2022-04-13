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
    protected String contentTypeBody;

    protected List<NameValuePair> QueryNameValuePairList;

    public Request(String method, String path, List<String> headers) {
        this.method = method;
        this.path = path;
        this.headers = headers;
    }

    public Request(String method, String path, List<String> headers, String body, String contentTypeBody) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.contentTypeBody = contentTypeBody;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        getQueryParams();
        if (QueryNameValuePairList.get(0) == null) {
            return path;
        }
        return QueryNameValuePairList.get(0).toString();
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public List<String> getQueryParam(String name) {
        return QueryNameValuePairList.stream()
                .filter(x -> x.getName().equals(name))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList());
    }

    public void getQueryParams() {
        QueryNameValuePairList = URLEncodedUtils.parse(path, StandardCharsets.UTF_8, '?', '&', ';');
    }

    public List<String> getPostParam(String name) {
        return getPostParams().stream()
                .filter(x -> x.getName().equals(name))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getPostParams() {
        if (contentTypeBody.equals("application/x-www-form-urlencoded")) {
            return URLEncodedUtils.parse(body, StandardCharsets.UTF_8, '&', ';');
        }
        return null;
    }
}