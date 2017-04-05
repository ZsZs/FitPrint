package com.processpuzzle.fitnesse.print.client;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.processpuzzle.fitnesse.print.plugin.FitPrintIntegrationTest;

@Category( FitPrintIntegrationTest.class )
@RunWith( SpringRunner.class )
@SpringBootTest( classes = { FitNesseClient.class } )
public class FitNesseClientTest {
   @Autowired private FitNesseClient client;

   @Before public void beforeEachTests() {
   }

   @Ignore @Test public void retrievePage_returnsHtmlSourceOfPage() {
      String htmlSource = client.retrievePage( "FitPrint" );

      assertThat( htmlSource, notNullValue() );
   }
}
