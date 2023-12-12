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
        InputStream in = null;
        OutputStream out = null;
        try {
        	in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
//            HttpServer hs = new HttpServer();
//            hs.executeHttpServer(in, out, path);
            Http dd = new Http();
            dd.executeHttpServer(in, out, path);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
			try {if (in != null) in.close();} catch (Exception e) { e.printStackTrace(); }
			try {if (out != null) out.close();} catch (Exception e) { e.printStackTrace(); }
		}
    }
}
