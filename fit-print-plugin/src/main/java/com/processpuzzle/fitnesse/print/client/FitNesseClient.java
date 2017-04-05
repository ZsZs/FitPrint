package com.processpuzzle.fitnesse.print.client;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FitNesseClient {
   private static final String DEFAULT_HOST = "http://localhohost:9123";
   private String hostUrl;
   @Autowired private RestTemplate restTemplate;

   // constructors
   public FitNesseClient() {
      this( DEFAULT_HOST );
   }

   public FitNesseClient( String hostUrl ) {
      this.hostUrl = hostUrl;
   }

   // public accessors and mutators
   public String retrievePage( String pageName ) {
      ResponseEntity<String> response = restTemplate.getForEntity( this.hostUrl + "/" + pageName, String.class );
      return response.getBody();
   }

   public void uploadFile( File sourceFile, String fileResourePath ) {}

   public Boolean verifyFileExist( String fileResourePath ) {
      return true;
   }

   // properties
   // @formatter:off
   @Bean public RestTemplate restTemplate() { return new RestTemplate(); }
   public void setHostUrl( String hostUrl ){ this.hostUrl = hostUrl; }
   // @formatter:on

   // protected, private helper methods

}