package me.spencernold.janus.reader.exceptions;

public abstract class ReaderException extends Exception {

    public static boolean verbose = true;

    public ReaderException(String message, Object... args) {
        super(String.format(message, args));
    }
}
