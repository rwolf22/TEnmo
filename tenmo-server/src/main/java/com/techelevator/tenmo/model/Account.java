package com.techelevator.tenmo.model;

import javax.validation.constraints.NotNull;

public class Account {

    @NotNull
    private int accountId;
    @NotNull
    private int userId;
    @NotNull
    private double balance;

    public Account() {
    }

    public Account(int accountId, int userId, double balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
