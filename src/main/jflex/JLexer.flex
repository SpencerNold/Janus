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
    return new Token(type.ordinal(), yytext());
}

private UnexpectedTokenException error(String message) {
    return new UnexpectedTokenException(
        message + " at line " + (yyline + 1) + ", column " + (yycolumn + 1)
    );
}

%}

WHITESPACE = [ \t\r\n]+
MAC        = [A-Fa-f0-9]{2}:[A-Fa-f0-9]{2}:[A-Fa-f0-9]{2}:[A-Fa-f0-9]{2}:[A-Fa-f0-9]{2}:[A-Fa-f0-9]{2}
V4CIDR     = [0-9]+\.[0-9]+\.[0-9]+\.[0-9]+(\/[0-9]+)?
PORT       = [0-9]+
IDENTIFIER = [a-zA-Z_][a-zA-Z0-9_]*

%%

{WHITESPACE}      {}

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


{IDENTIFIER}      { throw error("Unknown keyword '" + yytext() + "'"); }
.                 { throw error("Unknown token '" + yytext() + "'"); }