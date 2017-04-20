package com.processpuzzle.fitnesse.print.client;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

import com.processpuzzle.fitnesse.print.plugin.FitPrintIntegrationTest;

@Category( FitPrintIntegrationTest.class )
@RunWith( SpringRunner.class )
@SpringBootTest( classes = { FitNesseClient.class } )
public class FitNesseClientIntegrationTest {
   private static final String FILES_FOLDER = "FitToPdf";
   private static final String TEST_DIRECTORY = "newDirectory";
   @Autowired private FitNesseClient client;
   @Autowired private ResourceLoader resourceLoader;

   @Before public void beforeEachTests() {
   }

   @Test public void createDirectory_whenDirectoryDoensnExist_createsIt(){
      // EXCERCISE:
      client.createDirectory( TEST_DIRECTORY );
      
      // VERIFY:
      assertThat( client.verifyFileExist( TEST_DIRECTORY ), is( true ));
      
      // TEAR DOWN:
      client.deleteDirectory( TEST_DIRECTORY );
   }

   @Test public void deleteDirectory_whenDirectoryExist_deletesIt(){
      // SETUP:
      client.createDirectory( TEST_DIRECTORY );
      
      // EXCERCISE:
      client.deleteDirectory( TEST_DIRECTORY );
      
      // VERIFY:
      assertThat( client.verifyFileExist( TEST_DIRECTORY ), is( false ));
   }

   @Test public void verifyFileExist_whenFileExist_returnsTrue(){
      assertThat( client.verifyFileExist( "css/fitnesse.css" ), is( true ));
   }

   @Test public void verifyFileExist_whenFolderExist_returnsTrue(){
      assertThat( client.verifyFileExist( "css" ), is( true ));
   }

   @Test public void verifyFileExist_whenImageExist_returnsTrue(){
      assertThat( client.verifyFileExist( "images/checkmark.png" ), is( true ));
   }

   @Test public void verifyFileExist_whenFileNoExist_returnsFalse(){
      assertThat( client.verifyFileExist( "css/something_not_exists.css" ), is( false ));
   }
   
   @Test public void retrievePage_returnsHtmlSourceOfPage() {
      String htmlSource = client.retrievePage( "FitPrint" );

      assertThat( htmlSource, containsString( "FitPrint" ));
      assertThat( htmlSource, containsString( "Dokumentation and Acceptance Tests" ));
   }
   
   @Test public void uploadFile_whenFolderDoesntExist_createsIt() throws IOException{
      client.uploadFile( resourceLoader.getResource( "classpath:checkmark.png" ).getFile(), FILES_FOLDER );
      assertThat( client.verifyFileExist( FILES_FOLDER ), is( true ));
      
      // TEAR DOWN:
      client.deleteDirectory( FILES_FOLDER + "/checkmark.png" );
      client.deleteDirectory( FILES_FOLDER );
   }
   
   @Test public void uploadFile_placesFileInFitNesse() throws IOException{
      client.uploadFile( resourceLoader.getResource( "classpath:TestInputFile.html" ).getFile(), FILES_FOLDER );
      assertThat( client.verifyFileExist( "FitToPdf/TestInputFile.html" ), is( true ));
      
      // TEAR DOWN:
      client.deleteDirectory( FILES_FOLDER + "/TestInputFile.html" );
      client.deleteDirectory( FILES_FOLDER );
   }
}
