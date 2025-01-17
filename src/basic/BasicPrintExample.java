package basic;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

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
