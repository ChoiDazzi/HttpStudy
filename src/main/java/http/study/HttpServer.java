package http.study;

import java.io.*;

public class HttpServer {
    public void executeHttpServer(InputStream in, OutputStream out, String path) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        try {
            String brLine = br.readLine(); // GET /hello HTTP/1.0
            String[] httpInfo = brLine.split(" ");
            String url = httpInfo[1];
            if (url.contains("?")) {
                String[] urls = url.split("\\?");
                url = urls[0];
            }

            if ("/".equals(url)) {
                url += "index.html";
            } else if ("/home".equals(url)) {
                //302 처리
                found(out);
            }

            File file = new File(path + url);

            if (file.exists()) { //200
                success(file, out);
            } else { //400
                notFound(out, path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void found(OutputStream out) {
        try {
            out.write("HTTP/1.1 302 Found\r\n".getBytes());
            out.write(("Location: /\r\n\r\n").getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void success(File file, OutputStream out) {
        try {
            byte[] buffer = new byte[4096];
            int bytesRead;

            FileInputStream fis = new FileInputStream(file);
            out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());

            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notFound(OutputStream out, String path) {
        try {
            byte[] buffer = new byte[4096];
            int bytesRead;
            File file = new File(path + "/404.html");
            FileInputStream fis = new FileInputStream(file);
            out.write("HTTP/1.1 404 NotFound\r\n\r\n".getBytes());

            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
