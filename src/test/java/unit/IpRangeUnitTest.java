package unit;

import me.spencernold.janus.address.Ip4Range;
import me.spencernold.janus.reader.exceptions.SyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IpRangeUnitTest {


    @Test
    public void parseIPv4TestCase1() {
        String address = "0.0.0.0";
        try {
            Ip4Range range = Ip4Range.parseRange(address);
            Assertions.assertEquals(0, range.min());
            Assertions.assertEquals(0, range.max());
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parseIPv4TestCase2() {
        String address = "0.0.0.0/0";
        try {
            Ip4Range range = Ip4Range.parseRange(address);
            Assertions.assertEquals(0, range.min());
            long l = ((long) Integer.MAX_VALUE) * 2 + 1; // 32 1's
            Assertions.assertEquals(l, range.max());
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parseIPv4TestCase3() {
        String address = "0.0";
        Assertions.assertThrowsExactly(SyntaxException.class, () -> Ip4Range.parseRange(address));
    }

    @Test
    public void parseIPv4TestCase4() {
        String address = "0.0.0.0/";
        Assertions.assertThrowsExactly(SyntaxException.class, () -> Ip4Range.parseRange(address));
    }

    @Test
    public void checkIPv4TestCase1() {
        try {
            Ip4Range range = Ip4Range.parseRange("0.0.0.0/0");
            boolean test = range.test("192.168.0.1");
            Assertions.assertTrue(test);
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void checkIPv4TestCase2() {
        try {
            Ip4Range range = Ip4Range.parseRange("0.0.0.0");
            boolean test = range.test("192.168.0.1");
            Assertions.assertFalse(test);
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void checkIPv4TestCase3() {
        try {
            Ip4Range range = Ip4Range.parseRange("0.0.0.0/0");
            Assertions.assertThrowsExactly(SyntaxException.class, () -> range.test("0.0"));
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
