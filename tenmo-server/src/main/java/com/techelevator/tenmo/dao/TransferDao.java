package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    boolean create(Transfer transfer);

    boolean completeTransfer(int transferId);

    List<Transfer> list(int accountId);

    Transfer getById(int transferId);

}
