package com.processpuzzle.fitnesse.print.plugin;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.processpuzzle.fitnesse.print.client.FitNesseClient;
import com.processpuzzle.fitnesse.print.html.FitNessePageContentExtractor;
import com.processpuzzle.fitnesse.print.pdf.PDFRenderer;

import fitnesse.wikitext.parser.SourcePage;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { FitToPdfTranslation.class, FitNessePageContentExtractor.class, PDFRenderer.class } )
public class FitToPdfTranslationInProcessTest {
   @Mock private SourcePage currentPage;
   @MockBean private FitNesseClient fitNesseClient;
   @MockBean private PDFRenderer pdfRenderer;
   @Mock private Properties properties;
   @Autowired private FitToPdfTranslation translation;
   
   @Test public void traslate_skipsIfCurrentlyInProcess(){
      Whitebox.setInternalState( translation, "inProcessing", true );
      
      this.translation.translate( currentPage, properties );
      
      verify( fitNesseClient, never() ).verifyFileExist( anyString() );
      
      Whitebox.setInternalState( translation, "inProcessing", false );
   }
}
