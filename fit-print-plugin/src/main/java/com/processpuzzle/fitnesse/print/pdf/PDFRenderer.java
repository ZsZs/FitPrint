package com.processpuzzle.fitnesse.print.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.protocols.data.Handler;

import com.lowagie.text.DocumentException;

class DataURLStreamHandlerFactory implements URLStreamHandlerFactory {
   public URLStreamHandler createURLStreamHandler( String protocol ) {
      if( protocol.equalsIgnoreCase( "data" ) )
         return new Handler();
      else
         return null;
   }
}

@Component
public class PDFRenderer {
   private static final Logger logger = LoggerFactory.getLogger( PDFRenderer.class );
   private File outputPath;
   private File sourcePath;
   private File tempFile;

   // constructors
   public PDFRenderer() {}

   // public accessors and mutators
   public void render( File sourceFile, File outputFile ) throws DocumentException, IOException {
      this.sourcePath = sourceFile;
      this.outputPath = outputFile;

      setURLStreamHandlerFactory();
      generateTempFilePath();

      try{
         cleanUpHtml();
         createPdf();
      }catch( Exception e ){
         logger.error( "Failed to render: " + sourceFile.getAbsolutePath(), e );
      }finally{
         cleanUpTempFile();         
      }
   }

   // properties
   // @formatter:off
   // @formatter:on

   // protected, private helper methods
   private void cleanUpHtml() throws IOException {
      InputStream inputStream = new FileInputStream( this.sourcePath );
      OutputStream outputStream = new FileOutputStream( tempFile.getPath() );
      
      Tidy htmlCleaner = new Tidy();
      if( this.sourcePath != null )
         htmlCleaner.setInputEncoding( this.sourcePath.getAbsolutePath() );
      if( this.outputPath != null )
         htmlCleaner.setOutputEncoding( this.outputPath.getAbsolutePath() );
      htmlCleaner.setXHTML( true );
      htmlCleaner.parse( inputStream, outputStream );
      
      inputStream.close();
      outputStream.close();
   }

   private void cleanUpTempFile() {
      File tempFile = new File( this.tempFile.getPath() );
      tempFile.delete();
   }

   private void createPdf() throws MalformedURLException, FileNotFoundException, DocumentException, IOException {
      String url = new File( tempFile.getPath() ).toURI().toURL().toString();
      OutputStream outputPDF = new FileOutputStream( this.outputPath );

      // Create the renderer and point it to the XHTML document
      ITextRenderer renderer = new ITextRenderer();
      renderer.setDocument( url );

      // Render the PDF document
      renderer.layout();
      renderer.createPDF( outputPDF );

      // Close the streams (and don't cross them!)
      outputPDF.close();
   }

   private void generateTempFilePath() throws IOException {
      this.tempFile = File.createTempFile( "cleaned-source-", ".html" );
   }

   private void setURLStreamHandlerFactory() {
      try{
         URL.setURLStreamHandlerFactory( new DataURLStreamHandlerFactory() );
      }catch( Error e ){
         System.out.println( "The Stream Handler Factory is already defined. Moving on to convert to PDF." );
      }
   }
}
