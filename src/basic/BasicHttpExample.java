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
            // 요청을 받을 서버 소켓을 만들고 0.0.0.0 호스트네임에 8080 포트로 바인딩합니다.
            // 이렇게 하면 모든 호스트가 이 호스트의 8080 포트로 요청을 보낼 수 있습니다.
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("0.0.0.0", 8080));

            // 웹 서버는 요청을 받으면 요청을 처리하고 응답을 보내는 일을 반복합니다.
            // 따라서 무한루프 속에 가둬 둡니다 ㅎㅎ
            while (true) {
                // 새 요청이 들어오기 전까지는, 그러니까 정확히는 새 소켓 연결이 수립되기 전까지는
                // 아래 호출이 반환되지 않고 blocking 상태를 유지합니다.
                // 즉슨 아래 accept() 호출이 반환되었다면 새로운 소켓 연결이 수립되었다는 뜻입니다.
                // 이 새로운 소켓 연결은 HTTP 클라이언트에 의해 수립된 것입니다.
                // HTTP 클라이언트는 웹 브라우저일 수도 있고, curl같은 명령 도구일 수도 있습니다.
                // 앞으로 클라이언트와 통신을 주고받을 때에는 이 소켓을 사용합니다.
                Socket socket = serverSocket.accept();

                // 클라이언트로부터 데이터를 받아 읽을 때에는 inputReader를 사용하고,
                // 클라이언트로 데이터를 보낼 때에는 outputWriter를 사용합니다.
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                OutputStreamWriter outputWriter = new OutputStreamWriter(socket.getOutputStream());

                // HTTP 요청의 첫 줄은 "GET / HTTP/1.1" 같은 형식입니다.
                // 이 줄을 읽어서 요청 메서드와 요청 URI를 알아냅니다.
                String request = inputReader.readLine();
                String method = request.split(" ")[0];
                String uri = request.split(" ")[1];

                // 그 다음 줄부터는 헤더가 나옵니다.
                // 이 헤더들은 "Name: value" 형식으로 되어 있습니다.
                // 이들을 읽고 저장해둘 Map을 만들어 둡니다.
                Map<String, String> headers = new HashMap<>();

                // 이제 본격적으로 헤더를 읽어들입니다.
                // HTTP 메시지에서 헤더는 빈 줄이 나오기 전까지 읽으면 됩니다.
                // 각각의 헤더 필드는 줄바꿈으로 구분되어 있습니다.
                // 또한 헤더 필드 내에서 이름과 값은 ": "로 구분되어 있습니다.
                String headerField = inputReader.readLine();
                while (!headerField.isEmpty()) {
                    // 예를 들어 "Content-Type: text/plain"이라는 헤더 필드가 있다면,
                    // 이를 "Content-Type"이라는 이름과 "text/plain"이라는 값으로 나눌 수 있습니다.
                    String[] tokens = headerField.split(": ");
                    String name = tokens[0];
                    String value = tokens[1];
                    headers.put(name, value);

                    headerField = inputReader.readLine();
                }

                // 위 루프를 탈출했다면 그것은 헤더를 모두 받고 빈 줄을 만났다는 뜻입니다.
                // HTTP 메시지에서 헤더 필드가 연달아 나오고 이어서 빈 줄이 나오면 이는 본문(body)이 나온다는 뜻입니다.
                // 본문을 어디까지 읽어야 하는지는 아까 읽은 헤더 필드 중 "Content-Length"라는 이름의 필드에 적혀 있습니다.
                // 딱 그 만큼의 바이트 수를 읽어서 본문을 얻어냅니다.

                // 그런데 Content-Length 헤더 필드가 없을 수도 있습니다.
                // 그럴 경우에는 본문이 없다고(Content-Length: 0과 같음) 가정하고 처리합니다.
                int contentLength = Integer.parseInt(Optional.ofNullable(headers.get("Content-Length")).orElse("0"));

                // 이제 본문을 읽어볼까요!
                // 요청의 Content-Length만큼만, 즉 본문의 크기만큼만 읽어서 bodyBuffer에 저장합니다.
                // 물론 contentLength가 0이라면 아래 본문 읽기 루프는 실행되지 않습니다.
                ByteArrayOutputStream bodyBuffer = new ByteArrayOutputStream();
                for (int i = 0; i < contentLength; i++) {
                    bodyBuffer.write(inputReader.read());
                }

                // 여기까지 왔다면 이제 HTTP 요청을 모두 받은 것입니다.
                // 따라서 이 부분에 그 요청을 처리하는 코드를 배치하면 됩니다.
                // 이 예제에서는 별다른 처리 없이 바로 간단한 HTML 응답을 보내는 것으로 대신합니다.

                // 보낼 응답의 본문을 만듭니다.
                // 이 본문은 요청한 URI, 요청한 메서드, 요청 헤더, 요청 본문을 보여주는 HTML 문서입니다.
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

                // HTTP 응답 메시지를 작성하여 클라이언트에게 보냅니다.
                // 이 응답 메시지는 HTTP/1.1 200 OK 상태 코드와 text/html 컨텐트 타입을 가지며,
                // 본문의 길이는 응답 HTML의 바이트 수이며(스트링의 길이가 아닙니다ㅏ!) 이는 Content-Length 헤더에 담깁니다.
                outputWriter.write("HTTP/1.1 200 OK\r\n");
                outputWriter.write("Content-Type: text/html\r\n");
                outputWriter.write("Content-Length: " + responseBody.getBytes().length + "\r\n");

                // HTTP 메시지에서 헤더와 본문을 구분하는 빈 줄을 출력합니다.
                outputWriter.write("\r\n");

                // 그리고 본문을 출력합니다.
                outputWriter.write(responseBody);

                // 마지막으로 flush를 호출하여 버퍼에 남아있는 데이터를 모두 클라이언트에게 보냅니다.
                // 이 호출을 빼먹으면 응답이 클라이언트에게 전달되지 않을 수 있습니다.
                outputWriter.flush();

                // 요청을 받고 응답까지 보냈으니 소켓은 더이상 필요하지 않습니다.
                // 닫아 줍니다.
                // 이 시점 이후부터는 다시 다른 요청을 받아 처리할 수 있습니다.
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
