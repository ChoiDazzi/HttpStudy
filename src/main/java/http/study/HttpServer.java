package http.study;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpServer {
	private Map<String, String> sessionMap = new HashMap<>();
    public void executeHttpServer(InputStream in, OutputStream out, String path) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        	
        try {
            String brLine = br.readLine(); // GET /hello HTTP/1.0
            String[] httpInfo = brLine.split(" ");
            
            String requestHeaders = readRequestHeaders(br);
            System.out.println("Request Headers:\n" + requestHeaders);
            
            String method = httpInfo[0];
            String url = httpInfo[1];
            String queryStr = null;
            if (url.contains("?")) {
                String[] urls = url.split("\\?");
                url = urls[0];
                queryStr = urls[1];
            }
            
            if ("/".equals(url)) {
                url += "index.html";
            } else if ("/home".equals(url)) {
                //302 처리
                found(out);
            }

            File file = new File(path + url);

            if (file.exists()) { //200
                success(br, file, out, queryStr, method);
            } else { //400
                notFound(out, path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String readRequestHeaders(BufferedReader br) throws IOException {
        StringBuilder headers = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            headers.append(line).append("\n");
        }

        return headers.toString();
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

    private void success(BufferedReader br, File file, OutputStream out, String queryStr, String method) {
        try {
            FileInputStream fis = new FileInputStream(file);
            String htmlFile = new String(fis.readAllBytes());
            out.write("HTTP/1.1 200 OK\r\n".getBytes());

            if ("GET".equals(method)) {
                if (queryStr != null) {
                    htmlFile = replaceStr(queryStr, htmlFile);
                }
                out.write("\r\n".getBytes());
                out.write(htmlFile.getBytes());
            } else if ("POST".equals(method)) {
            	//session 처리 -> cookie 값읽어서 처리(if) / get이든, post이든 상관없이 
            	String session = UUID.randomUUID().toString();
            	sessionMap.put(session, "세션저장성공!");
            	
                out.write("Content-Type: text/html; charset=UTF-8\r\n".getBytes());
                out.write(("Set-Cookie: sessionId=" + session + ";Path=/\r\n").getBytes());
                out.write("\r\n".getBytes()); 
                	
                String requestParam = getParamHeader(br);
                out.write(requestParam.getBytes(StandardCharsets.UTF_8));
                out.write(htmlFile.getBytes());
            }

            out.flush();
            	
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String getParamHeader(BufferedReader br) throws IOException {
    	int contentLength = 0;
        while (true) {
            String line = br.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            if (line.startsWith("Content-Length: ")) {
                contentLength = Integer.parseInt(line.substring("Content-Length: ".length()));
            }
        }

        char[] buffer = new char[contentLength];
        int bytesRead = br.read(buffer);
        String requestParam = URLDecoder.decode(new String(buffer, 0, bytesRead), StandardCharsets.UTF_8.toString());
    	return requestParam;
    }
    
    private String replaceStr(String queryStr, String htmlStr) { //id=1&pw=2
       String[] queryStrs = queryStr.split("&");
       for (String qStr : queryStrs) {
          String[] queryArr = qStr.split("=");
          htmlStr = htmlStr.replace(queryArr[0], queryArr[1]); //key, value
       }
       return htmlStr;
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