package ru.flamexander.december.chat.server.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ru.flamexander.december.chat.server.model.User;

public class UsersRepositoryImpl implements UsersRepository {

    private Connection connection;
    private Statement statement;


    /**
     * Конструктор.
     *
     * @throws SQLException исключение
     */
    public UsersRepositoryImpl(Connection connection) {

        try {
            this.connection = connection;
            this.statement = connection.createStatement();
            createTable();
            prepareStatements();
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Подготовка всех PreparedStatement'ов
     *
     * @throws SQLException <code>SQLException</code>
     */
    public void prepareStatements() throws SQLException {
        PreparedStatement psInsert = this.connection.prepareStatement("insert into students (name, score, created_at) values (?, ?, ?);");
    }


    private void dropTable() throws SQLException {
        statement.executeUpdate("drop table if exists users;");
    }

    private void createTable() throws SQLException {
        statement.executeUpdate(
                "" +
                        "create table if not exists public.user (" +
                        "    login       varchar(255)," +
                        "    password    varchar(255)," +
                        "    username    varchar(255)," +
                        "    role        varchar(255)," +
                        "    bancount    int4 NULL," +
                        "    banexpirytime timestamp NULL," +
                        "    permanentban bool NULL," +
                        "    lastactivetime timestamp NULL," +
                        "    active bool NULL," +
                        "    CONSTRAINT user_pk PRIMARY KEY (login)" +
                        ")");
    }

    /**
     * Создание записи в БД.
     *
     * @param user - заполненный объект
     * @return
     */
    @Override
    public User create(User user) {

        String sqlIns = "insert into public.user "
                + "(login, password, username, role) values (?, ?, ?, ?);";
        try (PreparedStatement statementIns = connection.prepareStatement(sqlIns)) {
            statementIns.setObject(1, user.getLogin());
            statementIns.setString(2, user.getPassword());
            statementIns.setString(3, user.getUsername());
            statementIns.setObject(4, user.getRole());
            statementIns.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Получение записи по login из БД.
     *
     * @param login идентификатор записи
     * @return запись
     */
    @Override
    public User selectById(String login) {
        User user = null;

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM public.user WHERE login=?;")) {
            statement.setObject(1, login);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                //String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                String userName = resultSet.getString("username");
                String role = resultSet.getString("role");

                user = new User(login, password, userName, role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Получение всех записей из БД.
     *
     * @return записи
     */
    @Override
    public List<User> selectAll() {
        List<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM public.user;")) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                String userName = resultSet.getString("username");
                String role = resultSet.getString("role");

                User user = new User(login, password, userName, role);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Обновление записи в БД.
     *
     * @param user изменяемая запись
     * @return boolean
     */
    @Override
    public int update(User user) {
        int affectedRows = 0;
        if (user.getLogin() == null) {
            return 0;
        }
        //update
        String sqlUpd = "UPDATE public.user SET password=?, username=?, role=?, banexpirytime=?, permanentban=?, bancount=?, lastactivetime=?, active=? WHERE login = ?;";

        try (PreparedStatement statementUpd = connection.prepareStatement(sqlUpd)) {
            statementUpd.setString(1, user.getPassword());
            statementUpd.setString(2, user.getUsername());
            statementUpd.setString(3, user.getRole());
            statementUpd.setObject(4, user.getBanexpirytime());
            statementUpd.setBoolean(5, user.isPermanentban());
            statementUpd.setInt(6, user.getBancount());
            statementUpd.setObject(7, user.getLastactivetime());
            statementUpd.setBoolean(8, user.isActive());
            statementUpd.setString(9, user.getLogin());
            affectedRows = statementUpd.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return affectedRows;
    }

    /**
     * Удаление указанных записей по login.
     *
     * @param loginList@return количество удаленных записей
     */
    @Override
    public int remove(List<String> loginList) {
        int affectedRows = 0;
        if (loginList.size() == 0) {
            return 0;
        }
        //удаление в цикле по одной записи
        String sqlDel2 = "DELETE FROM public.user WHERE login = ?;";
        try (PreparedStatement statementDel = connection.prepareStatement(sqlDel2)) {
            for (int i = 0; i < loginList.size(); i++) {
                statementDel.setString(1, loginList.get(i));
                affectedRows += statementDel.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        return affectedRows;
    }
}
