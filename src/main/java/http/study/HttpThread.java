package http.study;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpThread extends Thread {
    private Socket clientSocket;
    private String path;

    public HttpThread(Socket clientSocket, String path) {
        this.clientSocket = clientSocket;
        this.path = path;
    }

    @Override
    public void run() {
        System.out.println("NEW THREAD");
        try (
                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();
        ) {
            HttpServer hs = new HttpServer();
            hs.executeHttpServer(in, out, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
