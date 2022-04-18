import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class RequestContextIml implements RequestContext {

    String contentType;
    int contentLength;
    InputStream inputStream;

//    public RequestContextIml(InputStream inputStream){
//        this.inputStream = inputStream;
//    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setContentType(String contentType) {
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
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }
}
