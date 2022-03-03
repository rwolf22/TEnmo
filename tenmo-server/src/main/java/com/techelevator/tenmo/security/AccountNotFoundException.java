package com.techelevator.tenmo.security;

public class AccountNotFoundException extends Exception{
    public AccountNotFoundException (String message) {
        super(message);
    }
}
