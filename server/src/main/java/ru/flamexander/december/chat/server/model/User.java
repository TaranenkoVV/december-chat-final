package ru.flamexander.december.chat.server.model;

import java.time.LocalDateTime;


/**
 * Класс, отражающий структуру хранимых в таблице полей.
 */
public class User {

    private String login;
    private String password;
    private String username;
    private String role;
    private LocalDateTime banexpirytime;
    private boolean permanentban;
    private int bancount;

    private LocalDateTime lastactivetime;
    private boolean active;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getBanexpirytime() {
        return banexpirytime;
    }

    public void setBanexpirytime(LocalDateTime banexpirytime) {
        this.banexpirytime = banexpirytime;
    }

    public boolean isPermanentban() {
        return permanentban;
    }

    public void setPermanentban(boolean permanentban) {
        this.permanentban = permanentban;
    }

    public int getBancount() {
        return bancount;
    }

    public void setBancount(int bancount) {
        this.bancount = bancount;
    }

    public LocalDateTime getLastactivetime() {
        return lastactivetime;
    }

    public void setLastactivetime(LocalDateTime lastactivetime) {
        this.lastactivetime = lastactivetime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public User(String login, String password, String username, String role) {
        this.login = login;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    public User(String login, String password, String username, String role, LocalDateTime banexpirytime,
            boolean permanentban, int bancount, LocalDateTime lastactivetime, boolean active) {
        this.login = login;
        this.password = password;
        this.username = username;
        this.role = role;
        this.banexpirytime = banexpirytime;
        this.permanentban = permanentban;
        this.bancount = bancount;
        this.lastactivetime = lastactivetime;
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", banexpirytime=" + banexpirytime +
                ", permanentban=" + permanentban +
                ", bancount=" + bancount +
                ", lastactivetime=" + lastactivetime +
                ", active=" + active +
                '}';
    }
}
