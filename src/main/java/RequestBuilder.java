import java.util.List;

public class RequestBuilder implements IRequestBuilder {
    private static RequestBuilder requestBuilder = new RequestBuilder();
    private String method;
    private String path;
    private List<String> headers;
    private String body;
    private String contentTypeBody;

    public RequestBuilder setMethod(String method) {
        requestBuilder.method = method;
        return requestBuilder;
    }

    public RequestBuilder setPath(String path) {
        requestBuilder.path = path;
        return requestBuilder;
    }

    public RequestBuilder setHeaders(List<String> headers) {
        requestBuilder.headers = headers;
        return requestBuilder;
    }

    public RequestBuilder setBody(String body) {
        requestBuilder.body = body;
        return requestBuilder;
    }

    public RequestBuilder setContentTypeBody(String contentTypeBody) {
        requestBuilder.contentTypeBody = contentTypeBody;
        return requestBuilder;
    }

    @Override
    public Request build() {
        if (requestBuilder.body == null) {
            return new Request(requestBuilder.method, requestBuilder.path, requestBuilder.headers);
        }
        return new Request(requestBuilder.method, requestBuilder.path, requestBuilder.headers, requestBuilder.body, requestBuilder.contentTypeBody);
    }
}