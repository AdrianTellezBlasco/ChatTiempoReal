package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static String username;
    private static Socket socket;
    private static BufferedReader inputStream;
    private static PrintWriter outputStream;
    private static Scanner sc;
    
    
    public static void main(String[] args) {
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        sc = new Scanner(System.in);
        System.out.print("Ingrese su nombre de usuario: ");
        username = sc.nextLine();
        System.out.print("Ingresa la dirección IP del servidor al que deseas conectarte:");
        String ip = sc.nextLine();
        System.out.print("Ingresa el puerto del servidor:");
        int port = sc.nextInt();
        sc.nextLine();
        


        connectToServer(ip, port);

        Thread messageReceiverThread = new Thread(() -> {
            while (true) {
                String message = receiveMessage();
                if (message != null) {
                    System.out.println(message);
                }
            }
        });
        messageReceiverThread.start();

        try {
            while (true) {
                String message = consoleInput.readLine();
                sendCommand(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            sc.close();
            System.exit(2);
        } finally {
            try {
                consoleInput.close();
                messageReceiverThread.interrupt();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                sc.close();
                System.exit(2);
            }
        }
    }

    public static void connectToServer(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            outputStream.println(username);
        } catch (IOException e) {
            e.printStackTrace();
            sc.close();
            System.exit(2);
        }
    }

    public static void sendCommand(String command) {
        outputStream.println(command);
    }

    public static String receiveMessage() {
        try {
            return inputStream.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            sc.close();
            System.exit(2);
            return null;
        }
    }
}