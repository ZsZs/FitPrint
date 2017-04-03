package com.processpuzzle.fitnesse.print.client;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.processpuzzle.fitnesse.print.plugin.FitPrintIntegrationTest;

@Category( FitPrintIntegrationTest.class )
public class FitNesseClientTest {
    private FitNesseClient client;
    
    @Before public void beforeEachTests(){
        client = new FitNesseClient( "http://fitnesse.fit-connect.processpuzzle.com" );        
    }
    
    @Ignore @Test public void retrievePage_returnsHtmlSourceOfPage(){
        String htmlSource = client.retrievePage( "FitNesseConnect" );
        
        assertThat( htmlSource, notNullValue() );
    }
}
