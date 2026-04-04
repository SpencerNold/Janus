package me.spencernold.janus.reader.def;

import me.spencernold.janus.reader.Token;

%%

%class DefLexer
%extends me.spencernold.janus.reader.AbstractLexer
%unicode
%public
%type Token

%{

private Token token(DefType type) {
    return new Token(type.ordinal(), yytext());
}

%}

WHITESPACE = [ \t\r\n]+
IDENTIFIER = [a-zA-Z_][a-zA-Z0-9_]*
NUMBER     = [0-9]+
CIDR       = [0-9]+\.[0-9]+\.[0-9]+\.[0-9]+(\/[0-9]+)?

%%

{WHITESPACE}      {}

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

{IDENTIFIER}      { return token(DefType.IDENTIFIER); }
{NUMBER}          { return token(DefType.NUMBER); }
{CIDR}            { return token(DefType.CIDR); }
<<EOF>>           { return new Token(DefType.EOF.ordinal(), ""); }