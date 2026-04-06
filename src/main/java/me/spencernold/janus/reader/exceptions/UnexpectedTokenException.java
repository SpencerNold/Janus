package me.spencernold.janus.reader.exceptions;

import me.spencernold.janus.reader.AbstractLexer;
import me.spencernold.janus.reader.Token;

import java.util.Arrays;

public class UnexpectedTokenException extends ReaderException {

    public UnexpectedTokenException(String format, Object... args) {
        super(format, args);
    }

    @SafeVarargs
    public <T extends Enum<T>> UnexpectedTokenException(AbstractLexer lexer, Token token, T... expected) {
        this(lexer, token, "Unknown token '" + token.value() + "'" + (verbose ? ", expected " + (expected.length == 1 ? expected[0].toString() : Arrays.toString(expected)) : ""));
    }

    public UnexpectedTokenException(AbstractLexer lexer, Token token, String message) {
        super(buildErrorUp(lexer, message, token.line(), token.column(), token.length()));
    }

    public UnexpectedTokenException(AbstractLexer lexer, String message, int line, int column, int length) {
        super(buildErrorUp(lexer, message, line, column, length));
    }

    private static String buildErrorUp(AbstractLexer lexer, String message, int line, int column, int length) {
        String error = message + " at line " + (line + 1);
        String lineText = line >= lexer.fullLines.size() ? (lexer.currentLine + lexer.getRemainingLine()) : lexer.fullLines.get(line);
        String pointer = " ".repeat(Math.max(0, column)) + "^".repeat(Math.max(0, length));
        return String.join("\n", error, lineText, pointer);
    }
}
