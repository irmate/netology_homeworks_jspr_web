package request;

public class RequestLine {
    private final String method;
    private final String protocol;
    private final String path;

    public RequestLine(String[] requestLine) {
        method = requestLine[0];
        path = requestLine[1];
        protocol = requestLine[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}