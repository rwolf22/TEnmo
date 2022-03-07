package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer create(Transfer transfer) {
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?)" +
                "RETURNING transfer_id;";
        int newTransferId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        transfer.setTransferId(newTransferId);
        return transfer;
    }

    @Override
    public Transfer approveTransfer(Transfer transfer) {
        Transfer result = transfer;
        String getFromBalanceSql = "SELECT balance " +
                "FROM accounts " +
                "WHERE account_id = ?;";
        SqlRowSet rowSet1 = jdbcTemplate.queryForRowSet(getFromBalanceSql, result.getAccountFrom());
        double fromBalance = 0;
        while (rowSet1.next()) {
            fromBalance = rowSet1.getDouble("balance");
        }
        String getToBalanceSql = "SELECT balance " +
                "FROM accounts " +
                "WHERE account_id = ?;";
        SqlRowSet rowSet2 = jdbcTemplate.queryForRowSet(getToBalanceSql, result.getAccountTo());
        double toBalance = 0;
        while (rowSet2.next()) {
            toBalance = rowSet2.getDouble("balance");
        }
        if (fromBalance >= transfer.getAmount()) {
            String updateFromAccountSql = "UPDATE accounts " +
                    "SET balance = ? " +
                    "WHERE account_id = ?;";
            int updatedFromRows = jdbcTemplate.update(updateFromAccountSql, fromBalance - result.getAmount(), result.getAccountFrom());
            String updateToAccountSql = "UPDATE accounts " +
                    "SET balance = ? " +
                    "WHERE account_id = ?;";
            int updatedToRows = jdbcTemplate.update(updateToAccountSql, toBalance + result.getAmount(), result.getAccountTo());
            if (updatedToRows == 1 && updatedFromRows == 1) {
                String updateTransfer = "UPDATE transfers " +
                        "SET transfer_status_id = 2 " +
                        "WHERE transfer_id = ?;";
                int updatedTransfer = jdbcTemplate.update(updateTransfer, transfer.getTransferId());
                return result;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    public String rejectTransfer(int transferId) {
        String sql = "UPDATE transfers " +
                "SET transfer_status_id = 3 " +
                "WHERE transfer_id = ?;";
        int updatedRows = jdbcTemplate.update(sql, transferId);
        if (updatedRows == 1) {
            return "Transfer rejected";
        }
        else {
            return "Unable to reject the transfer";
        }
    }

    @Override
    public List<Transfer> list(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM transfers " +
                "WHERE account_from = ? OR account_to = ? AND transfer_status_id != 1;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while (rowSet.next()) {
            Transfer transfer = mapRowToTransfer(rowSet);
            transfers.add(transfer);
        }
        return transfers;
    }

    @Override
    public Transfer getById(int transferId) {
        String sql = "Select * " +
                "FROM transfers " +
                "JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id " +
                "JOIN transfer_statuses ON transfers.transfer_status_id = transfer_statuses.transfer_status_id " +
                "WHERE transfer_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
        Transfer transfer = null;
        while (rowSet.next()) {
            transfer = mapRowToTransfer(rowSet);
        }
        return transfer;
    }

    public String getType(int typeId) {
        String type = "";
        String sql = "SELECT transfer_type_desc " +
                "FROM transfer_types " +
                "WHERE transfer_type_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, typeId);
        while (rowSet.next()) {
            type = rowSet.getString("transfer_type_desc");
        }
        return  type;
    }

    public String getStatus(int statusId) {
        String status = "";
        String sql = "SELECT transfer_status_desc " +
                "FROM transfer_statuses " +
                "WHERE transfer_status_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, statusId);
        while (rowSet.next()) {
            status = rowSet.getString("transfer_status_desc");
        }
        return status;
    }

    public List<Transfer> getPending(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfers.transfer_status_id, account_from, account_to, amount " +
                "FROM transfers " +
                "WHERE account_from = ? AND transfer_type_id = 1 AND transfer_status_id = 1;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountId);
        while (rowSet.next()) {
            transfers.add(mapRowToTransfer(rowSet));
        }
        return transfers;
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
