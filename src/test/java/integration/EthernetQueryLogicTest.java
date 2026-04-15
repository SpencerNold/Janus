package integration;

import me.spencernold.janus.address.MacAddress;
import me.spencernold.janus.interrupt.InetFrame;
import me.spencernold.janus.interrupt.query.EthernetQuery;
import me.spencernold.janus.reader.exceptions.SyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EthernetQueryLogicTest {

    // TODO In progress

    private static final byte[] exampleValidPacket = new byte[] {
            // Destination MAC
            0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
            // Source MAC
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            // tag
            0x00, 0x00, 0x00, 0x00
    };

    //@Test
    public void emptyEthernetQueryTestCase() {
        try {
            MacAddress address = MacAddress.parseMac("00:00:00:00:00:00");
            EthernetQuery query = new EthernetQuery(address);
            InetFrame frame = new InetFrame(new byte[0], 0);
            boolean result = query.test(frame);
            Assertions.assertFalse(result);
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    //@Test
    public void validEthernetQueryTestCase() {
        try {
            MacAddress address = MacAddress.parseMac("00:00:00:00:00:00");
            EthernetQuery query = new EthernetQuery(address);
            InetFrame frame = new InetFrame(exampleValidPacket, exampleValidPacket.length);
            boolean result = query.test(frame);
            Assertions.assertTrue(result);
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    //@Test
    public void invalidEthernetQueryTestCase() {
        try {
            MacAddress address = MacAddress.parseMac("FF:FF:FF:FF:FF:FF");
            EthernetQuery query = new EthernetQuery(address);
            InetFrame frame = new InetFrame(exampleValidPacket, exampleValidPacket.length);
            boolean result = query.test(frame);
            Assertions.assertTrue(result);
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
