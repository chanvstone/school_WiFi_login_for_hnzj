package com.sio2.login.account;

import java.io.Serializable;

public class Account implements Serializable {
    private String username;
    private String password;
    private String provider;

    public Account(String username, String password, String provider) {
        this.username = username;
        this.password = password;
        this.provider = provider;
    }

    public Account() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isValuable() {
        return username != null && !username.equals("") && password != null && !password.equals("") && provider != null && !provider.equals("");
    }

}
