package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private JdbcTransferDao transferDao;

    public TransferController() {
    }

    public TransferController(JdbcTransferDao transferDao) {
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfers", method = RequestMethod.POST)
    public boolean createTransfer(@PathVariable("id") int id, @PathVariable("accountId") int accountId, @RequestBody Transfer transfer) {
        return transferDao.create(transfer);
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfers/{transferId}", method = RequestMethod.PUT)
    public boolean completeTransfer(@PathVariable int id, @PathVariable int accountId, @PathVariable int transferId) {
        return transferDao.completeTransfer(transferId);
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfers", method = RequestMethod.GET)
    public List<Transfer> getByAccount(@PathVariable int id, @PathVariable int accountId) {
        return transferDao.list(accountId);
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfers/{transferId)", method = RequestMethod.GET)
    public Transfer getById(@PathVariable int id, @PathVariable int accountId, @PathVariable int transferId) {
        return transferDao.getById(transferId);
    }
}
