package com.processpuzzle.fitnesse.print;

import com.processpuzzle.fitnesse.print.GraphicsWikiParserException;

import fitnesse.wikitext.parser.Matcher;
import fitnesse.wikitext.parser.Maybe;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.Rule;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolType;
import fitnesse.wikitext.parser.Translation;
import fitnesse.wikitext.parser.Translator;

public class FitToPdfSymbol extends SymbolType implements Rule, Translation {
   private static final String SYMBOL_NAME = "FitToPdf";
   private String content;
   private final SymbolType symbolEnd;

   // constructors
   public FitToPdfSymbol() {
      super( "start" + SYMBOL_NAME );
      wikiMatcher( new Matcher().startLine().ignoreWhitespace().string( "!start" + SYMBOL_NAME ) );
      this.symbolEnd = new SymbolType( "end" + SYMBOL_NAME ).wikiMatcher( new Matcher().startLine().ignoreWhitespace().string( "!end" + SYMBOL_NAME ) );

      wikiRule( this );
      htmlTranslation( this );
   }

   // public accessors and mutators
   @Override public String toTarget( Translator translator, Symbol symbol ) {
      return "Print wiki pages as PDF or Word.";
   }

   @Override public Maybe<Symbol> parse( Symbol current, Parser parser ) {
      try{
         this.parseFromWiki( parser );
      }catch( GraphicsWikiParserException e ){
         e.printStackTrace();
      }
      return new Maybe<Symbol>( current );
   }
   
   public void parseFromWiki(Parser parser) throws GraphicsWikiParserException {

      String propertiesText = parser.parseToAsString(SymbolType.Newline).getValue();
      
      if (parser.atEnd()) {
          throw new GraphicsWikiParserException("No new line after !start" + SYMBOL_NAME);
      }
      
      content = parser.parseLiteral( this.symbolEnd );
      if (parser.atEnd()) {
          throw new GraphicsWikiParserException("No !end" + SYMBOL_NAME + " found");
      }

      if (content == null || content.isEmpty()) {
          throw new GraphicsWikiParserException("No content for " + SYMBOL_NAME);
      }
      
      System.out.println( "propertiesText: " + propertiesText );
      System.out.println( "content: " + this.content );
  }
}
