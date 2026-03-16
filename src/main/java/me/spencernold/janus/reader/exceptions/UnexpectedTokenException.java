package me.spencernold.janus.reader.exceptions;

public class UnexpectedTokenException extends ReaderException {

    public UnexpectedTokenException(String format, Object... args) {
        super(format, args);
    }
}
