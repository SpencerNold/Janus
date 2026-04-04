package me.spencernold.janus.fw;

public enum Protocol {

    TCP(6), UDP(17);

    private final int networkValue;

    Protocol(int networkValue) {
        this.networkValue = networkValue;
    }

    public static Protocol parseProtocol(int networkValue) {
        for (Protocol protocol : values()) {
            if (protocol.networkValue == networkValue)
                return protocol;
        }
        return null;
    }

    public static Protocol parseProtocol(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
