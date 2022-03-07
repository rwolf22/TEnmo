package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AccountService {

    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public AccountService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public double getBalance(int userId, int accountId) {
//        int accountId = getIdByUser(userId);
        ResponseEntity<Double> response = restTemplate.exchange(baseUrl + "users/" + userId + "/accounts/" + accountId, HttpMethod.GET, makeAuthEntity(), Double.class);
        double balance = response.getBody();
        return balance;
    }

    public int getIdByUser(int userId) {
        ResponseEntity<Integer> accountIdResponse = restTemplate.exchange(baseUrl + "users/" + userId + "/accounts", HttpMethod.GET, makeAuthEntity(), Integer.class);
        return accountIdResponse.getBody();
    }

    public Account getAccountByUser(int userId) {
        ResponseEntity<Account> response = restTemplate.exchange(baseUrl + "users/" + userId + "/account", HttpMethod.GET, makeAuthEntity(), Account.class);
        return response.getBody();
    }

    public int getUserIdByAccountId(int accountId) {
        ResponseEntity<Integer> response = restTemplate.exchange(baseUrl + "users/accounts/" + accountId, HttpMethod.GET, makeAuthEntity(), Integer.class);
        return response.getBody();
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
