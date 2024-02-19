package ru.flamexander.december.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ru.flamexander.december.chat.server.model.User;

public class Server {

    private int port;
    private Connection connection;
    private List<ClientHandler> clients;
    private UserService userService;

    private boolean serverActive;

    public void setServerActive(boolean serverActive) {
        this.serverActive = serverActive;
    }

    public UserService getUserService() {
        return userService;
    }

    public Server(int port, Connection connection) {
        this.port = port;
        this.connection = connection;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Сервер запущен на порту %d. Ожидание подключения клиентов\n", port);

            userService = new InJDBCUserService(connection);

            System.out.println("Запущен сервис для работы с пользователями");
            serverActive = true;
            while (serverActive) {
                Socket socket = serverSocket.accept();
                try {
                    new ClientHandler(this, socket);
                } catch (IOException e) {
                    System.out.println("Не удалось подключить клиента");
                }
            }
        } catch (IOException | SQLException e) {
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

    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUsername, String message) {
        // TODO homework chat part 1
        boolean receiverNotFound = true;
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(receiverUsername)) {
                clientHandler.sendMessage(
                        "Private Message from " + sender.getUsername() + " to " + receiverUsername + ": " + message);
                sender.sendMessage(
                        "Private Message from " + sender.getUsername() + " to " + receiverUsername + ": " + message);
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

    public synchronized boolean banUser(ClientHandler sender, String banUserName, Integer timeOff) {
        // бан пользователя
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(banUserName)) {

                User user = userService.getUserByUserName(banUserName);

                LocalDateTime currentTime = LocalDateTime.now();
                LocalDateTime expiryTime = currentTime.plusMinutes(timeOff);

                user.setBanexpirytime(expiryTime);
                int affectedRows = userService.updateUser(user);
                if (affectedRows == 0) {
                    return false;
                } else {
                    clientHandler.sendMessage("СЕРВЕР: администратор отключил вас из чата на " + timeOff + " минут.");
                    clientHandler.sendMessage("/ban");
                    clients.remove(clientHandler);
                    unsubscribe(clientHandler);
                }
                return true;
            }
        }
        sender.sendMessage("Пользователь " + banUserName + " не найден");
        return false;
    }

    public synchronized boolean isUserBusy(String username) {
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean changeUserName(ClientHandler currentUser, String newUserName) {

        String currentUserName = currentUser.getUsername();
        User user = userService.getUserByUserName(currentUserName);
        if (user == null) {
            currentUser.sendMessage("СЕРВЕР: текущий пользователь с именем ' + currentUserName + ' не найден");
            return false;
        }
        if (isUserBusy(newUserName)) {
            currentUser.sendMessage("СЕРВЕР: имя уже занято: " + newUserName);
            return false;
        }

        user.setUsername(newUserName);
        int affectedRows = userService.updateUser(user);
        if (affectedRows == 0) {
            return false;
        }

        clients.remove(currentUser);
        currentUser.setUsername(newUserName);
        clients.add(currentUser);
        return true;
    }

    public synchronized List<String> getActiveUsers() {
        List<String> activeUsers = new ArrayList<>();
        for (ClientHandler c : clients) {
            //if (c.getUsername().active) {
            activeUsers.add(c.getUsername());
            //}
        }
        return activeUsers;
    }
}
