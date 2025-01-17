package basic;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;

/**
 * 소켓으로 들어온 데이터를 바이트 단위로 읽어서 그대로 돌려주는 예제입니다.
 * 이 예제를 실행하려면 Main 클래스의 main 메서드에서 new BasicCharEchoExample().run()을 호출하면 됩니다.
 */
public class BasicCharEchoExample {
    public void run() {
        System.out.println("Hello, World!");

        try {
            ServerSocket serverSocket = new ServerSocket();

            serverSocket.bind(new InetSocketAddress("0.0.0.0", 8080));

            Socket socket = serverSocket.accept();

            DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            OutputStream outputStream = socket.getOutputStream();

            try {
                while (true) {
                    byte read = inputStream.readByte();
                    outputStream.write(read);
                }
            } catch (EOFException e) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
