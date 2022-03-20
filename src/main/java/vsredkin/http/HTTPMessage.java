package vsredkin.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HTTPMessage {

    protected HTTPMessage(String proto) {
        this.proto = proto;
        this.headers = new HashMap<>();
        this.body = new byte[0];
    }

    public String getProto() {
        return proto;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setBody(byte[] body){
        this.body = body;
        this.headers.put("Content-Length", String.valueOf(body.length));
    }

    private String proto;
    private Map<String, String> headers;
    private byte[] body;

    protected byte[] serializeHeaders() {
        try{
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            for (Map.Entry<String, String> header : this.headers.entrySet()) {
                buffer.write(header.getKey().getBytes(StandardCharsets.UTF_8));
                buffer.write(": ".getBytes(StandardCharsets.UTF_8));
                buffer.write(header.getValue().getBytes(StandardCharsets.UTF_8));
                buffer.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }

            return buffer.toByteArray();
        }catch (IOException e) {
            return null;
        }

    }
}

