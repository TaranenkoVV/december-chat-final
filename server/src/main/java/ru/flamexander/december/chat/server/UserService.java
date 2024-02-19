package ru.flamexander.december.chat.server;

import ru.flamexander.december.chat.server.model.User;

public interface UserService {

    String getUsernameByLoginAndPassword(String login, String password);

    User getUserByUserName(String userName);

    void createNewUser(String login, String password, String username);

    int updateUser(User user);

    boolean isLoginAlreadyExist(String login);

    boolean isUsernameAlreadyExist(String username);

    boolean isUserAdmin(String username);

    boolean isLoginBanned(String login);
}