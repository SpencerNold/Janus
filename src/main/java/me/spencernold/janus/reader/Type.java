package me.spencernold.janus.reader;

public enum Type {
    EOF,

    SEC_INIT, SEC_RULES,

    DEFAULT, PROTOCOL, PORT,

    TCP, UDP,

    ALLOW, DENY, TARPIT,

    COMMA,

    IDENTIFIER, NUMBER, CIDR
}
