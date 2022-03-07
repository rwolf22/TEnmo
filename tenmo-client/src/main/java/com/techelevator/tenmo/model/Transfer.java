package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.services.UserService;

import java.text.NumberFormat;

public class Transfer {

//    @JsonProperty("transfer_id")
    private int transferId;
//    @JsonProperty("transfer_type_id")
    private int transferTypeId;
//    @JsonProperty("transfer_status_id")
    private int transferStatusId;
    private int accountFrom;
    private int accountTo;
    private double amount;

    public Transfer() {
    }

    public Transfer(int transferId, int transferTypeId, int transferStatusId, int accountFrom, int accountTo, double amount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public Transfer(int transferTypeId, int transferStatusId, int accountFrom, int accountTo, double amount) {
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String toString(String token) {
        UserService userService = new UserService("http://localhost:8080/");
        TransferService transferService = new TransferService("http://localhost:8080/");
        AccountService accountService = new AccountService("http://localhost:8080/");
        userService.setAuthToken(token);
        transferService.setAuthToken(token);
        accountService.setAuthToken(token);
        return "Id: " + this.transferId + "\n" +
                "From: " + userService.getUsername(accountService.getUserIdByAccountId(accountFrom)) + "\n" +
                "To: " + userService.getUsername(accountService.getUserIdByAccountId(accountTo)) + "\n" +
                "Type: " + transferService.getTypeDesc(this.transferTypeId) + "\n" +
                "Status: " + transferService.getStatusDesc(this.transferStatusId) + "\n" +
                "Amount: " + NumberFormat.getCurrencyInstance().format(this.amount);
    }
}
