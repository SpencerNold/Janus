package me.spencernold.janus.address;

import me.spencernold.janus.reader.exceptions.SyntaxException;

public record Ip6Range(byte[] address, byte[] mask) {

    public boolean test(byte[] data) {
        return false;
    }

    public static Ip6Range parseRange(String cidr) throws SyntaxException {
        return null;
    }
}
