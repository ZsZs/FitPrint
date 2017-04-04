package com.processpuzzle.fitnesse.print.plugin;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import fitnesse.plugins.PluginException;
import fitnesse.wikitext.parser.SymbolProvider;

public class FitPrintPluginTest {
   private FitPrintPlugin plugin;

   @Before public void beforeEachTests() {
      plugin = Mockito.spy( new FitPrintPlugin() );
   }
   
   @Test public void run_instantiatesApplicationContext(){
      plugin.run();
      
      assertThat( plugin.getApplicationContext(), notNullValue() );
      verify( plugin, times( 1 )).startUp();
   }

   @Test public void stop_shutsDownApplicationContext(){
      // SETUP:
      plugin.run();
      
      // EXCERCISE:
      plugin.stop();
      
      // VERIFY:
      assertThat( plugin.getApplicationContext(), nullValue() );      
   }
   
   @Test public void registerSymbolTypes_lazyLoadsApplicationContext() throws PluginException{
      SymbolProvider symbolProvider = mock( SymbolProvider.class );
      
      plugin.registerSymbolTypes( symbolProvider );
      
      assertThat( plugin.getApplicationContext(), instanceOf( ApplicationContext.class ));
   }
}
