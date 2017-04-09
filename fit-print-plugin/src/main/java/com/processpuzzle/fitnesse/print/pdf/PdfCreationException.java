package com.processpuzzle.fitnesse.print.pdf;

import java.text.MessageFormat;

public class PdfCreationException extends Exception {
   private static final long serialVersionUID = 1L;
   private static final String messageTemplate = "Couldn't create the PDF: {1} from HTML {0}";

   public PdfCreationException( String sourcePath, String outputPath ) {
      this( sourcePath, outputPath, null );
   }

   public PdfCreationException( String sourcePath, String outputPath, Exception e ) {
      super( MessageFormat.format( messageTemplate, sourcePath, outputPath ), e);
   }
}
