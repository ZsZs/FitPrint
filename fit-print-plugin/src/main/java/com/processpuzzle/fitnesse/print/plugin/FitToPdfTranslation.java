package com.processpuzzle.fitnesse.print.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lowagie.text.DocumentException;
import com.processpuzzle.fitnesse.print.client.FitNesseClient;
import com.processpuzzle.fitnesse.print.html.FitNessePageContentExtractor;
import com.processpuzzle.fitnesse.print.pdf.PDFRenderer;

import fitnesse.wikitext.parser.SourcePage;

@Component
public class FitToPdfTranslation {
   public static final String FIT_TO_PDF_FILES_ROOT = "FitToPdf/";
   private static final Logger logger = LoggerFactory.getLogger( FitToPdfTranslation.class );
   @Autowired private FitNessePageContentExtractor contentExtractor;
   private SourcePage currentPage;
   private Properties fitToPdfProperties;
   @Autowired private FitNesseClient fitNesseClient;
   private Boolean inProcessing = false;
   private File outputFile;
   @Autowired private PDFRenderer pdfRenderer;
   private String pdfResourcePathInFitNesse;
   private File inputFile;

   // public accessors and mutators
   public String translate( SourcePage currentPage, Properties properties ) {
      if( inProcessing )
         return "";
      logger.debug( "Starting translation of: " + currentPage.getName() + " HTML page to PDF." );
      this.currentPage = currentPage;
      this.fitToPdfProperties = properties;

      try{
         setUpTranslation();
         if( !verifyPdfExist() ){
            String sourceHtml = compileSourceHtml( this.currentPage );
            sourceHtml = contentExtractor.cleanUpHtml( sourceHtml );
            saveSourceHtml( sourceHtml );
            renderHtmlToPdf();
            uploadPdfToFitNesse();
         }
      }catch( DocumentException | IOException | FitToPdfException e ){
         logger.error( "Could not tranlate page: " + currentPage.getFullName() + " to PDF.", e );
      }finally{
         tearDownTranslation();
      }

      return pdfResourcePathInFitNesse;
   }

   // properties
   // @formatter:off
   public File getOutputFile(){ return this.outputFile; }
   public File getInputFile(){ return this.inputFile; }
   // @formatter:on

   // protected, private helper methods
   private String compileSourceHtml( SourcePage page ) {
      String pageContent = contentExtractor.extractRealContent( fitNesseClient.retrievePage( page.getFullName() ) );
      
      Boolean printChildPages = Boolean.parseBoolean( this.fitToPdfProperties.getProperty( FitToPdfProperties.PRINT_CHILD_PAGES.getPropertyName() ) );
      if( printChildPages != null && printChildPages ){
         for( SourcePage childPage : page.getChildren() ){
            if( !childPage.getName().equals( "RecentChanges" )){
               pageContent += this.compileSourceHtml( childPage );
            }
         }
      }

      return pageContent;
   }

   private void renderHtmlToPdf() throws DocumentException, IOException {
      this.pdfRenderer.render( inputFile, outputFile );
   }
   
   private void saveSourceHtml( String pageContent ) throws FitToPdfException{
      BufferedWriter bufferedWriter = null;
      FileWriter fileWriter = null;

      try{
         fileWriter = new FileWriter( inputFile.getAbsoluteFile(), true );
         bufferedWriter = new BufferedWriter( fileWriter );
         bufferedWriter.write( pageContent );
      }catch( Exception e ){
         String message = "Couldn't write HTML content to file: " + inputFile.getAbsolutePath();
         logger.error( message, e );
         throw new FitToPdfException( message, e );
      }finally{
         try{
            if( bufferedWriter != null )
               bufferedWriter.close();
            if( fileWriter != null )
               fileWriter.close();
         }catch( IOException ex ){
            ex.printStackTrace();
         }
      }
      
      logger.debug( "HTML content is saved to file: " + inputFile.getAbsolutePath() );
   }

   private void setUpTranslation() throws IOException {
      inProcessing = true;
      fitNesseClient.setHostUrl( "http://localhost:9123" );
      inputFile = File.createTempFile( "source-", ".html" );
      outputFile = File.createTempFile( "output", ".pdf" );
      pdfResourcePathInFitNesse = FIT_TO_PDF_FILES_ROOT + currentPage.getName() + ".pdf";
   }

   private void tearDownTranslation() {
      inProcessing = false;
      inputFile.delete();
      outputFile.delete();
   }

   private void uploadPdfToFitNesse() throws IOException {
      Path fileToMovePath = Paths.get( outputFile.getCanonicalPath() );
      Path targetPath = Paths.get( System.getProperty("java.io.tmpdir"));
      Path targetFile = Files.move( fileToMovePath, targetPath.resolve( currentPage.getName() + ".pdf" ) );
      
      this.outputFile.delete();
      this.outputFile = targetFile.toFile();
      
      fitNesseClient.uploadFile( this.outputFile, FIT_TO_PDF_FILES_ROOT );
   }

   private boolean verifyPdfExist() {
      logger.debug( "Verify if generated PDF " + pdfResourcePathInFitNesse + " already exists." );
      return fitNesseClient.verifyFileExist( pdfResourcePathInFitNesse );
   }
}
