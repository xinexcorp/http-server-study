package basic;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
