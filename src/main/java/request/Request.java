package request;

import java.util.List;

public class Request {
    private final RequestLine requestLine;
    private final List<String> headers;
    private Body body;

    public Request(String[] requestLine, List<String> headers) {
        this.requestLine = new RequestLine(requestLine);
        this.headers = headers;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public List<String> getHeaders() {
        return headers;
    }

}