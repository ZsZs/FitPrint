package com.processpuzzle.fitnesse.print.plugin;

public enum FitToPdfProperties {
   PRINT_CHILD_PAGES( "printChildPages" ),
   PRING_ROOT_PAGE( "printRootPage" );
   
   FitToPdfProperties( String propertyName ){
      this.propertyName = propertyName;
   }
   
   private final String propertyName;
   
   // @formatter:off
   public String getPropertyName(){ return propertyName; }
   // @formatter:on
}
