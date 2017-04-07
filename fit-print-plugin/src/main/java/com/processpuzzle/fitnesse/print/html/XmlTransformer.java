/*
Name: 
    - XmlTransformer

Description: 
    -  

Requires:
    - 
    
Provides:
    - 

Part of: ProcessPuzzle Framework, Domain and Business Model Ready Architecture. Provides content, workflow and social networking functionality. 
http://www.processpuzzle.com

ProcessPuzzle - Content and Workflow Management Integration Business Platform

Author(s): 
    - Zsolt Zsuffa

Copyright: (C) 2011 This program is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 3 of the License, 
or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.processpuzzle.fitnesse.print.html;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlTransformer {

   public static String transform( String xml, String xslt ) throws IOException, TransformerException, ParserConfigurationException, SAXException {
      return transform( xml, new ByteArrayInputStream( xslt.getBytes( StandardCharsets.UTF_8 ) ) );
   }

   public static String transform( Document xml, InputStream xslt ) throws IOException, TransformerException {
      StringWriter out = new StringWriter();

      try{
         TransformerFactory factory = TransformerFactory.newInstance();
         Transformer transformer = factory.newTransformer( new StreamSource( xslt ) );

         Source src = new DOMSource( xml );
         Result res = new StreamResult( out );

         transformer.transform( src, res );
         return out.toString();
      }finally{
         out.close();
      }
   }

   public static String transform( String xml, InputStream xslt ) throws IOException, TransformerException, ParserConfigurationException, SAXException {
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder domParser = builderFactory.newDocumentBuilder();
      Document domDocument = domParser.parse( xml );
      return transform( domDocument, xslt );
   }
}
