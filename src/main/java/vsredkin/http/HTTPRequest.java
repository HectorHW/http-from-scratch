package vsredkin.http;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Scanner;

public class HTTPRequest extends HTTPMessage {

    protected HTTPRequest(String proto) {
        super(proto);
    }

    public HTTPRequest(String method, String path, String proto){
        super(proto);
        this.method = method;
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    private String method;
    private String path;

    public static HTTPRequest readFromStream(InputStream stream) throws RequestReadingException {
        try{

            Scanner scanner = new Scanner(stream);
            String header = scanner.nextLine();
            String[] parts = header.split(" ", 3);
            String method = parts[0];
            String path = parts[1];
            String proto = parts[2];

            HTTPRequest request = new HTTPRequest(method, path, proto);


            String headerString;
            while (!(headerString = scanner.nextLine()).isEmpty()) {
                parts = headerString.split(":", 2);
                request.addHeader(parts[0].trim(), parts[1].trim());
            }

            int bodyLength = Integer.parseInt(request.getHeaders().getOrDefault("Content-Length", "0"));

            ByteArrayOutputStream body = new ByteArrayOutputStream();

            while (bodyLength>0){
                body.write(stream.read());
                bodyLength--;
            }

            request.setBody(body.toByteArray());

            return request;

        }catch (Exception e){
            throw new RequestReadingException();
        }
    }

}
