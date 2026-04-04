package integration;

import me.spencernold.janus.address.IpRange;
import me.spencernold.janus.address.MacAddress;
import me.spencernold.janus.interrupt.Query;
import me.spencernold.janus.interrupt.query.EthernetQuery;
import me.spencernold.janus.interrupt.query.Ipv4Query;
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
    public void validIPv4TestCase1() {
        String input = """
                ipv4.address = 0.0.0.0
                """;
        List<Query> queries = runValidTestCase(input);
        Assertions.assertEquals(1, queries.size());
        Query q = queries.getFirst();
        Assertions.assertInstanceOf(Ipv4Query.class, q);
        Ipv4Query query = (Ipv4Query) q;
        IpRange ip = query.getAddress();
        Assertions.assertEquals(0, ip.max());
        Assertions.assertEquals(0, ip.min());
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
