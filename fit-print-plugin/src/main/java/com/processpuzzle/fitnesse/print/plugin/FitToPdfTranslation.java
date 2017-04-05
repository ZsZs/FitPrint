package com.processpuzzle.fitnesse.print.plugin;

import java.io.File;
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
   public static final String FIT_TO_PDF_FILES_ROOT = "/FitToPdf/";
   private SourcePage currentPage;
   private Properties fitToPdfProperties;
   @Autowired private FitNesseClient fitNesseClient;
   private TempFile outputFile;
   @Autowired PDFRenderer pdfRenderer;
   private String pdfResourcePathInFitNesse;
   private TempFile sourceFile;

   // public accessors and mutators
   public String translate( SourcePage currentPage, Properties properties ) {
      this.currentPage = currentPage;
      this.fitToPdfProperties = properties;
      setUpTranslation();

      try{
         compileSourceHtml();
         renderHtmlToPdf();
         uploadPdfToFitNesse();
      }catch( DocumentException | IOException e ){
         e.printStackTrace();
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
   }
   
   private void uploadPdfToFitNesse(){
      pdfResourcePathInFitNesse = FIT_TO_PDF_FILES_ROOT + currentPage.getName() + ".pdf";
      fitNesseClient.uploadFile( outputFile.getFile(), pdfResourcePathInFitNesse );
   }
}
