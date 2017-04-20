package com.processpuzzle.fitnesse.print.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties( "fitnesse" )
public class FitNesseClientProperties {
   private String host = "localhost";
   private String path = "";
   private int port = 8080;
   private String protocol = "http";

   public String hostUrl(){
       String hostUrl = null;
       try{
          URI uri = new URIBuilder().setScheme( protocol ).setHost( host ).setPort( port ).setPath( path ).build();
          hostUrl = uri.toURL().toString();
       }catch( URISyntaxException | MalformedURLException e ){
          e.printStackTrace();
          hostUrl = host + ":" + new Integer( port ).toString();
       }
       
       return hostUrl;
   }
   
   // properties
   // @formatter:off
   public String getHost() { return host; }
   public String getPath() { return path; }
   public int getPort() { return port; }
   public String getProtocol() { return protocol; }
   public void setHost( String host ) { this.host = host; }
   public void setPath( String path ) { this.path = path; }
   public void setPort( int port ) { this.port = port; }
   public void setProtocol( String protocol ) { this.protocol = protocol; }
   // @formatter:on

}