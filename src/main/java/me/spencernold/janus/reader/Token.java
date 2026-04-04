package me.spencernold.janus.reader;

public record Token(int type, String value) {
    public boolean is(int type) {
        return type == type();
    }

    public <T extends Enum<T>> boolean is(T type) {
        return is(type.ordinal());
    }
}
