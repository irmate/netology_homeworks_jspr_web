package request;

import org.apache.commons.fileupload.RequestContext;

import java.io.InputStream;
import java.nio.charset.Charset;

public class RequestContextIml implements RequestContext {
    private final InputStream inputStream;
    private final int contentLength;
    private final String contentType;

    public RequestContextIml(InputStream inputStream, int contentLength, String contentType) {
        this.inputStream = inputStream;
        this.contentLength = contentLength;
        this.contentType = contentType;
    }

    @Override
    public String getCharacterEncoding() {
        var charset = Charset.defaultCharset();
        return charset.toString();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }
}