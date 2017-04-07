package com.processpuzzle.fitnesse.print.plugin;

import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fitnesse.wikitext.parser.Matcher;
import fitnesse.wikitext.parser.Maybe;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.Rule;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolType;
import fitnesse.wikitext.parser.Translation;
import fitnesse.wikitext.parser.Translator;

@Component
public class FitToPdfSymbol extends SymbolType implements Rule, Translation {
   private static final String DISPLAY_TEXT_TEMPLATE = "<a class=\"FitToPdf\" href=\"/files/{0}\">View PDF</a>";
   private static final String SYMBOL_NAME = "FitToPdf";
   private static final Logger logger = LoggerFactory.getLogger( FitToPdfSymbol.class );
   private String content;
   private String displayText;
   @Autowired private Properties fitToPdfProperties;
   @Autowired FitToPdfTranslation fitToPdfTranslation;
   private final SymbolType symbolEnd;

   // constructors
   public FitToPdfSymbol() {
      super( "start" + SYMBOL_NAME );
      wikiMatcher( new Matcher().startLine().ignoreWhitespace().string( "!start" + SYMBOL_NAME ) );
      this.symbolEnd = new SymbolType( "end" + SYMBOL_NAME ).wikiMatcher( new Matcher().startLine().ignoreWhitespace().string( "!end" + SYMBOL_NAME ) );

      wikiRule( this );
      htmlTranslation( this );
      logger.debug( "FitToPdfSymbol is instantiated and initialized." );
   }

   // public accessors and mutators
   @Override public String toTarget( Translator translator, Symbol symbol ) {
      weaveInFilePath( this.fitToPdfTranslation.translate( translator.getPage(), fitToPdfProperties ) );
      return displayText;
   }

   @Override public Maybe<Symbol> parse( Symbol current, Parser parser ) {
      this.parseFromWiki( parser );
      this.parsePropertiesString();
      logger.debug( "Parsed content: " + this.content );
      return new Maybe<Symbol>( current );
   }

   // properties
   // @formatter:off
   public String getDisplayText(){ return displayText; }
   // @formatter:on

   // protected, private helper methods
   private void parseFromWiki( Parser parser ) {
      parser.parseToAsString( SymbolType.Newline ).getValue();

      if( parser.atEnd() ){
         this.displayText = "No new line after !start" + SYMBOL_NAME;
      }else{
         this.content = parser.parseLiteral( this.symbolEnd );

         if( parser.atEnd() ){
            this.displayText = "No !end" + SYMBOL_NAME + " found";
         }else if( this.content == null || this.content.isEmpty() ){
            this.displayText = "No content for " + SYMBOL_NAME;
         }
      }
   }

   private Properties parsePropertiesString() {
      try{
         fitToPdfProperties.load( new StringReader( this.content ) );
      }catch( IOException e ){
         this.displayText = "Fit to PDF configuration properties expected like: includeChildPages=true";
      }
      return fitToPdfProperties;
   }

   private void weaveInFilePath( String outputFilePath ) {
      this.displayText = MessageFormat.format( DISPLAY_TEXT_TEMPLATE, outputFilePath );
   }
}
