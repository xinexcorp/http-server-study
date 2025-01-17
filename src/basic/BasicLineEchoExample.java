package basic;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 소켓으로 들어온 데이터를 줄 단위로 읽어서 그대로 돌려주는 예제입니다.
 * BasicCharEchoExample과 다르게, 한 줄이 끝나기 전까지는 읽은 데이터를 돌려주지 않습니다.
 * @see BasicCharEchoExample 한번 비교해보셔용
 * 이 예제를 실행하려면 Main 클래스의 main 메서드에서 new BasicLineEchoExample().run()을 호출하면 됩니다.
 */
public class BasicLineEchoExample {
    public void run() {
        System.out.println("Hello, World!");

        try {
            ServerSocket serverSocket = new ServerSocket();

            serverSocket.bind(new InetSocketAddress("0.0.0.0", 8080));

            Socket socket = serverSocket.accept();

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            OutputStreamWriter outputWriter = new OutputStreamWriter(socket.getOutputStream());

            try {
                while (true) {
                    String line = inputReader.readLine();
                    outputWriter.write(line + "\r\n");
                    outputWriter.flush();
                }
            } catch (EOFException e) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
