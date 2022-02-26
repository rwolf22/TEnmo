package com.techelevator.tenmo.model;

public class Account {

    private int userId;
    private double balance;
    private int accountId;

    public Account() {
    }

    public Account(int userId, int accountId) {
        this.userId = userId;
        this.accountId = accountId;
        this.balance = 1000.0;
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

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
