package com.processpuzzle.fitnesse.print.plugin;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lowagie.text.DocumentException;
import com.processpuzzle.fitnesse.print.client.FitNesseClient;
import com.processpuzzle.fitnesse.print.file.TempFile;
import com.processpuzzle.fitnesse.print.pdf.PDFRenderer;

import fitnesse.wikitext.parser.SourcePage;

@Component
public class FitToPdfTranslation {
   private Properties fitToPdfProperties;
   private FitNesseClient fitNesseClient;
   @Autowired PDFRenderer pdfRenderer;

   public String translate( SourcePage currentPage, Properties properties ) {
      this.fitToPdfProperties = properties;

      fitNesseClient = new FitNesseClient( "http://localhost:9123" );
      TempFile sourceFile = new TempFile( ".html" );
      TempFile outputFile = new TempFile( ".pdf" );

      try{
         sourceFile.save( fitNesseClient.retrievePage( currentPage.getFullName() ) );
         this.pdfRenderer.render( sourceFile.getPath(), outputFile.getPath() );

         for( SourcePage childPage : currentPage.getChildren() ){
            System.out.println( "Child: " + childPage.getName() );
         }
      }catch( DocumentException | IOException e ){
         e.printStackTrace();
      }

      return outputFile.getPath();
   }
}
