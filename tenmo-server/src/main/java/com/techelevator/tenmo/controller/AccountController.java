package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.security.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private JdbcAccountDao accountDao;

    @Autowired
    public AccountController(JdbcAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "users/{userId}/accounts/{accountId}", method = RequestMethod.GET)
    public double getAccountBalance(@PathVariable("userId") int userId, @PathVariable("accountId") int accountId) throws com.techelevator.tenmo.security.AccountNotFoundException {
        return accountDao.getBalance(userId, accountId);
    }

    @RequestMapping(path = "users/{userId}/accounts", method = RequestMethod.GET)
    public int getAccountIdByUserId(@PathVariable int userId) throws com.techelevator.tenmo.security.AccountNotFoundException {
        return accountDao.getIdByUserId(userId);
    }

    @RequestMapping(path = "/users/{userId}/account", method = RequestMethod.GET)
    public Account getAccountByUser(@PathVariable int userId) {
        return accountDao.getAccount(userId);
    }

    @RequestMapping(path = "/users/accounts/{id}", method = RequestMethod.GET)
    public int getUserIdFromAccountId(@PathVariable int id) throws AccountNotFoundException {
        return accountDao.getUserIdByAccountId(id);
    }
}
