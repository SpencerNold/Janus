package me.spencernold.janus.reader;

import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;

import java.io.IOException;

public abstract class AbstractLexer {

    public StringBuilder currentLine = new StringBuilder();

    public abstract Token yylex() throws IOException, UnexpectedTokenException;
    public abstract String getRemainingLine();
}
