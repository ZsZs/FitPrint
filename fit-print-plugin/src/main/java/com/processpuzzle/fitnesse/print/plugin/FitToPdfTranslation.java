package com.processpuzzle.fitnesse.print.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lowagie.text.DocumentException;
import com.processpuzzle.fitnesse.print.client.FitNesseClient;
import com.processpuzzle.fitnesse.print.file.TempFile;
import com.processpuzzle.fitnesse.print.pdf.PDFRenderer;

import fitnesse.wikitext.parser.SourcePage;

@Component
public class FitToPdfTranslation {
   public static final String FIT_TO_PDF_FILES_ROOT = "/FitToPdf/";
   private static final Logger logger = LoggerFactory.getLogger( FitToPdfTranslation.class );
   private SourcePage currentPage;
   private Properties fitToPdfProperties;
   @Autowired private FitNesseClient fitNesseClient;
   private TempFile outputFile;
   @Autowired private PDFRenderer pdfRenderer;
   private String pdfResourcePathInFitNesse;
   private TempFile sourceFile;

   // public accessors and mutators
   public String translate( SourcePage currentPage, Properties properties ) {
      this.currentPage = currentPage;
      this.fitToPdfProperties = properties;
      setUpTranslation();

      try{
         if( !verifyPdfExist() ){
            compileSourceHtml();
            renderHtmlToPdf();
            uploadPdfToFitNesse();
         }
      }catch( DocumentException | IOException e ){
         logger.error( "Could not tranlate page: " + currentPage.getFullName() + " to PDF.", e );;
      }
      
      return pdfResourcePathInFitNesse;
   }

   // properties
   // @formatter:off
   public File getOutputFile(){ return this.outputFile.getFile(); }
   // @formatter:on

   // protected, private helper methods
   private void compileSourceHtml() {
      sourceFile.save( fitNesseClient.retrievePage( currentPage.getFullName() ) );
   }

   private void renderHtmlToPdf() throws DocumentException, IOException {
      this.pdfRenderer.render( sourceFile.getPath(), outputFile.getPath() );
   }

   private void setUpTranslation() {
      fitNesseClient.setHostUrl( "http://localhost:9123" );
      sourceFile = new TempFile( ".html" );
      outputFile = new TempFile( ".pdf" );
      pdfResourcePathInFitNesse = FIT_TO_PDF_FILES_ROOT + currentPage.getName() + ".pdf";
   }

   private void uploadPdfToFitNesse() {
      fitNesseClient.uploadFile( outputFile.getFile(), pdfResourcePathInFitNesse );
   }

   private boolean verifyPdfExist() {
      return fitNesseClient.verifyFileExist( null );
   }
}
