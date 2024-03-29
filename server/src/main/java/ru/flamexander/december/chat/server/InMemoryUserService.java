package ru.flamexander.december.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.flamexander.december.chat.server.model.User;

public class InMemoryUserService implements UserService {

    static final String ROLE_ADMIN = "ADMIN";
    static final String ROLE_USER = "USER";

    private List<User> users;

    public InMemoryUserService() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("login1", "pass1", "admin", ROLE_ADMIN),
                new User("login2", "pass2", "user2", ROLE_USER),
                new User("login3", "pass3", "user3", ROLE_USER)
        ));
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.getLogin().equals(login) && u.getPassword().equals(password)) {
                return u.getUsername();
            }
        }
        return null;
    }

    @Override
    public void createNewUser(String login, String password, String username) {
        User newUser = new User(login, password, username, ROLE_USER);
        users.add(newUser);
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (User u : users) {
            if (u.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUsernameAlreadyExist(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUserAdmin(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getRole().equals(ROLE_ADMIN)) {
                return true;
            }
        }
        return false;
    }
}
