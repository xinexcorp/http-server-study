package winter.http;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * HTTP 라이브러리가 노출하는 최상위 클래스입니다.
 * 얘 만들어서 요청 가져오고 알아서 응답하십쇼.
 */
public class Http {
    ServerSocket serverSocket;
    Socket currentClientSocket;

    /**
     * 요청 수신을 시작합니다~
     *
     * @param port 몇번에서 들을까요?
     */
    public void listen(int port) {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("0.0.0.0", port));
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    /**
     * 소켓이 연결되고 유효한(=최소한 HTTP 메시지 형식은 갖춘) HTTP 요청이 올 때까지 기다렸다가
     * 요청을 수신, 파싱하고 반환합니다.
     *
     * @return 수신한 요청 객체!
     */
    public HttpRequest waitForRequest() throws IOException {
        currentClientSocket = serverSocket.accept();
        HttpRequest request = new HttpRequest();

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(currentClientSocket.getInputStream(), StandardCharsets.UTF_8));

        String startLine = inputReader.readLine();
        if (startLine == null) {
            // 클라이언트가 소켓 연결은 만들어놓고 근데 바로 끊어버리면(stream end) 여기에 도착합니다.
            // 그러면은 더 처리할 것도 없음 그냥 때려치셈요 ㅋㅎ
            throw new EOFException();
        }

        request.method = startLine.split(" ")[0];
        request.uri = startLine.split(" ")[1];

        String headerField = inputReader.readLine();
        while (!headerField.isEmpty()) {
            String[] tokens = headerField.split(": ");
            String name = tokens[0];
            String value = tokens[1];
            request.headers.put(name, value);

            headerField = inputReader.readLine();
        }

        int contentLength = Integer.parseInt(Optional.ofNullable(request.headers.get("Content-Length")).orElse("0"));

        ByteArrayOutputStream bodyBuffer = new ByteArrayOutputStream();
        for (int i = 0; i < contentLength; i++) {
            bodyBuffer.write(inputReader.read());
        }

        request.body = bodyBuffer.toString();

        return request;
    }

    /**
     * 컨트롤러가 꽉 채워준 응답 객체를 고대로 돌려보내줍니다.
     *
     * @param response
     */
    public void sendResponse(HttpResponse response) throws IOException {


        if (currentClientSocket == null) {
            throw new IllegalStateException("있을 수 없는 일이라며 난 울었어~");
        }

        OutputStreamWriter outputWriter = new OutputStreamWriter(currentClientSocket.getOutputStream());

        outputWriter.write("HTTP/1.1 200 OK\r\n");
        outputWriter.write("Content-Type: text/html\r\n");
        outputWriter.write("Content-Length: " + response.body.getBytes().length + "\r\n");
        outputWriter.write("\r\n");

        outputWriter.write(response.body);
        outputWriter.flush(); // ㅈㄴ중요!

        currentClientSocket.close();
        currentClientSocket = null;
    }
}
