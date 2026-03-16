package me.spencernold.janus.reader;

%%

%class Lexer
%unicode
%public
%type Token

%{

private Token token(Type type) {
    return new Token(type, yytext());
}

%}

WHITESPACE = [ \t\r\n]+
IDENTIFIER = [a-zA-Z_][a-zA-Z0-9_]*
NUMBER     = [0-9]+
CIDR       = [0-9]+\.[0-9]+\.[0-9]+\.[0-9]+(\/[0-9]+)?

%%

{WHITESPACE}      {}

".init"           { return token(Type.SEC_INIT); }
".rules"          { return token(Type.SEC_RULES); }

"default:"        { return token(Type.DEFAULT); }
"protocol:"       { return token(Type.PROTOCOL); }
"port:"           { return token(Type.PORT); }

"TCP"             { return token(Type.TCP); }
"UDP"             { return token(Type.UDP); }

"ALLOW"           { return token(Type.ALLOW); }
"DENY"            { return token(Type.DENY); }
"TARPIT"          { return token(Type.TARPIT); }

","               { return token(Type.COMMA); }

{IDENTIFIER}      { return token(Type.IDENTIFIER); }
{NUMBER}          { return token(Type.NUMBER); }
{CIDR}            { return token(Type.CIDR); }
<<EOF>>           { return new Token(Type.EOF, ""); }