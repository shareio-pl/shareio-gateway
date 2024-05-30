package org.shareio.gateway.config.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component

public class AuthUtil {


    private RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public String getToken(String userId, String password) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplate = restTemplateBuilder.build();
        UUID userUUID = UUID.fromString(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject getTokenJsonObject = new JSONObject();
        getTokenJsonObject.put("userId", userId);
        getTokenJsonObject.put("password", password);
        HttpEntity<String> request =
                new HttpEntity<>(getTokenJsonObject.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange("http://shareio.local:8085/jwt/generate", HttpMethod.POST,request, String.class);
        System.out.println("token:"+response.getBody());
        return response.getBody();
    }
}
