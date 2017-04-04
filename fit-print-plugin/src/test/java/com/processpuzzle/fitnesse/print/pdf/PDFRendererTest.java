package com.processpuzzle.fitnesse.print.pdf;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = {PDFRenderer.class} )
public class PDFRendererTest {
   private static final String OUTPUT_PDF = "OutputFile.pdf";
   private static final String INPUT_HTML = "classpath:TestInputFile.html";
   @Autowired private PDFRenderer renderer;

   @Before public void beforeEachTests() {
   }
   
   @After public void afterEachTests(){
      File outputFile = new File( OUTPUT_PDF );
      outputFile.delete();
      
   }

   @Test public void render_generatesPdfFile() throws Exception {
      this.renderer.render( INPUT_HTML, OUTPUT_PDF );

      assertThat( (new File( OUTPUT_PDF )).exists(), is( true ) );
   }
}
