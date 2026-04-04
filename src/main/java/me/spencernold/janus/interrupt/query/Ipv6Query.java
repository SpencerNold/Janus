package me.spencernold.janus.interrupt.query;

import me.spencernold.janus.interrupt.InetFrame;
import me.spencernold.janus.interrupt.Query;

public class Ipv6Query extends Query {

    public static boolean isIntended(String device, InetFrame frame) {
        return false;
    }

    @Override
    public boolean test(InetFrame frame) {
        return false;
    }
}
