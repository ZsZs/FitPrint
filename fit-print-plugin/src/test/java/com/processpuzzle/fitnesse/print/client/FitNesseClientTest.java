package com.processpuzzle.fitnesse.print.client;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { FitNesseClient.class, FitNesseClientProperties.class } )
@EnableConfigurationProperties
@ActiveProfiles( "unit-test" )
public class FitNesseClientTest {
   @Autowired private FitNesseClient client;
   @Autowired private FitNesseClientProperties properties;

   @Test public void constructor_injectsProperties(){
       assertThat( client.getHostUrl(), equalTo( properties.hostUrl() ));
       assertThat( client.getHostUrl(), containsString( "https://ide.c9.io:8090/zsuffa/fit-print" ));
   }    
}
