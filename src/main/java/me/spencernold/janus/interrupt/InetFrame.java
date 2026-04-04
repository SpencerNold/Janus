package me.spencernold.janus.interrupt;

import me.spencernold.janus.fw.Protocol;

public class InetFrame {

    private final byte[] data;
    private final int length;

    private int linkLayerStart = -1;
    private int networkLayerStart = -1;
    private int transportLayerStart = -1;

    private int ipv;
    private Protocol protocol;

    public InetFrame(byte[] data, int length) {
        this.data = data;
        this.length = length;
        this.parse();
    }

    private void parse() {
        linkLayerStart = 0;
        parseEthernetHeader();
        parseNetworkHeader();
    }

    private void parseEthernetHeader() {
        if (linkLayerStart == -1)
            return;
        int offset = linkLayerStart;
        if (length < 14)
            return;
        int etherType = ((data[offset + 12] & 0xFF) << 8) | (data[offset + 13] & 0xFF);
        if (etherType == 0x8100)
            networkLayerStart = offset + 18;
        else
            networkLayerStart = offset + 14;
    }

    private void parseNetworkHeader() {
        if (networkLayerStart == -1)
            return;
        int offset = networkLayerStart;
        if (length < (offset + 1))
            return;
        int v_ihl = data[offset] & 0xFF;
        int ihl = (v_ihl & 0x0F);
        ipv = (v_ihl >> 4);
        if (ipv == 6) {
            if (length < (offset + 6))
                return;
            byte next = data[offset + 6];
            int counter = 40;
            while (true) {
                int offs = offset + counter;
                if (length < offs)
                    return;
                if (next == 6 || next == 17 || next == 58) {
                    protocol = Protocol.parseProtocol(next);
                    transportLayerStart = offs;
                    return;
                } else if (next == 0 || next == 43 || next == 60) {
                    if (length < (offs + 2))
                        return;
                    byte headerLength = data[offs + 1];
                    int extensionLength = (headerLength + 1) * 8;
                    next = data[offs];
                    counter += extensionLength;
                } else if (next == 44) {
                    if (length < (offs + 8))
                        return;
                    next = data[offs];
                    counter += 8;
                } else if (next == 50 || next == 51) {
                    // ESP or AH, don't really care
                    transportLayerStart = offs;
                    return;
                } else {
                    return;
                }
            }
        } else if (ipv == 4) {
            if (length < (offset + 20))
                return;
            int headerLength = ihl * 4;
            if (length < (offset + headerLength))
                return;
            protocol = Protocol.parseProtocol(data[offset + 9]);
            transportLayerStart = offset + (ihl * 4);
        }
    }

    public int getLinkLayerStart() {
        return linkLayerStart;
    }

    public int getNetworkLayerStart() {
        return networkLayerStart;
    }

    public int getTransportLayerStart() {
        return transportLayerStart;
    }

    public int getIpVersion() {
        return ipv;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public int getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }
}
