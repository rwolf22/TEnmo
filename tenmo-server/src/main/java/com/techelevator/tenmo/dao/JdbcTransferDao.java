package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.List;

public class JdbcTransferDao {

    private JdbcTemplate jdbcTemplate;

    public boolean create(int transferType, int fromAccount, int toAccount, double amount){
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?);";
        int newTransferId;
        try {
            newTransferId = jdbcTemplate.queryForObject(sql, Integer.class, transferType, 1, fromAccount, toAccount, amount);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    public boolean completeTransfer(int transferId) {
        String sql = "SELECT account_from, account_to, amount " +
                "FROM transfers " +
                "WHERE transfer_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
        int fromId = rowSet.getInt("account_from");
        int toId = rowSet.getInt("account_to");
        double amount = rowSet.getDouble("amount");
        String getFromBalanceSql = "SELECT balance " +
                "FROM accounts " +
                "WHERE account_id = ?;";
        SqlRowSet rowSet1 = jdbcTemplate.queryForRowSet(getFromBalanceSql, fromId);
        double fromBalance = rowSet1.getDouble("balance");
        String getToBalanceSql = "SELECT balance " +
                "FROM accounts " +
                "WHERE account_id = ?;";
        SqlRowSet rowSet2 = jdbcTemplate.queryForRowSet(getToBalanceSql, toId);
        double toBalance = rowSet2.getDouble("balance");
        if (fromBalance >= amount) {
            String updateFromAccountSql = "UPDATE accounts " +
                    "SET balance = ? " +
                    "WHERE account_id = ?;";
            int updatedFromRows = jdbcTemplate.update(updateFromAccountSql, fromBalance - amount, fromId);
            String updateToAccountSql = "UPDATE accounts " +
                    "SET balance = ? " +
                    "WHERE account_id = ?;";
            int updatedToRows = jdbcTemplate.update(updateToAccountSql, toBalance + amount, toId);
            if (updatedToRows == 1 && updatedFromRows == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public List<Transfer> list(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM transfers " +
                "WHERE account_from = ? OR account_to = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while (rowSet.next()) {
            Transfer transfer = mapRowToTransfer(rowSet);
            transfers.add(transfer);
        }
        return transfers;
    }

    public Transfer getById(int transferId) {
        String sql = "Select * " +
                "FROM transfers " +
                "JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id " +
                "JOIN transfer_statuses ON transfers.transfer_status_id = transfer_statuses.transfer_status_id " +
                "WHERE transfer_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
        return mapRowToTransfer(rowSet);
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getDouble("amount"));
        return transfer;
    }
}
