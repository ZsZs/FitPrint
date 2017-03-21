package com.processpuzzle.fitnesse.print.plugin;

import fitnesse.plugins.PluginException;
import fitnesse.plugins.PluginFeatureFactoryBase;
import fitnesse.wikitext.parser.SymbolProvider;

public class FitPrintPlugin extends PluginFeatureFactoryBase {
   public void registerSymbolTypes( SymbolProvider symbolProvider ) throws PluginException {
      symbolProvider.add( new FitToPdfSymbol() );
   }
}
