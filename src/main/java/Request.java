import java.util.List;

public class Request {
    private final List<String> validPaths = List.of(
            "/index.html",
            "/spring.svg",
            "/spring.png",
            "/resources.html",
            "/styles.css",
            "/app.js",
            "/links.html",
            "/forms.html",
            "/classic.html",
            "/events.html",
            "/events.js"
    );
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

    public List<String> getValidPaths() {
        return validPaths;
    }
}