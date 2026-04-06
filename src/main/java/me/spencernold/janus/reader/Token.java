package me.spencernold.janus.reader;

public record Token(int type, String value, int line, int column) {
    public boolean is(int type) {
        return type == type();
    }

    public <T extends Enum<T>> boolean is(T type) {
        return is(type.ordinal());
    }

    public int length() {
        return value.length();
    }

    public Token copy() {
        return new Token(type, value, line, column);
    }
}
