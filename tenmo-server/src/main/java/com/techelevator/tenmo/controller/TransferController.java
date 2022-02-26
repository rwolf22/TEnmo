package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private JdbcTransferDao transferDao;

    public TransferController(JdbcTransferDao transferDao) {
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfer", method = RequestMethod.POST)
    public boolean createTransfer(@PathVariable int id, @PathVariable int accountId, @RequestBody int transferType, @RequestBody int toAccount, @RequestBody double amount) {
        return transferDao.create(transferType, accountId, toAccount, amount);
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfer/{transferId}", method = RequestMethod.PUT)
    public boolean completeTransfer(@PathVariable int id, @PathVariable int accountId, @PathVariable int transferId) {
        return transferDao.completeTransfer(transferId);
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfer", method = RequestMethod.GET)
    public List<Transfer> getbyAccount(@PathVariable int id, @PathVariable int accountId) {
        return transferDao.list(accountId);
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfer/{transferId)", method = RequestMethod.GET)
    public Transfer getById(@PathVariable int id, @PathVariable int accountId, @PathVariable int transferId) {
        return transferDao.getById(transferId);
    }
}
