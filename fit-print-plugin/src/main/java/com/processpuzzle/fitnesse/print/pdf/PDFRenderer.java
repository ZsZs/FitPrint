package com.processpuzzle.fitnesse.print.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.protocols.data.Handler;

import com.lowagie.text.DocumentException;

class DataURLStreamHandlerFactory implements URLStreamHandlerFactory {
   private static final String PROTOCOL_DATA = "data";

   public URLStreamHandler createURLStreamHandler( String protocol ) {
      if( protocol.equalsIgnoreCase( PROTOCOL_DATA ) )
         return new Handler();
      else
         return null;
   }
}

@Component
public class PDFRenderer {
   private static final Logger logger = LoggerFactory.getLogger( PDFRenderer.class );
   private File outputFile;
   private File sourceFile;

   // constructors
   
   // public accessors and mutators
   public void render( File sourceFile, File outputFile ) {
      this.sourceFile = sourceFile;
      this.outputFile = outputFile;

      setURLStreamHandlerFactory();

      try{
         createPdf();
      }catch( Exception e ){
         logger.error( "Failed to render: " + sourceFile.getAbsolutePath(), e );
      }
   }

   // properties
   // @formatter:off
   // @formatter:on

   // protected, private helper methods
   private void createPdf() throws PdfCreationException {
      OutputStream outputPDF = null;
      
      try{
         outputPDF = new FileOutputStream( this.outputFile );
         
         // Create the renderer and point it to the XHTML document
         ITextRenderer renderer = new ITextRenderer();
         renderer.setDocument( this.sourceFile );

         // Render the PDF document
         renderer.layout();
         renderer.createPDF( outputPDF );         
      }catch( Exception e ){
         logger.error( "Couldn't create the PDF: " + this.outputFile.getPath() + " from HTML" + sourceFile.getPath(), e );
         throw new PdfCreationException( sourceFile.getPath(), outputFile.getPath(), e );
      }finally {
         // Close the streams (and don't cross them!)
         try{
            outputPDF.close();
         }catch( IOException e ){
            logger.error( "Couldn't close the output stream.", e );
            throw new PdfCreationException( sourceFile.getPath(), outputFile.getPath(), e );
         }         
      }
   }

   private void setURLStreamHandlerFactory() {
      try{
         URL.setURLStreamHandlerFactory( new DataURLStreamHandlerFactory() );
      }catch( Error e ){
         logger.info( "The Stream Handler Factory is already defined. Moving on to convert to PDF." );
      }
   }
}
