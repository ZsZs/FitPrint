package com.processpuzzle.fitnesse.print.html;

import static com.processpuzzle.fitnesse.print.html.XmlUtil.asList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component
public class FitNessePageContentExtractor {
   private static final String SVG_STYLE = "<head><style type=\"text/css\">svg { display: block; width: 100mm; height: 100mm; }</style></head>";
   private static final String XSLT_FILE = "classpath:FitToPdf.xsl";
   private static final Logger logger = LoggerFactory.getLogger( FitNessePageContentExtractor.class );
   private DocumentBuilderFactory builderFactory;
   private Document documentDOM;
   private DocumentBuilder domParser;
   @Autowired ResourceLoader resourceLoader;
   private String sourceHtml;
   private String strippedContent;
   private XPath xPath;

   // public accessors and mutators
   public String buildHtml( String inputHtml ) throws IOException {
      HtmlCleaner cleaner = new HtmlCleaner();
      CleanerProperties props = cleaner.getProperties();
      TagNode node = cleaner.clean( inputHtml );
      
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      new PrettyXmlSerializer(props).writeToStream( node, outputStream );
      String cleanedHtml = outputStream.toString( StandardCharsets.UTF_8.name() );
      cleanedHtml = StringUtils.replace( cleanedHtml, "<head/>", SVG_STYLE );
      cleanedHtml = StringUtils.replace( cleanedHtml, "<head />", SVG_STYLE );
      return cleanedHtml;
   }

   public String extractRealContent( String sourceHtml ) {
      logger.debug( "Extracting real content for FitNesse Page: " + sourceHtml );
      this.sourceHtml = sourceHtml;

      try{
         correctFailures();
         removeUnparsableElements();
         parseSourceHtml();
         strippContentWithXslt();
      }catch( IOException | TransformerException | ParserConfigurationException | SAXException e ){
         logger.error( "Extracting real page content failed.", e );
      }

      logger.debug( "Stripped content HTML: \n" + strippedContent );

      return strippedContent;
   }

   // protected, private helper mehtods
   private void correctFailures() {
      String[] searchList = { "<header>", "</header>", "<nav ", "</nav>", "<article>", "</article>", "<footer>", "</footer>", "content=\"IE=edge\">", ".css\">", "<li<", "<li <", "&pageTemplate", "&times" };
      String[] replacementList = { "<div id='header'>", "</div>", "<div id='nav' ", "</div>", "<div id='article'>", "</div>", "<div id='footer'>", "</div>", "content=\"IE=edge\"/>", ".css\"/>", "<li><", "<li><", "&amp;pageTemplate", "&amp;times" };
      sourceHtml = StringUtils.replaceEach( sourceHtml, searchList, replacementList );
      logger.debug( "Corrected HTML: \n" + sourceHtml );
   }

   private String node2String( Node node ) throws TransformerFactoryConfigurationError, TransformerException {
      StreamResult xmlOutput = new StreamResult( new StringWriter() );
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
      transformer.transform( new DOMSource( node ), xmlOutput );
      String extractedHtml = xmlOutput.getWriter().toString();
      return extractedHtml;
   }

   private void removeUnparsableElements() {
      String[] searchList = { "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" };
      String[] replacementList = { "" };
      sourceHtml = StringUtils.replaceEach( sourceHtml, searchList, replacementList );
      logger.debug( "Corrected HTML: \n" + sourceHtml );
   }

   @SuppressWarnings( "unused" ) private void strippContent() throws XPathExpressionException, TransformerFactoryConfigurationError, TransformerException {
      String expr = "//div[@id='article']";
      NodeList resultNodeList = (NodeList) xPath.compile( expr ).evaluate( documentDOM, XPathConstants.NODESET );
      for( Node node : asList( resultNodeList ) ){
         strippedContent = node2String( node );
      }
   }

   private void strippContentWithXslt() throws IOException, TransformerException {
      strippedContent = XmlTransformer.transform( documentDOM, resourceLoader.getResource( XSLT_FILE ).getInputStream() );
   }

   private void parseSourceHtml() throws ParserConfigurationException, SAXException, IOException {
      builderFactory = DocumentBuilderFactory.newInstance();
      builderFactory.setNamespaceAware(true);
      domParser = builderFactory.newDocumentBuilder();
      documentDOM = domParser.parse( new ByteArrayInputStream( sourceHtml.getBytes( StandardCharsets.UTF_8 ) ) );
   }
}
