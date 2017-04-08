package com.processpuzzle.fitnesse.print.plugin;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
public class FitToPdfTranslationWithChildsTest {
   private static final String CHILD_PAGE_ONE = "ChildPageOne";
   private static final String CHILD_PAGE_ONE_SOURCE = "<div>child one</div>";
   private static final String CHILD_PAGE_TWO = "ChildPageTwo";
   private static final String CHILD_PAGE_TWO_SOURCE = "<div>child two</div>";
   private static final String ROOT_PAGE = "CurrentPage";
   private static final String ROOT_PAGE_SOURCE = "<div>root page</div>";
   @Mock private SourcePage childPageOne;
   @Mock private SourcePage childPageTwo;
   @Mock private SourcePage currentPage;
   @MockBean private FitNesseClient fitNesseClient;
   @MockBean private FitNessePageContentExtractor contentExtractor;
   @MockBean private PDFRenderer pdfRenderer;
   @Mock private Properties properties;
   @Autowired private FitToPdfTranslation translation;

   @Before public void beforeEachTests() {
      when( properties.getProperty( FitToPdfProperties.PRINT_CHILD_PAGES.getPropertyName() )).thenReturn( "true" );
      when( currentPage.getName() ).thenReturn( ROOT_PAGE );
      when( currentPage.getFullName() ).thenReturn( ROOT_PAGE );
      when( currentPage.getChildren() ).thenReturn( Lists.newArrayList( childPageOne, childPageTwo ) );
      when( childPageOne.getName() ).thenReturn( CHILD_PAGE_ONE );
      when( childPageOne.getFullName() ).thenReturn( CHILD_PAGE_ONE );
      when( childPageTwo.getName() ).thenReturn( CHILD_PAGE_TWO );
      when( childPageTwo.getFullName() ).thenReturn( CHILD_PAGE_TWO );
      when( fitNesseClient.verifyFileExist( anyString() )).thenReturn( false );
      when( fitNesseClient.retrievePage( ROOT_PAGE )).thenReturn( ROOT_PAGE_SOURCE );
      when( fitNesseClient.retrievePage( CHILD_PAGE_ONE )).thenReturn( CHILD_PAGE_ONE_SOURCE );
      when( fitNesseClient.retrievePage( CHILD_PAGE_TWO )).thenReturn( CHILD_PAGE_TWO_SOURCE );
      when( contentExtractor.extractRealContent( ROOT_PAGE_SOURCE )).thenReturn( ROOT_PAGE_SOURCE );
      when( contentExtractor.extractRealContent( CHILD_PAGE_ONE_SOURCE )).thenReturn( CHILD_PAGE_ONE_SOURCE );
      when( contentExtractor.extractRealContent( CHILD_PAGE_TWO_SOURCE )).thenReturn( CHILD_PAGE_TWO_SOURCE );
      
      this.translation.translate( currentPage, properties );
   }

   @Test public void translate_whenChildPages_weawesContent(){
      verify( contentExtractor, times( 3 )).extractRealContent( any() );
      verify( fitNesseClient, times( 1 )).retrievePage( ROOT_PAGE );
      verify( fitNesseClient, times( 1 )).retrievePage( CHILD_PAGE_ONE );
      verify( fitNesseClient, times( 1 )).retrievePage( CHILD_PAGE_TWO );
   }
}
