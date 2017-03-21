package com.processpuzzle.fitnesse.print.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class FitNesseClient {
    private final String hostUrl;
    
    // constructors
    public FitNesseClient( String hostUrl ){
        this.hostUrl = hostUrl;
    }
    
    public String retrievePage( String pageName ){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity( this.hostUrl + "/" + pageName, String.class);
        return response.getBody();
    }
}