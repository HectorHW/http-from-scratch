package vsredkin.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException, RequestReadingException {
        ServerSocket socket = new ServerSocket(80);



        while (true){
            Socket client = socket.accept();

            ClientWorker worker = new ClientWorker(client);

            Thread handle = new Thread(worker);
            handle.start();
        }

    }
}
