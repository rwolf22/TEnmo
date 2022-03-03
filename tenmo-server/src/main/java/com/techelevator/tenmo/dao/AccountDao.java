package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.security.AccountNotFoundException;

public interface AccountDao {

    double getBalance(int userId, int accountId) throws AccountNotFoundException;

    int getIdByUserId(int userId) throws AccountNotFoundException;
}
