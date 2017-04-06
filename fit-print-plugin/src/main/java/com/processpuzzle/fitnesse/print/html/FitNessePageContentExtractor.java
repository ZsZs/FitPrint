package com.processpuzzle.fitnesse.print.html;

import static com.processpuzzle.fitnesse.print.html.XmlUtil.asList;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

@Component
public class FitNessePageContentExtractor {
   private static final Logger logger = LoggerFactory.getLogger( FitNessePageContentExtractor.class );
   private String strippedContent;
   private DocumentBuilderFactory builderFactory;
   private DocumentBuilder domParser;
   private Document domDocument;
   private XPath xPath;

   public String extractRealContent( String sourceHtml ) {
      logger.debug( "Extracting real content for FitNesse Page: " + sourceHtml );
      parseSourceHtml( sourceHtml );
      strippedContent();
      return strippedContent;
   }

   // protected, private helper mehtods
   private Document cleanUpHtml( String sourceHtml ) {
      Tidy tidy = new Tidy();
      tidy.setMakeClean( true );
      final Document cleanedHtml = tidy.parseDOM( new ByteArrayInputStream( sourceHtml.getBytes( StandardCharsets.UTF_8 ) ), null );
      return cleanedHtml;
   }

   private String correctFailures( String sourceHtml ) {
      String[] searchList = { "<header>", "</header>", "<nav ", "</nav>", "<article>", "</article>", "<footer>", "</footer>", "content=\"IE=edge\">", ".css\">", "<li <" };
      String[] replacementList = { "<div id='header'>", "</div>", "<div id='nav' ", "</div>", "<div id='article'>", "</div>", "<div id='footer'>", "</div>", "content=\"IE=edge\"/>", ".css\"/>", "<li><" };
      String correctedHtml = StringUtils.replaceEach( sourceHtml, searchList, replacementList );
      logger.debug( "Corrected HTML: \n" + correctedHtml );
      System.out.println( "Corrected HTML: \n" );
      System.out.println( correctedHtml );
      return correctedHtml;
   }

   private String node2String( Node node ) throws TransformerFactoryConfigurationError, TransformerException {
      StreamResult xmlOutput = new StreamResult( new StringWriter() );
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
      transformer.transform( new DOMSource( node ), xmlOutput );
      String extractedHtml = xmlOutput.getWriter().toString();
      logger.debug( "Extracted HTML: \n" + extractedHtml );
      System.out.println( "Extracted HTML: \n" );
      System.out.println( extractedHtml );
      return extractedHtml;
   }

   private void strippedContent(){
      String expr = "//div[@id='article']";
      NodeList resultNodeList;
      try{
         resultNodeList = (NodeList) xPath.compile( expr ).evaluate( domDocument, XPathConstants.NODESET );
         for( Node node : asList( resultNodeList ) ){
            strippedContent = node2String( node );
         }
      }catch( XPathExpressionException | TransformerFactoryConfigurationError | TransformerException e ){
         logger.error( "Stripping content failed.", e );
      }
   }
   
   private void parseSourceHtml( String sourceHtml ){
      builderFactory = DocumentBuilderFactory.newInstance();

      try{
         domParser = builderFactory.newDocumentBuilder();
         domDocument = cleanUpHtml( correctFailures( sourceHtml ) );
         xPath = XPathFactory.newInstance().newXPath();
      }catch( Exception e ){
         logger.error( "Parsing source FitNesse document failed.", e );
      }
   }
}
