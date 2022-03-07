package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {

    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private AccountService accountService;

    private String authToken = null;

    public TransferService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.accountService = new AccountService(baseUrl);
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Transfer[] list(int userId, int accountId) {
//        int accountId = accountService.getIdByUser(userId);
        ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "/user/" + userId + "/accounts/" + accountId + "/transfers", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
        return response.getBody();
    }

    public Transfer createTransfer(Transfer newTransfer, int userId, int accountId) {
        Transfer returnedTransfer = null;
        try {
            HttpEntity<Transfer> response = restTemplate.exchange(baseUrl + "user/" + userId + "/accounts/" + accountId + "/transfers", HttpMethod.POST, makeTransferEntity(newTransfer), Transfer.class);
            returnedTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        return returnedTransfer;
    }

    public Transfer getTransfer(int userId, int accountId, int transferId) {
//        int accountId = accountService.getIdByUser(userId);
        ResponseEntity<Transfer> transferResponse = restTemplate.exchange(baseUrl + "user/" + userId + "/accounts/" + accountId + "/transfers/" + transferId, HttpMethod.GET, makeAuthEntity(), Transfer.class);
        return transferResponse.getBody();
    }

    public String getTypeDesc(int typeId) {
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "transfers/type/" + typeId, HttpMethod.GET, makeAuthEntity(), String.class);
        return response.getBody();
    }

    public String getStatusDesc(int statusId) {
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "transfers/status/" + statusId, HttpMethod.GET, makeAuthEntity(), String.class);
        return response.getBody();
    }

    public Transfer[] getPending(int userId, int accountId) {
//        int accountId = accountService.getIdByUser(userId);
        ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "user/" + userId + "/accounts/" + accountId + "/transfers/pending", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
        return response.getBody();
    }

    public boolean approveTransfer(int userId, int accountId, int transferId) {
        boolean success = false;
//        int accountId = accountService.getIdByUser(userId);
        Transfer transfer = getTransfer(userId, accountId, transferId);
        try {
            restTemplate.put(baseUrl + "user/" + userId + "/accounts/" + accountId + "/transfers/" + transferId + "/approve",makeTransferEntity(transfer));
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        return success;
    }

    public boolean rejectTransfer(int userId, int accountId, int transferId) {
        boolean success = false;
//        int accountId = accountService.getIdByUser(userId);
        Transfer transfer = getTransfer(userId, accountId, transferId);
        try {
            restTemplate.put(baseUrl + "user/" + userId + "/accounts/" + accountId + "/transfers/" + transferId + "/reject", makeTransferEntity(transfer));
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        return success;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
