import java.net.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        try {
            ServerSocket serverSocket = new ServerSocket();

            serverSocket.bind(new InetSocketAddress("0.0.0.0", 8080));

            Socket socket = serverSocket.accept();

            DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            try {
                while (true) {
                    byte read = inputStream.readByte();
                    System.out.println(read);
                }
            } catch (EOFException e) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}