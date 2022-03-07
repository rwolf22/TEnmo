package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class UserService {

    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public UserService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername(int userId) {
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "user/" + userId, HttpMethod.GET, makeAuthEntity(), String.class);
        return response.getBody();
    }

    public User[] list() {
        ResponseEntity<User[]> response = restTemplate.exchange(baseUrl + "user/all", HttpMethod.GET, makeAuthEntity(), User[].class);
        return response.getBody();
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
