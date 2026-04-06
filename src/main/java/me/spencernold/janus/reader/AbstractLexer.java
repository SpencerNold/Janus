package me.spencernold.janus.reader;

import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLexer {

    public final List<String> fullLines = new ArrayList<>();
    public final StringBuilder currentLine = new StringBuilder();

    public abstract UnexpectedTokenException error(String message);
    public abstract Token yylex() throws IOException, UnexpectedTokenException;
    public abstract String getRemainingLine();

    public void cleanup() {
        fullLines.clear();
        currentLine.setLength(0);
    }
}
