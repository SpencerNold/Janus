package me.spencernold.janus.address;

import me.spencernold.janus.reader.exceptions.SyntaxException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record MacAddress(byte[] address) {

    private static final Pattern PATTERN = Pattern.compile("^([A-Fa-f0-9]{2}):([A-Fa-f0-9]{2}):([A-Fa-f0-9]{2}):([A-Fa-f0-9]{2}):([A-Fa-f0-9]{2}):([A-Fa-f0-9]{2})$");

    public boolean test(byte[] mac) {
        if (mac.length != 6 || address.length != 6)
            return false;
        for (int i = 0; i < 6; i++) {
            if (address[i] != mac[i])
                return false;
        }
        return true;
    }

    public static MacAddress parseMac(String address) throws SyntaxException {
        Matcher matcher = PATTERN.matcher(address);
        if (!matcher.find())
            throw new SyntaxException("MAC not in valid notation: " + address);
        byte[] mac = new byte[6];
        for (int i = 0; i < 6; i++) {
            String str = matcher.group(i + 1);
            mac[i] = (byte) Integer.parseInt(str, 16);
        }
        return new MacAddress(mac);
    }
}
