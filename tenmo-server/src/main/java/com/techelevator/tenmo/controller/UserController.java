package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("isAuthenticated()")
public class UserController {

    private JdbcUserDao userDao;

    public UserController() {
    }

    public UserController(JdbcUserDao userDao) {
        this.userDao = userDao;
    }


}
