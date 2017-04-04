package com.processpuzzle.fitnesse.print.plugin;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import fitnesse.plugins.PluginException;
import fitnesse.wikitext.parser.SymbolProvider;

public class FitPrintPluginWhenStartedTest {
   private FitPrintPlugin plugin;

   @Before public void beforeEachTests() {
      plugin = Mockito.spy( new FitPrintPlugin() );
   }
   
   @After public void afterEachTests(){
      plugin.stop();
   }
   
   @Test public void run_instantiatesApplicationContext(){
      plugin.run();
      
      assertThat( plugin.getApplicationContext(), notNullValue() );
   }

   @Test public void registerSymbolTypes_invokesRun() throws PluginException{
      SymbolProvider symbolProvider = mock( SymbolProvider.class );
      
      plugin.registerSymbolTypes( symbolProvider );
      
      verify( plugin, times( 1 )).run();
      assertThat( plugin.getApplicationContext(), instanceOf( ApplicationContext.class ));
   }
   
   @Test public void registerSymbolTypes_passesSymbolBean() throws PluginException{
      SymbolProvider symbolProvider = mock( SymbolProvider.class );
      
      plugin.registerSymbolTypes( symbolProvider );
      
      verify( symbolProvider ).add( plugin.getApplicationContext().getBean( FitToPdfSymbol.class ) );
   }
}
