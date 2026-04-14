package integration;

import me.spencernold.janus.Sys;
import me.spencernold.janus.address.Ip4Range;
import me.spencernold.janus.address.MacAddress;
import me.spencernold.janus.fw.Protocol;
import me.spencernold.janus.interrupt.Query;
import me.spencernold.janus.interrupt.query.BodyQuery;
import me.spencernold.janus.interrupt.query.EthernetQuery;
import me.spencernold.janus.interrupt.query.Ipv4Query;
import me.spencernold.janus.interrupt.query.TransportAddressQuery;
import me.spencernold.janus.reader.Reader;
import me.spencernold.janus.reader.exceptions.ReaderException;
import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

public class InterruptionParseTest {

    @Test
    public void emptyTestCase() {
        String input = "";
        StringReader reader = new StringReader(input);
        try {
             List<Query> queries = Reader.readInterruptions(reader);
            Assertions.assertTrue(queries.isEmpty());
        } catch (ReaderException e) {
            reader.close();
            throw new RuntimeException(e);
        }
        reader.close();
    }

    @Test
    public void validEthernetTestCase() {
        String input = """
                eth.address = FF:FF:FF:FF:FF:FF
                """;
        List<Query> queries = runValidTestCase(input);
        Assertions.assertEquals(1, queries.size());
        Query q = queries.getFirst();
        Assertions.assertInstanceOf(EthernetQuery.class, q);
        EthernetQuery query = (EthernetQuery) q;
        MacAddress mac = query.getAddress();
        byte[] address = mac.address();
        Assertions.assertEquals(6, address.length);
        for (byte b : address)
            Assertions.assertEquals(-1, b);
    }

    @Test
    public void validIPv4TestCase() {
        String input = """
                ipv4.address = 0.0.0.0
                """;
        List<Query> queries = runValidTestCase(input);
        Assertions.assertEquals(1, queries.size());
        Query q = queries.getFirst();
        Assertions.assertInstanceOf(Ipv4Query.class, q);
        Ipv4Query query = (Ipv4Query) q;
        Ip4Range ip = query.getAddress();
        Assertions.assertEquals(0, ip.max());
        Assertions.assertEquals(0, ip.min());
    }

    @Test
    public void validIPv6TestCase() {
        // TODO Implement when I implement IPv6 (I dont wanna!)
    }

    @Test
    public void validTCPAddressTestCase() {
        String input = """
                tcp.address = 80
                """;
        List<Query> queries = runValidTestCase(input);
        Assertions.assertEquals(1, queries.size());
        Query q = queries.getFirst();
        Assertions.assertInstanceOf(TransportAddressQuery.class, q);
        TransportAddressQuery query = (TransportAddressQuery) q;
        Assertions.assertEquals(Protocol.TCP, query.getProtocol());
        Assertions.assertEquals(80, query.getPort());
    }

    @Test
    public void invalidTCPAddressTestCase() {
        String input = "tcp.address = 999999999";
        StringReader reader = new StringReader(input);
        Assertions.assertThrowsExactly(UnexpectedTokenException.class, () -> Reader.readInterruptions(reader));
        reader.close();
    }

    @Test
    public void validTCPBodyTestCase1() {
        String input = """
                tcp.body contains {0}[0]
                """;
        List<Query> queries = runValidTestCase(input);
        Assertions.assertEquals(1, queries.size());
        Query q = queries.getFirst();
        Assertions.assertInstanceOf(BodyQuery.class, q);
        BodyQuery query = (BodyQuery) q;
        Assertions.assertEquals(Protocol.TCP, query.getProtocol());
        BodyQuery.Test[] tests = query.getTests();
        Assertions.assertEquals(1, tests.length);
        BodyQuery.Test test = tests[0];
        Assertions.assertFalse(test.isInverted());
        Assertions.assertEquals(0, test.getOffset());
        byte[] target = test.getTarget();
        Assertions.assertEquals(1, target.length);
        Assertions.assertEquals(0, target[0]);
    }

    @Test
    public void validTCPBodyTestCase2() {
        String input = """
                tcp.body not contains [0]
                """;
        List<Query> queries = runValidTestCase(input);
        Assertions.assertEquals(1, queries.size());
        Query q = queries.getFirst();
        Assertions.assertInstanceOf(BodyQuery.class, q);
        BodyQuery query = (BodyQuery) q;
        Assertions.assertEquals(Protocol.TCP, query.getProtocol());
        BodyQuery.Test[] tests = query.getTests();
        Assertions.assertEquals(1, tests.length);
        BodyQuery.Test test = tests[0];
        Assertions.assertTrue(test.isInverted());
        Assertions.assertEquals(0, test.getOffset());
        byte[] target = test.getTarget();
        Assertions.assertEquals(1, target.length);
        Assertions.assertEquals(0, target[0]);
    }

    @Test
    public void validTCPBodyTestCase3() {
        String input = """
                tcp.body contains {72}[]
                """;
        List<Query> queries = runValidTestCase(input);
        Assertions.assertEquals(1, queries.size());
        Query q = queries.getFirst();
        Assertions.assertInstanceOf(BodyQuery.class, q);
        BodyQuery query = (BodyQuery) q;
        Assertions.assertEquals(Protocol.TCP, query.getProtocol());
        BodyQuery.Test[] tests = query.getTests();
        Assertions.assertEquals(1, tests.length);
        BodyQuery.Test test = tests[0];
        Assertions.assertFalse(test.isInverted());
        Assertions.assertEquals(72, test.getOffset());
        byte[] target = test.getTarget();
        Assertions.assertEquals(0, target.length);
    }

    @Test
    public void validTCPBodyTestCase4() {
        String input = """
                tcp.body contains {0}[0] and not contains [] and contains {72}[1, 2, 3, 4]
                """;
        List<Query> queries = runValidTestCase(input);
        Assertions.assertEquals(1, queries.size());
        Query q = queries.getFirst();
        Assertions.assertInstanceOf(BodyQuery.class, q);
        BodyQuery query = (BodyQuery) q;
        Assertions.assertEquals(Protocol.TCP, query.getProtocol());
        BodyQuery.Test[] tests = query.getTests();
        Assertions.assertEquals(3, tests.length);

        // Test 1
        BodyQuery.Test test = tests[0];
        Assertions.assertFalse(test.isInverted());
        Assertions.assertEquals(0, test.getOffset());
        byte[] target = test.getTarget();
        Assertions.assertEquals(1, target.length);
        Assertions.assertEquals(0, target[0]);

        // Test 1
        test = tests[1];
        Assertions.assertTrue(test.isInverted());
        Assertions.assertEquals(0, test.getOffset());
        target = test.getTarget();
        Assertions.assertEquals(0, target.length);

        // Test 3
        test = tests[2];
        Assertions.assertFalse(test.isInverted());
        Assertions.assertEquals(72, test.getOffset());
        target = test.getTarget();
        Assertions.assertEquals(4, target.length);
        Assertions.assertEquals(1, target[0]);
        Assertions.assertEquals(2, target[1]);
        Assertions.assertEquals(3, target[2]);
        Assertions.assertEquals(4, target[3]);
    }

    @Test
    public void validUDPAddressTestCase() {
        String input = """
                udp.address = 25565
                """;
        List<Query> queries = runValidTestCase(input);
        Assertions.assertEquals(1, queries.size());
        Query q = queries.getFirst();
        Assertions.assertInstanceOf(TransportAddressQuery.class, q);
        TransportAddressQuery query = (TransportAddressQuery) q;
        Assertions.assertEquals(Protocol.UDP, query.getProtocol());
        Assertions.assertEquals(25565, query.getPort());
    }

    @Test
    public void invalidUDPAddressTestCase() {
        String input = "udp.address = 999999999";
        StringReader reader = new StringReader(input);
        Assertions.assertThrowsExactly(UnexpectedTokenException.class, () -> Reader.readInterruptions(reader));
        reader.close();
    }

    @Test
    public void invalidTestCase() {
        String input = "hey you, you're finally awake";
        StringReader reader = new StringReader(input);
        Assertions.assertThrowsExactly(UnexpectedTokenException.class, () -> Reader.readInterruptions(reader));
        reader.close();
    }

    private List<Query> runValidTestCase(String input) {
        StringReader reader = new StringReader(input);
        try {
            List<Query> queries = Reader.readInterruptions(reader);
            reader.close();
            return queries;
        } catch (ReaderException e) {
            throw new RuntimeException(e);
        }
    }
}
