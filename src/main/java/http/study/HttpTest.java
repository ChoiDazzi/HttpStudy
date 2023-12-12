package http.study;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpTest {
    private static String path = "D:\\sbadmin";
    private static int port = 8080;

    public static void main(String[] args) { 
    	ServerSocket serverSocket = null;
    	Socket socket = null;
    	HttpThread ht = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("HTTP Server Strart ( PORT: " + port + " )");
            
            while (true) {
                socket = serverSocket.accept();
                ht = new HttpThread(socket, path);
                ht.start();
            }
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
			try { if(serverSocket != null) serverSocket.close(); } catch (IOException e) { }
			try { if(socket != null) socket.close(); } catch (IOException e) { }
		}

    }
}
