package com.processpuzzle.fitnesse.print.plugin;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import fitnesse.wikitext.parser.SourcePage;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.Translator;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { FitToPdfSymbol.class } )
public class FitToPdfSymbolTest {
   private static final String OUTPUT_PDF = "/FitToPdf/Output.pdf";
   @Autowired private FitToPdfSymbol fitToPdfSymbol;
   @MockBean private FitToPdfTranslation fitToPdfTranslation;
   @MockBean private Properties properties;
   @Mock private SourcePage sourcePage;
   @Mock private Symbol symbol;
   @Mock private Translator translator;
   
   @Before public void beforeEachTest(){
      when( translator.getPage() ).thenReturn( sourcePage );
      when( fitToPdfTranslation.translate( sourcePage, properties ) ).thenReturn( OUTPUT_PDF );
   }
   
   @Test public void toTarget_waewesInFilePath(){
      fitToPdfSymbol.toTarget( translator, symbol );
      
      assertThat( fitToPdfSymbol.getDisplayText(), containsString( OUTPUT_PDF ));
   }
   
   @Test public void toTarget_invokesTranslation(){
      fitToPdfSymbol.toTarget( translator, symbol );
      
      verify( fitToPdfTranslation ).translate( sourcePage, properties );
   }
}
