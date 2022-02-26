package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.security.AccountNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class UserController {

    private JdbcAccountDao accountDao;

    public UserController(JdbcAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/user/{id}/account", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public double getAccountBalance(@PathVariable int id) throws AccountNotFoundException {
        return accountDao.getBalance(id);
    }
}
