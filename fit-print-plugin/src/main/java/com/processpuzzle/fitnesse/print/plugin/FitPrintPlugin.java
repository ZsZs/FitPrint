package com.processpuzzle.fitnesse.print.plugin;

import com.processpuzzle.fitnesse.print.client.FitNesseClientProperties;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import fitnesse.plugins.PluginException;
import fitnesse.plugins.PluginFeatureFactoryBase;
import fitnesse.wikitext.parser.SymbolProvider;

@SpringBootApplication
@ComponentScan( basePackages = {"com.processpuzzle.fitnesse.print.client", "com.processpuzzle.fitnesse.print.html", "com.processpuzzle.fitnesse.print.file", "com.processpuzzle.fitnesse.print.pdf", "com.processpuzzle.fitnesse.print.plugin" })
@EnableConfigurationProperties( FitNesseClientProperties.class )
public class FitPrintPlugin extends PluginFeatureFactoryBase {
   private static final Logger logger = LoggerFactory.getLogger( FitPrintPlugin.class );
   private static ApplicationContext applicationContext;

   // constructors
   public FitPrintPlugin() {}

   // public accessors and mutators
   public void registerSymbolTypes( SymbolProvider symbolProvider ) throws PluginException {
      run();
      symbolProvider.add( applicationContext.getBean( FitToPdfSymbol.class ) );
      logger.info( "FitPrintPlugin started successfully." );
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
