package com.processpuzzle.fitnesse.print.client;

public class FitNesseClient {
    private final String hostUrl;
    
    // constructors
    public FitNesseClient( String hostUrl ){
        this.hostUrl = hostUrl;
    }
    
    public String retrievePage( String pageName ){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity( this.hostUrl + "/" + pageName, String.class);
    }
}