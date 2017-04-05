package com.processpuzzle.fitnesse.print.plugin;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.lowagie.text.DocumentException;
import com.processpuzzle.fitnesse.print.client.FitNesseClient;
import com.processpuzzle.fitnesse.print.pdf.PDFRenderer;

import fitnesse.wikitext.parser.SourcePage;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { FitToPdfTranslation.class, PDFRenderer.class } )
public class FitToPdfTranslationTest {
   private static final String CURRENT_PAGE = "CurrentPage";
   @Mock private SourcePage currentPage;
   @MockBean private FitNesseClient fitNesseClient;
   private String outputFilePath;
   @MockBean private PDFRenderer pdfRenderer;
   @Mock private Properties properties;
   @Autowired private FitToPdfTranslation translation;

   @Before public void beforeEachTests() {
      when( currentPage.getName() ).thenReturn( CURRENT_PAGE );
      when( currentPage.getFullName() ).thenReturn( CURRENT_PAGE );
      when( fitNesseClient.verifyFileExist( anyString() )).thenReturn( false );
      when( fitNesseClient.retrievePage( CURRENT_PAGE )).thenReturn( "something" );
      
      outputFilePath = this.translation.translate( currentPage, properties );
   }

   @Test public void traslate_checkesIfFileAlreadyGenerated(){      
      verify( fitNesseClient ).verifyFileExist( anyString() );
   }
   
   @Test public void traslate_retrievesAndSavesCurrentPage() {      
      verify( fitNesseClient ).retrievePage( CURRENT_PAGE );
   }
   
   @Test public void translate_invokesPdfRenderer() throws DocumentException, IOException{
      verify( pdfRenderer ).render( anyString(), anyString() );
   }

   @Test public void traslate_returnsOutputFilePath() {
      assertThat( outputFilePath, containsString( FitToPdfTranslation.FIT_TO_PDF_FILES_ROOT ));
      assertThat( outputFilePath, containsString( ".pdf" ));
   }
   
   @Test public void translate_uploadsOutputPdf(){
      verify( fitNesseClient ).uploadFile( translation.getOutputFile(), outputFilePath );
   }
}
