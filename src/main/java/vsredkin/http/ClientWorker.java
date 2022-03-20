package vsredkin.http;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class ClientWorker implements Runnable{
    private final Socket clientSocket;

    private final InputStream input;
    private final OutputStream output;

    private boolean expectsRequests = true;

    public ClientWorker(Socket socket) throws IOException, SocketException{
        socket.setTcpNoDelay(true);
        this.clientSocket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
    }

    private void send(HTTPResponse resp) throws IOException {
        this.output.write(resp.serialize());
        this.output.flush();
    }

    @Override
    public void run() {

        try {
            while (this.expectsRequests){
                this.expectsRequests = false;
                try {
                    HTTPRequest r = HTTPRequest.readFromStream(input);

                    System.out.println(r.getMethod() + " " + r.getPath() + " " + r.getProto());

                    this.expectsRequests = r.getHeaders().getOrDefault("Connection", "close").equals("keep-alive");

                    HTTPResponse resp;

                    if (!r.getMethod().equals("GET")) {
                        this.send(new HTTPResponse(405, "Method Not Allowed"));
                        continue;
                    }

                    String path = r.getPath().substring(1);

                    File f = new File(path);

                    if (!f.exists() || f.isDirectory()){
                        this.send(new HTTPResponse(404, "Not Found"));
                        continue;
                    }

                    Scanner scan = new Scanner(f);

                    try{
                        int result = Arrays
                            .stream(scan.nextLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .sum();
                        scan.close();

                        resp = new HTTPResponse(
                            200, "Ok"
                        );
                        resp.addHeader("Content-Type", "text/plain");
                        resp.setBody(String.valueOf(result).getBytes(StandardCharsets.UTF_8));
                        this.send(resp);
                    }catch (NumberFormatException e){
                        this.send(new HTTPResponse(500, "Internal Server Error  "));
                    }


                }catch (RequestReadingException ignored) {
                    this.send(new HTTPResponse(400, "Bad Request"));
                }
            }
        } catch (IOException ignored){
        }
        finally {
            try {
                this.clientSocket.close();
            }catch (IOException ignored) {}

        }


    }
}
