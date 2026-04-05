package me.spencernold.janus.reader.janus;

import me.spencernold.janus.reader.Token;
import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;

%%

%class JLexer
%extends me.spencernold.janus.reader.AbstractLexer
%unicode
%public
%type Token
%line
%column
%yylexthrow UnexpectedTokenException

%{

private Token token(JType type) {
    String text = yytext();
    currentLine.append(text);
    return new Token(type.ordinal(), text);
}

private UnexpectedTokenException error(String message) {
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
MAC        = [A-Fa-f0-9]{2}:[A-Fa-f0-9]{2}:[A-Fa-f0-9]{2}:[A-Fa-f0-9]{2}:[A-Fa-f0-9]{2}:[A-Fa-f0-9]{2}
V4CIDR     = [0-9]+\.[0-9]+\.[0-9]+\.[0-9]+(\/[0-9]+)?
PORT       = [0-9]+
IDENTIFIER = [a-zA-Z_][a-zA-Z0-9_]*

%%

\n                { currentLine.setLength(0); }

{WHITESPACE}      { currentLine.append(yytext()); }

"."               { return token(JType.DOT); }
"eth"             { return token(JType.ETH); }
"ipv4"            { return token(JType.IPV4); }
"ipv6"            { return token(JType.IPV6); }
"tcp"             { return token(JType.TCP); }
"udp"             { return token(JType.UDP); }

"address"         { return token(JType.ADDRESS); }

"="               { return token(JType.EQ_OPER); }

{MAC}             { return token(JType.MAC); }
{V4CIDR}          { return token(JType.V4CIDR); }
{PORT}            { return token(JType.PORT); }

<<EOF>>           { return new Token(JType.EOF.ordinal(), ""); }


{IDENTIFIER}      { currentLine.append(yytext()); throw error("Unknown keyword '" + yytext() + "'"); }
.                 { currentLine.append(yytext()); throw error("Unknown token '" + yytext() + "'"); }