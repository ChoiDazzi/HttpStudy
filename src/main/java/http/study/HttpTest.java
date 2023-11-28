package http.study;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpTest {
    private static String path = "D:\\sbadmin";
    private static int port = 8080;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("HTTP Server Strart ( PORT: " + port + " )");

            while (true) {
                Socket socket = serverSocket.accept();
                HttpThread th = new HttpThread(socket, path);
                th.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
