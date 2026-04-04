package me.spencernold.janus.reader.def;

public enum DefType {
    EOF,

    SEC_INIT, SEC_RULES,

    DEFAULT, PROTOCOL, PORT,

    TCP, UDP,

    ALLOW, DENY, TARPIT,

    COMMA,

    IDENTIFIER, NUMBER, CIDR
}
