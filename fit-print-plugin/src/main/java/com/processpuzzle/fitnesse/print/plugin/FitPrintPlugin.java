package com.processpuzzle.fitnesse.print.plugin;

import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import fitnesse.plugins.PluginException;
import fitnesse.plugins.PluginFeatureFactoryBase;
import fitnesse.wikitext.parser.SymbolProvider;

@SpringBootApplication
public class FitPrintPlugin extends PluginFeatureFactoryBase {
   private static ApplicationContext applicationContext;

   // constructors
   public FitPrintPlugin() {}

   // public accessors and mutators
   public void registerSymbolTypes( SymbolProvider symbolProvider ) throws PluginException {
      run();
      symbolProvider.add( applicationContext.getBean( FitToPdfSymbol.class ) );
   }

   public void run() {
      startUp();
   }

   public void stop() {
      SpringApplication.exit( applicationContext );
   }

   // Properties
   // @formatter:off
   public ApplicationContext getApplicationContext() { return applicationContext; }
   // @formatter:on

   // protected, private helper methods
   private static void configureApplicationContext() {
      if( applicationContext == null ){
         applicationContext = new SpringApplicationBuilder( FitPrintPlugin.class ).web( false ).run( new String[] {} );
      }
   }

   @PreDestroy private void shutDown() {
      applicationContext = null;
   }

   private void startUp() {
      configureApplicationContext();
   }
}
