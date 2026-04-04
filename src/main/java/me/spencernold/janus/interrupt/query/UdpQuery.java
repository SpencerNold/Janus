package me.spencernold.janus.interrupt.query;

import me.spencernold.janus.fw.Firewall;
import me.spencernold.janus.fw.Protocol;
import me.spencernold.janus.interrupt.InetFrame;
import me.spencernold.janus.interrupt.Query;

public class UdpQuery extends Query {

    private final int port;

    public UdpQuery(int port) {
        this.port = port;
    }

    public static boolean isIntended(Firewall firewall, InetFrame frame) {
        if (frame.getProtocol() != Protocol.UDP)
            return false;
        int offset = frame.getTransportLayerStart();
        if (offset == -1)
            return false;
        if (frame.getLength() < (offset + 2))
            return false;
        int port = getPort(frame.getData(), offset);
        return firewall.port() == port;
    }

    @Override
    public boolean test(InetFrame frame) {
        if (frame.getProtocol() != Protocol.UDP)
            return false;
        int offset = frame.getTransportLayerStart();
        if (offset == -1)
            return false;
        if (frame.getLength() < (offset + 4))
            return false;
        int port = getPort(frame.getData(), offset + 2);
        return this.port == port;
    }

    private static int getPort(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }
}
