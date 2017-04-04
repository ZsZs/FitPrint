package com.processpuzzle.fitnesse.print.plugin;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FitPrintPluginWhenStoppedTest {
   private FitPrintPlugin plugin;

   @Before public void beforeEachTests() {
      plugin = Mockito.spy( new FitPrintPlugin() );
      plugin.run();
   }
   
   @Test public void stop_shutsDownApplicationContext(){
      plugin.stop();
      
      assertThat( plugin.getApplicationContext(), nullValue() );      
   }
}
