package me.liuyun.bjutlgn.entity;


public class User {
    private int id;
    private String account;
    private String password;
    private int pack;

    public User(int id, String account, String password, int pack) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.pack = pack;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPack(int pack) {
        this.pack = pack;
    }

    public int getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public int getPack() {
        return pack;
    }
}
