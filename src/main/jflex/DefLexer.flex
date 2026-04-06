package me.spencernold.janus.reader.def;

import me.spencernold.janus.reader.Token;
import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;

%%

%class DefLexer
%extends me.spencernold.janus.reader.AbstractLexer
%unicode
%public
%type Token
%line
%column
%yylexthrow UnexpectedTokenException

%{

private Token token(DefType type) {
      String text = yytext();
      currentLine.append(text);
    return new Token(type.ordinal(), text, yyline, yycolumn);
}

public UnexpectedTokenException error(String message) {
    return new UnexpectedTokenException(this, message, yyline, yycolumn, yytext().length());
}

public String getRemainingLine() {
      StringBuilder sb = new StringBuilder();
      int pos = zzMarkedPos;
      while (pos < zzEndRead) {
          char c = zzBuffer[pos];
          if (c == '\n') break;
          sb.append(c);
          pos++;
      }
      return sb.toString();
}

%}

WHITESPACE = [ \t\r]+
IDENTIFIER = [a-zA-Z_][a-zA-Z0-9_]*
NUMBER     = [0-9]+
CIDR       = [0-9]+\.[0-9]+\.[0-9]+\.[0-9]+(\/[0-9]+)?

%%

\n                { fullLines.add(currentLine.toString()); currentLine.setLength(0); }

{WHITESPACE}      { currentLine.append(yytext()); }

".init"           { return token(DefType.SEC_INIT); }
".rules"          { return token(DefType.SEC_RULES); }

"default:"        { return token(DefType.DEFAULT); }
"protocol:"       { return token(DefType.PROTOCOL); }
"port:"           { return token(DefType.PORT); }

"TCP"             { return token(DefType.TCP); }
"UDP"             { return token(DefType.UDP); }

"ALLOW"           { return token(DefType.ALLOW); }
"DENY"            { return token(DefType.DENY); }
"TARPIT"          { return token(DefType.TARPIT); }

","               { return token(DefType.COMMA); }

"localhost"       { return new Token(DefType.CIDR.ordinal(), "127.0.0.1", yyline, yycolumn); }

{IDENTIFIER}      { return token(DefType.IDENTIFIER); }
{NUMBER}          { return token(DefType.NUMBER); }
{CIDR}            { return token(DefType.CIDR); }
<<EOF>>           { return new Token(DefType.EOF.ordinal(), "", yyline, yycolumn); }

.                 { currentLine.append(yytext()); throw error("Unknown token '" + yytext() + "'"); }