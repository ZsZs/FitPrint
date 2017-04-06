package com.processpuzzle.fitnesse.print.html;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { FitNessePageContentExtractor.class } )
public class FitNessePageContentExtractorTest {
   private static final String SAMPLE_FIT_NESSE_PAGE = "classpath:SampleFitNessePage.html";
   @Autowired private FitNessePageContentExtractor contentExtractor;
   @Autowired private ResourceLoader resourceLoader;
   private String sourceHtml;
   private String fileContent = "";

   @Before public void beforeEachTests() throws IOException{
      sourceHtml = readTestFile();
   }

   @Test public void extractRealContent_stripsHeadAndBody() {
      assertThat( contentExtractor.extractRealContent( sourceHtml ), not( containsString( "<head>" ) ) );
   }

   // protected, private helper methods
   private String readTestFile() throws IOException {
      String filePath = resourceLoader.getResource( SAMPLE_FIT_NESSE_PAGE ).getFile().getAbsolutePath();
      Files.lines( Paths.get( filePath ), StandardCharsets.UTF_8 ).forEach( line -> {
         this.fileContent += line;
      });
      return fileContent;
   }
}
