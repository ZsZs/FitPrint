package com.processpuzzle.fitnesse.print.html;

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
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import static com.processpuzzle.fitnesse.print.html.XmlUtil.asList;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Component
public class FitNessePageContentExtractor {
   private static final Logger logger = LoggerFactory.getLogger( FitNessePageContentExtractor.class );
   private String strippedContent;

   public String extractRealContent( String sourceHtml ) {
      logger.debug( "Extracting real content for FitNesse Page: " + sourceHtml );
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder domParser = null;

      try{
         domParser = builderFactory.newDocumentBuilder();
         Document domDocument = cleanUpHtml( correctFailures( sourceHtml ) );
         XPath xPath = XPathFactory.newInstance().newXPath();
         String expr = "//div[@id='nav']";
         NodeList resultNodeList = (NodeList) xPath.compile( expr ).evaluate( domDocument, XPathConstants.NODESET );
         for( Node node : asList( resultNodeList ) ){
            strippedContent = node2String( node );
         }
      }catch( Exception e ){
         logger.error( "Parsing source FitNesse document failed.", e );
      }

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
      String[] searchList = { "<nav>", "</nav>", "<article>", "</article>", "<footer>", "</footer>" };
      String[] replacementList = { "<div id='nav'>", "</div>", "<div id='article'>", "</div>", "<div id='footer'>", "</div>" };
      String correctedHtml = StringUtils.replaceEach( sourceHtml, searchList, replacementList );
      return correctedHtml;
   }

   private String node2String( Node node ) throws TransformerFactoryConfigurationError, TransformerException {
      StreamResult xmlOutput = new StreamResult( new StringWriter() );
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
      transformer.transform( new DOMSource( node ), xmlOutput );
      return xmlOutput.getWriter().toString();
   }
}
