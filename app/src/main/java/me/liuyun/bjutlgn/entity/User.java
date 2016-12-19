package me.liuyun.bjutlgn.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(generatedId = true) int id;
    @DatabaseField String account;
    @DatabaseField String password;
    @DatabaseField int pack;
    @DatabaseField int position;

    public User(int id, String account, String password, int pack, int position) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.pack = pack;
        this.position = position;
    }

    public User() {
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
