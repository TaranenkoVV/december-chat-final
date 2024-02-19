package ru.flamexander.december.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplication {

    static String username;
    private static boolean isThreadActive;
    private static boolean isMainActive;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 8189);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Подключились к серверу");
            Scanner scanner = new Scanner(System.in);
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF();
                        if (message.startsWith("/")) {
                            if (message.startsWith("/authok ")) {
                                username = message.split(" ")[1];
                                isThreadActive = true;
                                break;
                            }
                        }
                        System.out.println(message);
                    }
                    boolean inter = false;
                    while (isThreadActive) {
                        if (Thread.currentThread().isInterrupted() || inter) {
                            break;
                        }
                        String message = in.readUTF();

                        if (Thread.currentThread().isInterrupted() || inter) {
                            break;
                        }

                        if (message.equals("/kickedout")
                                || message.equals("/ban")
                                || message.equals("/shutdown")) {
                            isThreadActive = false;
                            isMainActive = false;
                            Thread.currentThread().interrupt();
                            continue;
                        }
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();

            isMainActive = true;
            while (isMainActive) {
                String message = scanner.nextLine();
                if (isMainActive) {
                    out.writeUTF(message);
                    if (message.equals("/exit")) {
                        isMainActive = false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
