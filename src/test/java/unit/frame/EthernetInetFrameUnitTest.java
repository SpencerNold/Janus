package unit.frame;

import me.spencernold.janus.interrupt.InetFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class EthernetInetFrameUnitTest extends AbstractInetFrameUnitTest {

    @Test
    public void parseEthernetHeaderTestCase1() {
        // empty ethernet header test case
        byte[] data = new byte[] {};
        InetFrame frame = new InetFrame(data, data.length);
        int start = frame.getLinkLayerStart();
        Assertions.assertEquals(0, start);
        int end = frame.getNetworkLayerStart();
        Assertions.assertEquals(-1, end);
    }

    @Test
    public void parseEthernetHeaderTestCase2() {
        // valid ethernet header test case
        byte[] ethernetFrame = Arrays.copyOfRange(semiValidV4NetworkPacket, 0, 14);
        InetFrame frame = new InetFrame(ethernetFrame, ethernetFrame.length);
        int start = frame.getLinkLayerStart();
        Assertions.assertEquals(0, start);
        int end = frame.getNetworkLayerStart();
        Assertions.assertEquals(14, end);
    }

    @Test
    public void parseEthernetHeaderTestCase3() {
        // fractured packet test case
        int counter = 0;
        for (int i = 14; i >= 0; i--) {
            byte[] ethernetFrame = Arrays.copyOfRange(semiValidV4NetworkPacket, 0, i);
            InetFrame frame = new InetFrame(ethernetFrame, ethernetFrame.length);
            int start = frame.getLinkLayerStart();
            Assertions.assertEquals(0, start);
            int end = frame.getNetworkLayerStart();
            if (end == -1)
                break;
            counter++;
        }
        Assertions.assertEquals(1, counter);
    }

    @Test
    public void parseEthernetHeaderTestCase4() {
        // fractured packet (reverse) test case
        int counter = 0;
        for (int i = 0; i <= 14; i++) {
            byte[] ethernetFrame = Arrays.copyOfRange(semiValidV4NetworkPacket, 0, i);
            InetFrame frame = new InetFrame(ethernetFrame, ethernetFrame.length);
            int start = frame.getLinkLayerStart();
            Assertions.assertEquals(0, start);
            int end = frame.getNetworkLayerStart();
            if (end != -1)
                break;
            counter++;
        }
        Assertions.assertEquals(14, counter);
    }
}
