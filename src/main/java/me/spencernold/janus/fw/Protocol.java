package me.spencernold.janus.fw;

public enum Protocol {
    TCP, UDP;

    public static Protocol parseProtocol(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
