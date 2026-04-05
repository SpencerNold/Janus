package me.spencernold.janus.reader.exceptions;

import me.spencernold.janus.reader.AbstractLexer;
import me.spencernold.janus.reader.janus.JLexer;

public class UnexpectedTokenException extends ReaderException {

    public UnexpectedTokenException(String format, Object... args) {
        super(format, args);
    }

    public UnexpectedTokenException(AbstractLexer lexer, String message, int line, int column, int length) {
        super(buildErrorUp(lexer, message, line, column, length));
    }

    private static String buildErrorUp(AbstractLexer lexer, String message, int line, int column, int length) {
        String error = message + " at line " + (line + 1);
        String lineText = lexer.currentLine.toString() + lexer.getRemainingLine();
        String pointer = " ".repeat(Math.max(0, column)) + "^".repeat(Math.max(0, length));
        return String.join("\n", error, lineText, pointer);
    }
}
