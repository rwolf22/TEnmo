package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class AccountController {

    private JdbcAccountDao accountDao;

    public AccountController() {
    }

    public AccountController(JdbcAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "users/{userId}/accounts/{accountId}", method = RequestMethod.GET)
    public double getAccountBalance(@PathVariable("userId") int userId, @PathVariable("accountId") int accountId) throws com.techelevator.tenmo.security.AccountNotFoundException {
        return accountDao.getBalance(userId, accountId);
    }

    @RequestMapping(path = "users/{userId}/accounts", method = RequestMethod.GET)
    public int getAccountByUserId(@PathVariable int userId) throws com.techelevator.tenmo.security.AccountNotFoundException {
        return accountDao.getIdByUserId(userId);
    }
}
