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
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = {PDFRenderer.class} )
public class PDFRendererTest {
   private static final String OUTPUT_PDF = "target/OutputFile.pdf";
   private static final String INPUT_HTML = "classpath:SampleFitNessePage.html";
   @Autowired private PDFRenderer renderer;
   @Autowired private ResourceLoader resourceLoader;

   @Before public void beforeEachTests() {
   }
   
   @After public void afterEachTests(){
      File outputFile = new File( OUTPUT_PDF );
      outputFile.delete();
   }

   @Test public void render_generatesPdfFile() throws Exception {
      this.renderer.render( resourceLoader.getResource( INPUT_HTML ).getFile(), new File( OUTPUT_PDF ));

      assertThat( (new File( OUTPUT_PDF )).exists(), is( true ) );
   }
}
