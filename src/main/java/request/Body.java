package request;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class Body {
    private String contentBody;
    private String contentTypeBody;
    private List<NameValuePair> nameValuePairList;
    private List<FileItem> fileItemList;
    private RequestContextIml requestContextIml;

    public Body(List<String> headers, BufferedInputStream in) {
        if (extractHeader(headers, "Content-Type").isPresent()) {
            parseBody(headers, in);
            if (contentTypeBody.equals("application/x-www-form-urlencoded")) {
                nameValuePairList = getPostParams();
            }
            if (contentTypeBody.startsWith("multipart/form-data")) {
                fileItemList = getParts();
            }
        }
    }

    public String getContentTypeBody() {
        return contentTypeBody;
    }

    private Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private void parseBody(List<String> headers, BufferedInputStream in) {
        try {
            int length = 0;
            byte[] bodyBytes = null;
            final var contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                length = Integer.parseInt(contentLength.get());
                bodyBytes = in.readNBytes(length);
                contentBody = new String(bodyBytes);
            }
            final var contentType = extractHeader(headers, "Content-Type");
            if (contentType.isPresent()) {
                contentTypeBody = contentType.get();
            }
            requestContextIml = new RequestContextIml(new ByteArrayInputStream(bodyBytes), length, contentTypeBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getPostParam(String name) {
        nameValuePairList.stream()
                .filter(x -> x.getName().equals(name))
                .map(NameValuePair::getValue)
                .forEach(System.out::println);
    }

    private List<NameValuePair> getPostParams() {
        return URLEncodedUtils.parse(contentBody, StandardCharsets.UTF_8, '&', ';');
    }

    public void getPart(String name) {
        fileItemList.stream()
                .filter(item -> item.getFieldName().equals(name))
                .forEach(item -> {
                    try {
                        if (item.getName() == null) {
                            System.out.println(new String(item.get(), StandardCharsets.UTF_8));
                        } else {
                            item.write(new File(".", "/src/main/resources/" + item.getName()));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private List<FileItem> getParts() {
        List<FileItem> fileItemList = null;
        try {
            var fileUpload = new FileUpload(new DiskFileItemFactory());
            fileItemList = fileUpload.parseRequest(requestContextIml);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        return fileItemList;
    }
}