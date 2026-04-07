package me.spencernold.janus.address;

import me.spencernold.janus.reader.exceptions.SyntaxException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Ip4Range(long min, long max) {

    private static final Pattern PATTERN = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3}).(\\d{1,3})(?:/(\\d{1,2}))?$");

    public boolean test(long address) {
        return address >= min && address <= max;
    }

    public boolean test(String address) throws SyntaxException {
        return test(parseRange(address));
    }

    public boolean test(Ip4Range range) {
        if (range.min > range.max)
            return false; // Bad input
        return range.min >= min && range.max <= max;
    }

    public static Ip4Range parseRange(String cidr) throws SyntaxException {
        Matcher matcher = PATTERN.matcher(cidr);
        if (!matcher.find())
            throw new SyntaxException("IP not in CIDR notation: " + cidr);
        long ip = 0 ;
        for (int i = 1; i <= 4; i++) {
            String str = matcher.group(i);
            int value = Integer.parseInt(str);
            if (value < 0 || value > 255)
                throw new SyntaxException("value in IP not in [0-255] range: " + cidr);
            ip = (ip << 8) | (long) value;
        }
        String str = matcher.group(5);
        if (str != null) {
            int prefix = Integer.parseInt(str);
            if (prefix < 0 || prefix > 32)
                throw new SyntaxException("mask in CIDR must be [0-32]: " + cidr);
            long mask = prefix == 0 ? 0 : (-1L << (32 - prefix)) & 0xFFFFFFFFL;
            long network = ip & mask;
            long broadcast = (network | ~mask) & 0xFFFFFFFFL;
            return new Ip4Range(network, broadcast);
        }
        return new Ip4Range(ip, ip);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ip4Range ip))
            return false;
        return ip.min == min && ip.max == max;
    }
}
