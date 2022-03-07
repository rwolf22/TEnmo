package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class UserController {

    private JdbcUserDao userDao;

    @Autowired
    public UserController(JdbcUserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    public String getUsername(@PathVariable int id) {
        return userDao.findUsernameById(id);
    }

    @RequestMapping(path = "/user/name/{username}", method = RequestMethod.GET)
    public int getIdByUsername(@PathVariable String username) {
        return userDao.findIdByUsername(username);
    }

    @RequestMapping(path = "/user/all", method = RequestMethod.GET)
    public List<User> list() {
        return userDao.findAll();
    }

}
