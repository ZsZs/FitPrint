package com.processpuzzle.fitnesse.print.client;

import java.io.File;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class FitNesseClient {
   private static final String FILES_PATH = "/files";
   private static final String DEFAULT_HOST = "http://localhost:9123";
   private static final Logger logger = LoggerFactory.getLogger( FitNesseClient.class );
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
   public void createDirectory( String directoryName ) {
      try{
         restTemplate.exchange( this.hostUrl + FILES_PATH + "?createDir&dirname=" + directoryName, HttpMethod.POST, null, String.class );
      }catch( HttpClientErrorException e ){
         logger.error( "Failed to create directory: " + directoryName, e );
      }
   }

   public void deleteDirectory( String directoryName ) {
      try{
         restTemplate.exchange( this.hostUrl + FILES_PATH + "?deleteFile&filename=" + directoryName, HttpMethod.POST, null, String.class );
      }catch( HttpClientErrorException e ){
         logger.error( "Failed to delete directory: " + directoryName, e );
      }
   }

   public String retrievePage( String pageName ) {
      ResponseEntity<String> response = restTemplate.getForEntity( this.hostUrl + "/" + pageName, String.class );
      return response.getBody();
   }

   public void uploadFile( File sourceFile, String folder ) {
      createFolderIfDoesntExist( folder );

      MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
      parts.add( "Content-Type", URLConnection.guessContentTypeFromName( sourceFile.getName() ) );
      parts.add( "file", new FileSystemResource( sourceFile.getAbsolutePath() ) );
      
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType( MediaType.MULTIPART_FORM_DATA );
      
      HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>( parts, headers );

      try{
         restTemplate.exchange( this.hostUrl + FILES_PATH + "/" + folder + "/?upload&file", HttpMethod.POST, request, String.class );
      }catch( HttpClientErrorException e ){
         logger.error( "Failed to upload file: " + sourceFile.getAbsolutePath() + " to: " + folder, e );
      }
   }

   public Boolean verifyFileExist( String fileResourcePath ) {
      boolean fileExist = false;
      try{
         restTemplate.getForEntity( this.hostUrl + FILES_PATH + "/" + fileResourcePath, String.class );
         fileExist = true;
      }catch( Exception e ){
         logger.error( "Checking file's: " + fileResourcePath + " existance failed.", e );
      }

      return fileExist;
   }

   // properties
   // @formatter:off
   @Bean public RestTemplate restTemplate() { return new RestTemplate(); }
   public void setHostUrl( String hostUrl ){ this.hostUrl = hostUrl; }
   // @formatter:on

   // protected, private helper methods
   private void createFolderIfDoesntExist( String directoryName ) {
      if( !verifyFileExist( directoryName ) ){
         createDirectory( directoryName );
      }
   }
}