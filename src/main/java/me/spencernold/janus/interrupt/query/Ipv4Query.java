package me.spencernold.janus.interrupt.query;

import me.spencernold.janus.address.IpRange;
import me.spencernold.janus.binding.Network;
import me.spencernold.janus.interrupt.InetFrame;
import me.spencernold.janus.interrupt.Query;

import java.util.List;

public class Ipv4Query extends Query {

    private final IpRange address;

    public Ipv4Query(IpRange address) {
        this.address = address;
    }

    public static boolean isIntended(String device, InetFrame frame) {
        if (frame.getIpVersion() != 4)
            return false;
        int offset = frame.getNetworkLayerStart();
        if (offset == -1)
            return false;
        int length = frame.getLength();
        if (length < (offset + 20)) // Make sure there's space in data for source/destination addresses
            return false;
        byte[] data = frame.getData();
        List<byte[]> systemIPv4s = Network.getSystemIPv4s(device);
        boolean matched = false;
        for (byte[] ipv4 : systemIPv4s) {
            if (ipv4 == null || ipv4.length != 4)
                continue;
            int start = offset + 16;
            if (isSameAddress(ipv4, data, start)) {
                matched = true;
                break;
            }
        }
        return matched;
    }

    @Override
    public boolean test(InetFrame frame) {
        if (frame.getIpVersion() != 4)
            return false;
        int offset = frame.getNetworkLayerStart();
        if (offset == -1)
            return false;
        int length = frame.getLength();
        if (length < (offset + 20)) // Make sure there's space in data for source/destination addresses
            return false;
        byte[] data = frame.getData();
        long address = intifyAddress(data, offset + 12);
        return this.address.test(address);
    }

    private static boolean isSameAddress(byte[] address, byte[] packet, int offset) {
        for (int i = 0; i < 4; i++) {
            if (address[i] != packet[offset + i])
                return false;
        }
        return true;
    }

    private long intifyAddress(byte[] packet, int offset) {
        return ((long) (packet[offset] & 0xFF) << 24) |
                ((long) (packet[offset + 1] & 0xFF) << 16) |
                ((long) (packet[offset + 2] & 0xFF) << 8) |
                ((long) (packet[offset + 3] & 0xFF));
    }

    public IpRange getAddress() {
        return address;
    }
}
