package com.processpuzzle.fitnesse.print.pdf;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = {PDFRenderer.class} )
public class PDFRendererTest {
   private static final String INPUT_FITNESSE_HTML = "classpath:Extracted.html";
   private static final String INPUT_HTML = "classpath:TestInputFile.html";
   private File outputFile;
   @Autowired private PDFRenderer renderer;
   @Autowired private ResourceLoader resourceLoader;

   @Before public void beforeEachTests() throws IOException {
      outputFile = File.createTempFile( "test-output-", ".pdf" );
   }
   
   @After public void afterEachTests(){
      outputFile.delete();
   }

   @Test public void render_generatesPdfFile() throws Exception {
      this.renderer.render( resourceLoader.getResource( INPUT_HTML ).getFile(), outputFile );

      assertThat( outputFile.exists(), is( true ) );
   }

   @Test public void render_generatesPdfFromFitNesse() throws Exception {
      this.renderer.render( resourceLoader.getResource( INPUT_FITNESSE_HTML ).getFile(), outputFile );

      assertThat( outputFile.exists(), is( true ) );
   }
}
