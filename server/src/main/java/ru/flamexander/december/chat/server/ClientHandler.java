package ru.flamexander.december.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ClientHandler {

    private Server server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                authentication();
                listenUserChatMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    private void listenUserChatMessages() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith("/")) {

                if (message.equals("/exit")) {
                    break;
                }

                if (message.startsWith("/w ")) {
                    // TODO homework chat part 1
                    String[] parts = new String[3];
                    parts = message.split(" ", 3);
                    if (parts.length == 3) {
                        String receiverUsername = parts[1].trim();
                        String userMessage = parts[2];
                        server.sendPrivateMessage(this, receiverUsername, userMessage);
                    }
                    continue;
                }

                if (message.startsWith("/kick ")) {
                    // TODO homework chat part 2
                    if (server.getUserService().isUserAdmin(username)) {
                        String[] parts = new String[2];
                        parts = message.split(" ", 2);
                        if (parts.length == 2) {
                            String kickUsername = parts[1].trim();
                            if (server.kickUser(this, kickUsername)) {
                                server.broadcastMessage("Пользователь " + kickUsername + " отключен администратором");
                            }
                        }
                    } else {
                        sendMessage("СЕРВЕР: у вас недостаточно прав для отключения пользователей");
                    }
                    continue;
                }

                if (message.startsWith("/ban ")) {
                    // бан пользователя
                    if (server.getUserService().isUserAdmin(username)) {
                        String[] parts = new String[3];
                        parts = message.split(" ", 3);
                        if (parts.length == 3) {
                            String banUsername = parts[1].trim();
                            Integer timeOff = Integer.parseInt(parts[2].trim());
                            if (timeOff > 0 && server.banUser(this, banUsername, timeOff)) {
                                server.broadcastMessage("Пользователь " + banUsername
                                        + " отключен администратором на " + timeOff + " минут.");
                            }
                        }
                    } else {
                        sendMessage("СЕРВЕР: у вас недостаточно прав для отключения пользователей");
                    }
                    continue;
                }

                if (message.startsWith("/changenick ")) {
                    // смена ника пользователя

                    String[] parts = new String[2];
                    parts = message.split(" ", 2);
                    if (parts.length == 2) {
                        String newUsername = parts[1].trim();
                        String oldUsername = this.getUsername();
                        if (server.changeUserName(this, newUsername)) {
                            server.broadcastMessage(
                                    "СЕРВЕР: Пользователь " + oldUsername + " изменил свой ник на: " + newUsername);
                        }
                    }
                    continue;
                }

                if (message.startsWith("/activelist")) {
                    // список активных пользователей
                    List<String> activeUsers = server.getActiveUsers();
                    sendMessage("СЕРВЕР: список активных клиентов " + activeUsers.toString());
                    continue;
                }

                if (message.startsWith("/shutdown")) {
                    // остановка сервера (для админа)
                    if (server.getUserService().isUserAdmin(username)) {
                        server.broadcastMessage("СЕРВЕР остановлен администратором");
                        server.broadcastMessage("/shutdown");
                        server.setServerActive(false);
                        break;
                    } else {
                        sendMessage("СЕРВЕР: у вас недостаточно прав для остановки сервера");
                    }
                    continue;
                }

                server.broadcastMessage(username + ": " + message);
            }
        }
    }

    public void sendMessage(String message) {
        try {
            String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
            String messageWithTime = timeStamp + '\n' + message;
            out.writeUTF(messageWithTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean tryToAuthenticate(String message) {
        String[] elements = message.split(" "); // /auth login1 pass1
        if (elements.length != 3) {
            sendMessage("СЕРВЕР: некорректная команда аутентификации");
            return false;
        }
        String login = elements[1];
        String password = elements[2];
        String usernameFromUserService = server.getUserService().getUsernameByLoginAndPassword(login, password);
        if (usernameFromUserService == null) {
            sendMessage("СЕРВЕР: пользователя с указанным логин/паролем не существует");
            return false;
        }
        if (server.isUserBusy(usernameFromUserService)) {
            sendMessage("СЕРВЕР: учетная запись уже занята");
            return false;
        }
        username = usernameFromUserService;
        server.subscribe(this);
        sendMessage("/authok " + username);
        sendMessage("СЕРВЕР: " + username + ", добро пожаловать в чат!");
        return true;
    }

    private boolean register(String message) {
        String[] elements = message.split(" "); // /auth login1 pass1 user1
        if (elements.length != 4) {
            sendMessage("СЕРВЕР: некорректная команда аутентификации");
            return false;
        }
        String login = elements[1];
        String password = elements[2];
        String registrationUsername = elements[3];
        if (server.getUserService().isLoginAlreadyExist(login)) {
            sendMessage("СЕРВЕР: указанный login уже занят");
            return false;
        }
        if (server.getUserService().isUsernameAlreadyExist(registrationUsername)) {
            sendMessage("СЕРВЕР: указанное имя пользователя уже занято");
            return false;
        }
        server.getUserService().createNewUser(login, password, registrationUsername);
        username = registrationUsername;
        sendMessage("/authok " + username);
        sendMessage("СЕРВЕР: " + username + ", вы успешно прошли регистрацию, добро пожаловать в чат!");
        server.subscribe(this);
        return true;
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            boolean isSucceed = false;
            if (message.startsWith("/auth ")) {
                isSucceed = tryToAuthenticate(message);
            } else if (message.startsWith("/register ")) {
                isSucceed = register(message);
            } else {
                sendMessage("СЕРВЕР: требуется войти в учетную запись или зарегистрироваться");
            }
            if (isSucceed) {
                break;
            }
        }
    }
}