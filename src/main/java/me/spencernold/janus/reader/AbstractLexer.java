package me.spencernold.janus.reader;

import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;

import java.io.IOException;

public abstract class AbstractLexer {
    public abstract Token yylex() throws IOException, UnexpectedTokenException;
}
