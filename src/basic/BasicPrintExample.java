package basic;

import java.net.Socket;
import java.io.IOException;
import java.io.EOFException;
import java.net.ServerSocket;
import java.io.DataInputStream;
import java.net.InetSocketAddress;
import java.io.BufferedInputStream;

/**
 * 소켓으로 들어온 데이터를 읽어서 콘솔에 출력하는 예제입니다.
 * 이 예제를 실행하려면 Main 클래스의 main 메서드에서 new BasicPrintExample().run()을 호출하면 됩니다.
 */
public class BasicPrintExample {
    public void run() {
        System.out.println("Hello, World!");

        try {
            ServerSocket serverSocket = new ServerSocket();

            serverSocket.bind(new InetSocketAddress("0.0.0.0", 8080));

            Socket socket = serverSocket.accept();

            DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            try {
                while (true) {
                    byte read = inputStream.readByte();
                    System.out.println((char) read);
                }
            } catch (EOFException e) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
