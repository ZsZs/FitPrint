package com.processpuzzle.fitnesse.print.plugin;

public class FitToPdfException extends Exception {
   private static final long serialVersionUID = 1L;

   public FitToPdfException( String message ) {
      super( message );
   }

   public FitToPdfException( String message, Exception e ) {
      super( message, e );
   }
}
