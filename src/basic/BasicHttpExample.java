package basic;

import java.io.*;
import java.util.Map;
import java.net.Socket;
import java.util.HashMap;
import java.util.Optional;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 실제로 작동하는 아주 간단한 HTTP 서버 예제입니다.
 * 이 예제를 실행하려면 Main 클래스의 main 메서드에서 new BasicHttpExample().run()을 호출하면 됩니다.
 */
public class BasicHttpExample {
    public void run() {
        System.out.println("Hello, World!");

        try {
            ServerSocket serverSocket = new ServerSocket();

            serverSocket.bind(new InetSocketAddress("0.0.0.0", 8080));

            while (true) {
                Socket socket = serverSocket.accept();

                BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                OutputStreamWriter outputWriter = new OutputStreamWriter(socket.getOutputStream());

                String request = inputReader.readLine(); // GET / HTTP/1.1
                String method = request.split(" ")[0];
                String uri = request.split(" ")[1];

                Map<String, String> headers = new HashMap<>();

                String line = inputReader.readLine();
                while (!line.isEmpty()) {
                    String[] tokens = line.split(": ");
                    String name = tokens[0];
                    String value = tokens[1];
                    headers.put(name, value);

                    line = inputReader.readLine();
                }

                int contentLength = Integer.parseInt(Optional.ofNullable(headers.get("Content-Length")).orElse("0"));

                ByteArrayOutputStream bodyBuffer = new ByteArrayOutputStream();
                for (int i = 0; i < contentLength; i++) {
                    bodyBuffer.write(inputReader.read());
                }

                String contentType = headers.get("Content-Type");

                String responseBody = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>응답을 드려요</title>
                    </head>
                    <body>
                        <h1>당신이 보낸 요청에 대한 응답</h1>
                        <div>당신은 방금 "%s" 경로로 "%s" 요청을 보냈습니다.</div>
                        <div>당신은 "%s" 컨텐트 타입으로 아래와 같은 body를 보냈습니다:</div>
                        <textbox>%s</textbox>
                    </body>
                    </html>
                    """.stripIndent().formatted(uri, method, contentType, bodyBuffer.toString());

                outputWriter.write("HTTP/1.1 200 OK\r\n");
                outputWriter.write("Content-Type: text/html\r\n");
                outputWriter.write("Content-Length: " + responseBody.getBytes().length + "\r\n");
                outputWriter.write("\r\n");
                outputWriter.write(responseBody);
                outputWriter.flush();

                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
