package unit;

import me.spencernold.janus.address.MacAddress;
import me.spencernold.janus.reader.exceptions.SyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MacAddressUnitTest {

    @Test
    public void parseMacTestCase1() {
        String address = "FF:FF:FF:FF:FF:FF";
        try {
            MacAddress mac = MacAddress.parseMac(address);
            byte[] bytes = mac.address();
            for (byte b : bytes)
                Assertions.assertEquals(-1, b);
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parseMacTestCase2() {
        String address = "G0:00:00:00:00:00";
        Assertions.assertThrowsExactly(SyntaxException.class, () -> MacAddress.parseMac(address));
    }

    @Test
    public void parseMacTestCase3() {
        String address = "00:00";
        Assertions.assertThrowsExactly(SyntaxException.class, () -> MacAddress.parseMac(address));
    }

    @Test
    public void checkMacTestCase1() {
        MacAddress mac = new MacAddress(new byte[] {-1, -1, -1, -1, -1, -1});
        boolean test = mac.test(new byte[] {-1, -1, -1, -1, -1, -1});
        Assertions.assertTrue(test);
    }
}
