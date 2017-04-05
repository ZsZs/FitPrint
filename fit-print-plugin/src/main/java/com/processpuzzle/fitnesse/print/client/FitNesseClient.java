package com.processpuzzle.fitnesse.print.client;

import java.io.File;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FitNesseClient {
   private String hostUrl;

   // constructors
   public FitNesseClient() {
      this( null );
   }

   public FitNesseClient( String hostUrl ) {
      this.hostUrl = hostUrl;
   }

   // public accessors and mutators
   public String retrievePage( String pageName ) {
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<String> response = restTemplate.getForEntity( this.hostUrl + "/" + pageName, String.class );
      return response.getBody();
   }
   
   public void uploadFile( File sourceFile, String destinationFileName ){
      
   }
   
   // properties
   // @formatter:off
   public void setHostUrl( String hostUrl ){ this.hostUrl = hostUrl; }
   // @formatter:on
   
   // protected, private helper methods
   
}