import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HTTPResponse extends HTTPMessage {
    protected HTTPResponse(String proto) {
        super(proto);
    }

    public HTTPResponse(int statusCode, String reason) {
        super("HTTP/1.1");
        this.statusCode = statusCode;
        this.reason = reason;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReason() {
        return reason;
    }

    private int statusCode;
    private String reason;


    public byte[] serialize() {
        try{
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            result.write(this.getProto().getBytes(StandardCharsets.UTF_8));
            result.write(" ".getBytes(StandardCharsets.UTF_8));
            result.write(String.valueOf(this.getStatusCode()).getBytes(StandardCharsets.UTF_8));
            result.write(" ".getBytes(StandardCharsets.UTF_8));
            result.write(this.getReason().getBytes(StandardCharsets.UTF_8));
            result.write("\r\n".getBytes(StandardCharsets.UTF_8));

            result.write(this.serializeHeaders());

            result.write("\r\n".getBytes(StandardCharsets.UTF_8));

            result.write(this.getBody());

            return result.toByteArray();
        }catch (IOException e){
            return null;
        }
    }
}
