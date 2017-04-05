package com.processpuzzle.fitnesse.print.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.protocols.data.Handler;

import com.lowagie.text.DocumentException;
import com.processpuzzle.fitnesse.print.file.TempFile;

class DataURLStreamHandlerFactory implements URLStreamHandlerFactory {
   public URLStreamHandler createURLStreamHandler( String protocol ) {
      if( protocol.equalsIgnoreCase( "data" ) )
         return new Handler();
      else
         return null;
   }
}

@Component
public class PDFRenderer implements ResourceLoaderAware {
   private ResourceLoader resourceLoader;
   private String outputPath;
   private String sourcePath;
   private TempFile tempFile;

   // constructors
   public PDFRenderer() {}

   // public accessors and mutators
   public void render( String sourcePath, String outputPath ) throws DocumentException, IOException {
      this.sourcePath = sourcePath;
      this.outputPath = outputPath;

      setURLStreamHandlerFactory();
      generateTempFilePath();

      InputStream inputStream = this.resourceLoader.getResource( this.sourcePath ).getInputStream();
      OutputStream outputStream = new FileOutputStream( tempFile.getPath() );
      cleanUpHtml( inputStream, outputStream );
      createPdf( outputStream );
      cleanUpTempFile();
   }

   // properties
   // @formatter:off
   public void setResourceLoader(ResourceLoader resourceLoader) { this.resourceLoader = resourceLoader; }
   // @formatter:on

   // protected, private helper methods
   private void cleanUpHtml( InputStream inputStream, OutputStream outputStream ) {
      Tidy htmlCleaner = new Tidy();
      if( !this.sourcePath.isEmpty() )
         htmlCleaner.setInputEncoding( this.sourcePath );
      if( !this.outputPath.isEmpty() )
         htmlCleaner.setOutputEncoding( this.outputPath );
      htmlCleaner.setXHTML( true );
      htmlCleaner.parse( inputStream, outputStream );
   }

   private void cleanUpTempFile() {
      File tempFile = new File( this.tempFile.getPath() );
      tempFile.delete();
   }

   private void createPdf( OutputStream outputStream ) throws MalformedURLException, FileNotFoundException, DocumentException, IOException {
      String url = new File( tempFile.getPath() ).toURI().toURL().toString();
      OutputStream outputPDF = new FileOutputStream( this.outputPath );

      // Create the renderer and point it to the XHTML document
      ITextRenderer renderer = new ITextRenderer();
      renderer.setDocument( url );

      // Render the PDF document
      renderer.layout();
      renderer.createPDF( outputPDF );

      // Close the streams (and don't cross them!)
      outputStream.close();
      outputPDF.close();
   }

   private void generateTempFilePath() {
      this.tempFile = new TempFile( ".html" );
   }

   private void setURLStreamHandlerFactory() {
      try{
         URL.setURLStreamHandlerFactory( new DataURLStreamHandlerFactory() );
      }catch( Error e ){
         System.out.println( "The Stream Handler Factory is already defined. Moving on to convert to PDF." );
      }
   }
}
