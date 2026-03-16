package me.spencernold.janus.fw;

import me.spencernold.janus.reader.exceptions.ReaderException;
import me.spencernold.janus.reader.exceptions.SyntaxException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record IpRange(int min, int max) {

    private static final Pattern PATTERN = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3}).(\\d{1,3})(?:/(\\d{1,2}))?$");

    public static IpRange parseRange(String cidr) throws ReaderException {
        Matcher matcher = PATTERN.matcher(cidr);
        if (!matcher.find())
            throw new SyntaxException("IP not in CIDR notation: " + cidr);
        int ip = 0 ;
        for (int i = 1; i <= 4; i++) {
            String str = matcher.group(i);
            int value = Integer.parseInt(str);
            if (value < 0 || value > 255)
                throw new SyntaxException("value in IP not in [0-255] range: " + cidr);
            ip = (ip << 8) | value;
        }
        String str = matcher.group(5);
        if (str != null) {
            int prefix = Integer.parseInt(str);
            if (prefix < 0 || prefix > 32)
                throw new SyntaxException("mask in CIDR must be [0-32]: " + cidr);
            int mask = prefix == 0 ? 0 : (-1 << (32 - prefix));
            int network = ip & mask;
            int broadcast = network | ~mask;
            return new IpRange(network, broadcast);
        }
        return new IpRange(ip, ip);
    }
}
