package ru.flamexander.december.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Сервер запущен на порту %d. Ожидание подключения клиентов\n", port);
            userService = new InMemoryUserService();
            System.out.println("Запущен сервис для работы с пользователями");
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    new ClientHandler(this, socket);
                } catch (IOException e) {
                    System.out.println("Не удалось подключить клиента");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("Подключился новый клиент " + clientHandler.getUsername());
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Отключился клиент " + clientHandler.getUsername());
    }

    public synchronized boolean isUserBusy(String username) {
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUsername, String message) {
        // TODO homework chat part 1
        boolean receiverNotFound = true;
        for (ClientHandler clientHandler : clients) {
            if(clientHandler.getUsername().equals(receiverUsername)) {
                clientHandler.sendMessage("Private Message from " + sender.getUsername() + " to " + receiverUsername + ": " + message);
                sender.sendMessage("Private Message from " + sender.getUsername() + " to " + receiverUsername + ": " + message);
                receiverNotFound = false;
            }
        }

        if (receiverNotFound) {
            sender.sendMessage("User: " + receiverUsername + " not found");
        }
    }

    public synchronized boolean kickUser(ClientHandler sender, String kickUsername) {
        // TODO homework chat part 2
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(kickUsername)) {

                clientHandler.sendMessage("СЕРВЕР: администратор отключил вас из чата");
                clientHandler.sendMessage("/kickedout");

                clients.remove(clientHandler);
                unsubscribe(clientHandler);

                return true;
            }
        }
        sender.sendMessage("Пользователь " + kickUsername + " не найден");
        return false;
    }
}
