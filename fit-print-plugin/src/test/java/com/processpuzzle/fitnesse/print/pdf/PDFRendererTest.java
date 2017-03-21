package com.processpuzzle.fitnesse.print.pdf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class PDFRendererTest {
    private PDFRenderer renderer;
    
    @Before public void beforeEachTests(){
        this.renderer = new PDFRenderer( "http://fitnesse.fit-connect.processpuzzle.com/FrontPage", "output.pdf" );
    }
    
    @Test public void render_generatesPdfFile() throws Exception{
        this.renderer.render();
        
        assertThat( (new File( "output.pdf" )).exists(), is( true ) );
    }
}
