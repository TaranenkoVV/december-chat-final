package ru.flamexander.december.chat.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ru.flamexander.december.chat.server.model.User;
import ru.flamexander.december.chat.server.repository.UsersRepository;
import ru.flamexander.december.chat.server.repository.UsersRepositoryImpl;

public class InJDBCUserService implements UserService {

    static final String ROLE_ADMIN = "ADMIN";
    static final String ROLE_USER = "USER";
    private List<User> users;
    private UsersRepository usersRepository;


    public InJDBCUserService(Connection connection) throws SQLException {

        usersRepository = new UsersRepositoryImpl(connection);

        //первичное наполнение БД
        User user = usersRepository.selectById("login1");
        if (user == null) {
            user = new User("login1", "pass1", "admin", ROLE_ADMIN);
            user = usersRepository.create(user);
        }

        user = usersRepository.selectById("login2");
        if (user == null) {
            user = new User("login2", "pass2", "user2", ROLE_USER);
            user = usersRepository.create(user);
        }

        user = usersRepository.selectById("login3");
        if (user == null) {
            user = new User("login3", "pass3", "user3", ROLE_USER);
            user = usersRepository.create(user);
        }

        this.users = new ArrayList<>(usersRepository.selectAll());
    }

    public UsersRepository getUsersRepository() {
        return usersRepository;
    }

    @Override
    public User getUserByUserName(String userName) {
        for (User u : users) {
            if (u.getUsername().equals(userName)) {
                return u;
            }
        }
        return null;
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
        newUser = usersRepository.create(newUser);
        users.add(newUser);
    }

    @Override
    public int updateUser(User user) {
        int affectedRows = usersRepository.update(user);
        return affectedRows;
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
