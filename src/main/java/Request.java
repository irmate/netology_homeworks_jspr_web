public class Request {
    private String method;
    private String path;
    private String http;

    public Request(String method, String message, String http) {
        this.method = method;
        this.path = message;
        this.http = http;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}