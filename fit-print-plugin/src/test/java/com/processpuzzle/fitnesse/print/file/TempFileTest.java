package com.processpuzzle.fitnesse.print.file;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TempFileTest {
   private static final String FILE_EXTENSION = ".html";
   private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
   private TempFile tempFile;

   @Before public void beforeEachTests() {
      tempFile = new TempFile( FILE_EXTENSION );
   }

   @Test public void constructor_determinesTempFolder() {
      assertThat( tempFile.getSystemTempFolder(), equalTo( System.getProperty( JAVA_IO_TMPDIR ) ) );
   }

   @Test public void constructor_determinesTempFilePath() {
      assertThat( tempFile.getPath(), containsString( System.getProperty( JAVA_IO_TMPDIR ) ) );
      assertThat( tempFile.getPath(), containsString( FILE_EXTENSION ) );
   }
}
