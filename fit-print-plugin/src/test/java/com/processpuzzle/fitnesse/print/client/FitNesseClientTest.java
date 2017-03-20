package com.processpuzzle.fitnesse.print.client;

public class FitNesseClientTest {
    private FitNesseClient client;
    
    @Before public void beforeEachTests(){
        client = new FitNesseClient( "http://localhost:8080" );        
    }
    
    @Test public void retrievePage_returnsHtmlSourceOfPage(){
        String htmlSource = client.retrievePage( "FrontPage" );
    }
}
