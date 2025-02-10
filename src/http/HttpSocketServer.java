package http;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpSocketServer {
    public void run() {
        System.out.println("Hello, World!");

        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("0.0.0.0", 8080));

            while (true) {
                Socket socket = serverSocket.accept();

                BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                OutputStreamWriter outputWriter = new OutputStreamWriter(socket.getOutputStream());

                String request = inputReader.readLine();
                String method = request.split(" ")[0];
                String uri = request.split(" ")[1];

                Map<String, String> headers = new HashMap<>();

                String headerField = inputReader.readLine();
                while (!headerField.isEmpty()) {
                    String[] tokens = headerField.split(": ");
                    String name = tokens[0];
                    String value = tokens[1];
                    headers.put(name, value);

                    headerField = inputReader.readLine();
                }

                int contentLength = Integer.parseInt(Optional.ofNullable(headers.get("Content-Length")).orElse("0"));

                ByteArrayOutputStream bodyBuffer = new ByteArrayOutputStream();
                for (int i = 0; i < contentLength; i++) {
                    bodyBuffer.write(inputReader.read());
                }

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
                    """.stripIndent().formatted(uri, method, headers.get("Content-Type"), bodyBuffer.toString());

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
