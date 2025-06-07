
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RandomNumberServer {
    private static final int PORT = 12345;
    private static List<PrintWriter> clientOutputStreams;

    public static void main(String[] args) {
        clientOutputStreams = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("服务器已启动，等待客户端连接...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread clientHandler = new Thread(new ClientHandler(clientSocket));
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Scanner input;

        public ClientHandler(Socket clientSocket) {
            try {
                input = new Scanner(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            while (input.hasNextLine()) {
                message = input.nextLine();
                if (message.equals("GENERATE")) {
                    Random random = new Random();
                    int randomNumber = random.nextInt(101);
                    if (randomNumber < 100) {
                        broadcastMessage("0-99随机数: " + randomNumber);
                    } else {
                        broadcastMessage("100-200随机数: " + randomNumber);
                    }
                }
            }
        }

        private void broadcastMessage(String message) {
            for (PrintWriter writer : clientOutputStreams) {
                writer.println(message);
                writer.flush();
            }
        }
    }
}
