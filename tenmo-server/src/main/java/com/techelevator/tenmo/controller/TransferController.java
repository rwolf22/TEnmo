package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private JdbcTransferDao transferDao;

    @Autowired
    public TransferController(JdbcTransferDao transferDao) {
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfers", method = RequestMethod.POST)
    public Transfer createTransfer(@PathVariable("id") int id, @PathVariable("accountId") int accountId, @RequestBody Transfer transfer) {
        return transferDao.create(transfer);
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfers/{transferId}/approve", method = RequestMethod.PUT)
    public Transfer approveTransfer(@PathVariable("id") int id, @PathVariable("accountId") int accountId, @PathVariable("transferId") int transferId, @RequestBody Transfer transfer) {
        return transferDao.approveTransfer(transfer);
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfers/{transferId}/reject", method = RequestMethod.PUT)
    public String rejectTransfer(@PathVariable("id") int id, @PathVariable("accountId") int accountId, @PathVariable("transferId") int transferId) {
        return transferDao.rejectTransfer(transferId);
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfers", method = RequestMethod.GET)
    public List<Transfer> getByAccount(@PathVariable int id, @PathVariable int accountId) {
        return transferDao.list(accountId);
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfers/{transferId}", method = RequestMethod.GET)
    public Transfer getById(@PathVariable int id, @PathVariable int accountId, @PathVariable int transferId) {
        return transferDao.getById(transferId);
    }

    @RequestMapping(path = "/user/{id}/accounts/{accountId}/transfers/pending", method = RequestMethod.GET)
    public List<Transfer> getPending(@PathVariable("id") int userId, @PathVariable("accountId") int accountId) {
        return transferDao.getPending(accountId);
    }

    @RequestMapping(path = "/transfers/type/{typeId}", method = RequestMethod.GET)
    public String getTypeDesc(@PathVariable int typeId) {
        return transferDao.getType(typeId);
    }

    @RequestMapping(path = "/transfers/status/{statusId}", method = RequestMethod.GET)
    public String getStatusDesc(@PathVariable int statusId) {
        return transferDao.getStatus(statusId);
    }
}
