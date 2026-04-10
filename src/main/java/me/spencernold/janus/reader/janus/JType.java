package me.spencernold.janus.reader.janus;

public enum JType {
    EOF,
    DOT,

    ETH,
    IPV4,
    IPV6,
    TCP,
    UDP,

    ADDRESS, BODY,

    MAC, V4CIDR, NUMBER,

    HEX_ARR, STRING,

    EQ_OPER, NOT_OPER, CONTAINS,

    AND,

    O_BRACKET, COMMA, C_BRACKET, O_CURLY, C_CURLY;

    @Override
    public String toString() {
        return "'" + name().toLowerCase() + "'";
    }
}
