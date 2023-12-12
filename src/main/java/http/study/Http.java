package http.study;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
//수정 중
public class Http {
    public void executeHttpServer(InputStream in, OutputStream out, String path) {
    	BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            String brLine = br.readLine(); // GET /hello HTTP/1.0
            String[] httpInfo = brLine.split(" ");
            
            String httpHeader = getHttpHeader(br);
            System.out.println("httpHeader:\n" + httpHeader);
            
            
            String method = httpInfo[0];
            String url = httpInfo[1];
            if (url.contains("?")) { //StringUtiles로 바꾸기
                String[] urls = url.split("\\?");
                url = urls[0];
            }
            
            if ("/".equals(url)) {
                url += "index.html";
            } else if ("/home".equals(url)) {
                //302 처리
            }
            
            File file = new File(path + url);

            if (file.exists()) { //200
                success(httpHeader, file, out, method);
            } else { 
            	//400
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getHttpHeader(BufferedReader br) throws IOException {
        StringBuilder headers = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            headers.append(line).append("\n");
        }

        return headers.toString();
    }
    
    private void success(String httpHeader, File file, OutputStream out, String method) {
    	try {
			FileInputStream fis = new FileInputStream(file);
			
			if ("GET".equals(method)) {
				out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
		    	byte[] buffer = new byte[1024];
		        int bytesRead;
		        while ((bytesRead = fis.read(buffer)) != -1) {
		            out.write(buffer, 0, bytesRead);
		        }
		        out.flush();
			} else if ("POST".equals(method)) {
				getBody(httpHeader);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void getBody(String httpHeader) {
    	int start = httpHeader.indexOf("Content-Length: ") + "Content-Length: ".length();
        int end = httpHeader.indexOf("\n", start);
        
        String contentLength = httpHeader.substring(start, end);
    }
}
