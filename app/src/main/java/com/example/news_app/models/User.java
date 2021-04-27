package com.example.news_app.models;

public class User {

    private int id;
    private String name;
    private String login;
    private String password;
    private String history;
    private String themes;

    public User(int id, String name, String login, String password, String history, String themes) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.password = password;
        this.history = history;
        this.themes = themes;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getThemes() {
        return themes;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }
}
