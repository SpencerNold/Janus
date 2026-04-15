package me.spencernold.janus.address;

import me.spencernold.janus.reader.exceptions.SyntaxException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record MacAddress(byte[] address) {

    private static final Pattern PATTERN = Pattern.compile("^([A-Fa-f0-9]{2}):([A-Fa-f0-9]{2}):([A-Fa-f0-9]{2}):([A-Fa-f0-9]{2}):([A-Fa-f0-9]{2}):([A-Fa-f0-9]{2})$");

    /**
     * Tests if this record matches the value given as a parameter.
     *
     * @param mac address to check against the record
     * @return true if the parameter matches, false otherwise
     */
    public boolean test(byte[] mac) {
        if (mac.length != 6 || address.length != 6)
            return false;
        for (int i = 0; i < 6; i++) {
            if (address[i] != mac[i])
                return false;
        }
        return true;
    }

    /**
     * Parses MAC address notation from a string in the pattern of 6 1-byte hex values with a ':' as the deliminator.
     *
     * @param address String hex representation of a mac address with ':' as the deliminator
     * @return MacAddress record of the input address
     * @throws SyntaxException should the MAC address not be in proper format
     */
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
