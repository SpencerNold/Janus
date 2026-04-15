package unit.frame;

import me.spencernold.janus.interrupt.InetFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class Ipv4InetFrameUnitTest extends AbstractInetFrameUnitTest {

    @Test
    public void parseIPv4HeaderTestCase1() {
        // empty ipv4 header test case
        byte[] data = new byte[] {};
        InetFrame frame = new InetFrame(data, data.length);
        int start = frame.getNetworkLayerStart();
        Assertions.assertEquals(-1, start);
        int end = frame.getTransportLayerStart();
        Assertions.assertEquals(-1, end);
    }

    @Test
    public void parseIPv4HeaderTestCase2() {
        // valid ipv4 header test case
        byte[] ethernetFrame = Arrays.copyOfRange(semiValidV4NetworkPacket, 0, 34);
        InetFrame frame = new InetFrame(ethernetFrame, ethernetFrame.length);
        int start = frame.getNetworkLayerStart();
        Assertions.assertEquals(14, start);
        int end = frame.getTransportLayerStart();
        Assertions.assertEquals(34, end);
    }

    @Test
    public void parseIPv4HeaderTestCase3() {
        // fractured packet test case
        int counter = 0;
        for (int i = 34; i >= 0; i--) {
            byte[] ethernetFrame = Arrays.copyOfRange(semiValidV4NetworkPacket, 0, i);
            InetFrame frame = new InetFrame(ethernetFrame, ethernetFrame.length);
            int start = frame.getNetworkLayerStart();
            Assertions.assertEquals(14, start);
            int end = frame.getTransportLayerStart();
            if (end == -1)
                break;
            counter++;
        }
        Assertions.assertEquals(1, counter);
    }
}
