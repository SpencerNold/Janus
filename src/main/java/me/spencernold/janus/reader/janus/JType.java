package me.spencernold.janus.reader.janus;

public enum JType {
    EOF,
    DOT,

    ETH,
    IPV4,
    IPV6,
    TCP,
    UDP,

    ADDRESS,

    MAC, V4CIDR, PORT,

    EQ_OPER, NEQ_OPER
}
