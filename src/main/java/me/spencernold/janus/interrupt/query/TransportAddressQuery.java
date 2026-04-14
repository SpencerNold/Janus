package me.spencernold.janus.interrupt.query;

import me.spencernold.janus.fw.Firewall;
import me.spencernold.janus.fw.Protocol;
import me.spencernold.janus.interrupt.InetFrame;
import me.spencernold.janus.interrupt.Query;

public class TransportAddressQuery extends Query {

    private final Protocol protocol;
    private final int port;

    public TransportAddressQuery(Protocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
    }

    public static boolean isIntended(Firewall firewall, InetFrame frame) {
        if (frame.getProtocol() != firewall.protocol())
            return false;
        int offset = frame.getTransportLayerStart();
        if (offset == -1)
            return false;
        if (frame.getLength() < (offset + 4))
            return false;
        int port = getPort(frame.getData(), offset + 2);
        return firewall.port() == port;
    }

    @Override
    public boolean test(InetFrame frame) {
        if (frame.getProtocol() != protocol)
            return false;
        int offset = frame.getTransportLayerStart();
        if (offset == -1)
            return false;
        if (frame.getLength() < (offset + 2))
            return false;
        int port = getPort(frame.getData(), offset);
        return this.port == port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public int getPort() {
        return port;
    }

    private static int getPort(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }
}
