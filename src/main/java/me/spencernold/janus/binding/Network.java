package me.spencernold.janus.binding;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Network extends Binding {

    static {
        ensureBindingLoaded();
    }

    private static final byte[] systemMacAddress = new byte[6];
    private static boolean storedSystemMacAddress;
    private static final List<byte[]> systemIPv4s = new ArrayList<>();
    private static final List<byte[]> systemIPv6s = new ArrayList<>();

    private static NetworkInterface getSystemInterface(String device) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nif = interfaces.nextElement();
                if (nif.getName().equals(device))
                    return nif;
            }
        } catch (SocketException ignored) {
        }
        return null;
    }

    public static byte[] getSystemMacAddress(String device) {
        if (storedSystemMacAddress)
            return systemMacAddress;
        NetworkInterface nif = getSystemInterface(device);
        if (nif == null)
            return null;
        try {
            byte[] mac = nif.getHardwareAddress();
            if (mac == null)
                return null;
            System.arraycopy(mac, 0, systemMacAddress, 0, 6);
            storedSystemMacAddress = true;
            return systemMacAddress;
        } catch (SocketException e) {
            return null;
        }
    }

    public static List<byte[]> getSystemIPv4s(String device) {
        if (!systemIPv4s.isEmpty())
            return systemIPv4s;
        NetworkInterface nif = getSystemInterface(device);
        if (nif == null)
            return new ArrayList<>();
        Enumeration<InetAddress> addresses = nif.getInetAddresses();
        while (addresses.hasMoreElements()) {
            InetAddress address = addresses.nextElement();
            if (address instanceof Inet4Address)
                systemIPv4s.add(address.getAddress());
        }
        return systemIPv4s;
    }

    public static List<byte[]> getSystemIPv6s(String device) {
        if (!systemIPv6s.isEmpty())
            return systemIPv6s;
        NetworkInterface nif = getSystemInterface(device);
        if (nif == null)
            return new ArrayList<>();
        Enumeration<InetAddress> addresses = nif.getInetAddresses();
        while (addresses.hasMoreElements()) {
            InetAddress address = addresses.nextElement();
            if (address instanceof Inet6Address)
                systemIPv6s.add(address.getAddress());
        }
        return systemIPv6s;
    }

    public static int getLinkLayerStart(byte[] data, int length) {
        return 0;
    }

    public static native int getLinkLayerEnd(byte[] data, int length);

    public static int getNetworkLayerStart(byte[] data, int length) {
        return getLinkLayerEnd(data, length);
    }

    public static native int getNetworkLayerEnd(byte[] data, int length);

    public static int getTransportLayerStart(byte[] data, int length) {
        return getNetworkLayerEnd(data, length);
    }

    public static native int getTransportLayerEnd(byte[] data, int length);
}
