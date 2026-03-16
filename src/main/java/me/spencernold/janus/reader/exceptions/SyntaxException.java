package me.spencernold.janus.reader.exceptions;

public class SyntaxException extends ReaderException {

    public SyntaxException(String message, Object... args) {
        super(message, args);
    }
}
