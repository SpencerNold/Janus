package me.spencernold.janus.interrupt.query;

import me.spencernold.janus.address.MacAddress;
import me.spencernold.janus.binding.Network;
import me.spencernold.janus.interrupt.InetFrame;
import me.spencernold.janus.interrupt.Query;

public class EthernetQuery extends Query {

    private final MacAddress address;

    public EthernetQuery(MacAddress address) {
        this.address = address;
    }

    public static boolean isIntended(String device, InetFrame frame) {
        int offset = frame.getLinkLayerStart();
        if (offset == -1)
            return false;
        int length = frame.getLength();
        if (length < 6)
            return false;
        byte[] mac = Network.getSystemMacAddress(device);
        if (mac == null)
            return false;
        byte[] data = frame.getData();
        return isSameAddress(mac, data, offset);
    }

    @Override
    public boolean test(InetFrame frame) {
        int offset = frame.getLinkLayerStart();
        if (offset == -1)
            return false;
        int length = frame.getLength();
        if (length < 12)
            return false;
        byte[] data = frame.getData();
        return isSameAddress(address.address(), data, offset + 6);
    }

    public MacAddress getAddress() {
        return address;
    }

    private static boolean isSameAddress(byte[] address, byte[] packet, int offset) {
        for (int i = 0; i < 6; i++) {
            if (address[i] != packet[offset + i])
                return false;
        }
        return true;
    }
}
