package unit.frame;

public class AbstractInetFrameUnitTest {

    private static final byte[] validEthernetHeader = new byte[] {
            // Destination MAC
            0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
            // Source MAC
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            // EtherType
            0x08, 0x00 // Leave as IPv4 for now, TODO change when support for IPv6 is added
    };

    private static final byte[] semiValidIPv4Header = new byte[] {
            (byte) 0x45,               // Version=4, IHL=5 (5*4 = 20 bytes)
            (byte) 0x00,               // DSCP/ECN
            (byte) 0x00, (byte) 0x28,  // Total Length = 40 bytes (header + payload)
            (byte) 0x12, (byte) 0x34,  // Identification
            (byte) 0x40, (byte) 0x00,  // Flags (DF set) + Fragment Offset
            (byte) 0x40,               // TTL = 64
            (byte) 0x06,               // Protocol = TCP
            (byte) 0x00, (byte) 0x00,  // Header checksum (can be 0 for testing)
            (byte) 0xC0, (byte) 0xA8, (byte) 0x01, (byte) 0x01, // Source IP: 192.168.1.1
            (byte) 0xC0, (byte) 0xA8, (byte) 0x01, (byte) 0x02  // Dest IP:   192.168.1.2
    };

    protected static final byte[] semiValidV4NetworkPacket = join(
            validEthernetHeader,
            semiValidIPv4Header
    );

    private static byte[] join(byte[]... data) {
        int length = 0;
        for (byte[] bytes : data)
            length += bytes.length;
        byte[] joined = new byte[length];
        int offset = 0;
        for (byte[] bytes : data) {
            System.arraycopy(bytes, 0, joined, offset, bytes.length);
            offset += bytes.length;
        }
        return joined;
    }
}
