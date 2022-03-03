package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.security.AccountNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao() {
    }

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public double getBalance(int userId, int accountId) throws AccountNotFoundException {
        String sql = "SELECT balance " +
                "FROM accounts " +
                "WHERE user_id = ? AND account_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, accountId);
        if (rowSet.next()) {
            return rowSet.getDouble("balance");
        }
        else {
            throw new AccountNotFoundException("Account for user ID: " + userId + " was not found.");
        }
    }

    @Override
    public int getIdByUserId(int userId) throws AccountNotFoundException {
        String sql = "SELECT account_id " +
                "FROM accounts " +
                "WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()) {
            return rowSet.getInt("user_id");
        }
        else {
            throw new AccountNotFoundException("Account for user ID: " + userId + " was not found.");
        }
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getDouble("balance"));
        return account;
    }
}
