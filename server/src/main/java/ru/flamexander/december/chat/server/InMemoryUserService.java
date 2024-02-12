package ru.flamexander.december.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryUserService implements UserService {
    class User {
        static final String ROLE_ADMIN = "ADMIN";
        static final String ROLE_USER = "USER";
        private String login;
        private String password;
        private String username;
        private String role;

        public User(String login, String password, String username, String role) {
            this.login = login;
            this.password = password;
            this.username = username;
            this.role = role;
        }
    }

    private List<User> users;

    public InMemoryUserService() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("login1", "pass1", "admin", User.ROLE_ADMIN),
                new User("login2", "pass2", "user2", User.ROLE_USER),
                new User("login3", "pass3", "user3", User.ROLE_USER)
        ));
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.username;
            }
        }
        return null;
    }

    @Override
    public void createNewUser(String login, String password, String username) {
        users.add(new User(login, password, username, User.ROLE_USER));
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (User u : users) {
            if (u.login.equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUsernameAlreadyExist(String username) {
        for (User u : users) {
            if (u.username.equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUserAdmin(String username) {
        for (User u : users) {
            if (u.username.equals(username) && u.role.equals(User.ROLE_ADMIN) ) {
                return true;
            }
        }
        return false;
    }
}
