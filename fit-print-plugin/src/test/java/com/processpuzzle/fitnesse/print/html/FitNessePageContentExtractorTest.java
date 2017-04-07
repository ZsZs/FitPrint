package com.processpuzzle.fitnesse.print.html;

import static com.processpuzzle.fitnesse.print.html.XmlUtil.asList;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { FitNessePageContentExtractor.class } )
public class FitNessePageContentExtractorTest {
   private static final String SAMPLE_FIT_NESSE_PAGE = "classpath:SampleFitNessePage.html";
   @Autowired private FitNessePageContentExtractor contentExtractor;
   @Autowired private ResourceLoader resourceLoader;
   private String sourceHtml;
   private String fileContent = "";
   private DocumentBuilderFactory builderFactory;
   private DocumentBuilder domParser;
   private XPath xPath;
   private Document domDocument;

   @Before public void beforeEachTests() throws IOException {
      sourceHtml = readTestFile();
      parseResultDocument( contentExtractor.extractRealContent( sourceHtml ) );
   }

   @Test public void extractRealContent_stripScripts() {
      assertThat( searchElement( "//script" ), nullValue() );
   }

   // protected, private helper methods
   private String readTestFile() throws IOException {
      String filePath = resourceLoader.getResource( SAMPLE_FIT_NESSE_PAGE ).getFile().getAbsolutePath();
      Files.lines( Paths.get( filePath ), StandardCharsets.UTF_8 ).forEach( line -> {
         this.fileContent += line;
      } );
      return fileContent;
   }

   private String searchElement( String xPathSelector ) {
      String foundElements = null;
      NodeList resultNodeList;
      try{
         resultNodeList = (NodeList) xPath.compile( xPathSelector ).evaluate( domDocument, XPathConstants.NODESET );
         for( Node node : asList( resultNodeList ) ){
            foundElements += node2String( node );
         }
      }catch( XPathExpressionException | TransformerFactoryConfigurationError | TransformerException e ){
         e.printStackTrace();
      }
      return foundElements;
   }

   private void parseResultDocument( String resultHtml ) {
      builderFactory = DocumentBuilderFactory.newInstance();
      domParser = null;

      try{
         domParser = builderFactory.newDocumentBuilder();
         domDocument = domParser.parse( new InputSource( new StringReader( resultHtml ) ) );
         xPath = XPathFactory.newInstance().newXPath();
      }catch( Exception e ){
         e.printStackTrace();
      }
   }

   private String node2String( Node node ) throws TransformerFactoryConfigurationError, TransformerException {
      StreamResult xmlOutput = new StreamResult( new StringWriter() );
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
      transformer.transform( new DOMSource( node ), xmlOutput );
      String extractedHtml = xmlOutput.getWriter().toString();
      return extractedHtml;
   }
}
