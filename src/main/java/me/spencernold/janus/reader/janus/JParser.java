package me.spencernold.janus.reader.janus;

import me.spencernold.janus.address.IpRange;
import me.spencernold.janus.address.MacAddress;
import me.spencernold.janus.interrupt.Query;
import me.spencernold.janus.interrupt.query.*;
import me.spencernold.janus.reader.AbstractParser;
import me.spencernold.janus.reader.exceptions.ReaderException;

import java.util.ArrayList;
import java.util.List;

public class JParser extends AbstractParser {

    public JParser(me.spencernold.janus.reader.janus.JLexer lexer) throws ReaderException {
        super(lexer);
    }

    public List<Query> parse() throws ReaderException {
        List<Query> queries = new ArrayList<>();
        while (!lookahead.is(JType.EOF) && nextIs(JType.ETH, JType.IPV4, JType.IPV6, JType.TCP, JType.UDP)) {
            if (lookahead.is(JType.ETH)) {
                queries.add(parseEthernetQuery());
            } else if (lookahead.is(JType.IPV4)) {
                queries.add(parseIpv4Query());
            } else if (lookahead.is(JType.IPV6)) {
                queries.add(parseIpv6Query());
            } else if (lookahead.is(JType.TCP)) {
                queries.add(parseTcpQuery());
            } else if (lookahead.is(JType.UDP)) {
                queries.add(parseUdpQuery());
            }
        }
        return queries;
    }

    private Query parseEthernetQuery() throws ReaderException {
        // Likely will become more complex as the language grows
        consume(JType.class, JType.ETH);
        consume(JType.class, JType.DOT);
        consume(JType.class, JType.ADDRESS);
        consume(JType.class, JType.EQ_OPER);
        MacAddress macAddress = parseMac();
        return new EthernetQuery(macAddress);
    }

    private MacAddress parseMac() throws ReaderException {
        String address = consume(JType.class, JType.MAC);
        return MacAddress.parseMac(address);
    }

    private Query parseIpv4Query() throws ReaderException {
        consume(JType.class, JType.IPV4);
        consume(JType.class, JType.DOT);
        consume(JType.class, JType.ADDRESS);
        consume(JType.class, JType.EQ_OPER);
        IpRange ipv4 = parseV4Cidr();
        return new Ipv4Query(ipv4);
    }

    private IpRange parseV4Cidr() throws ReaderException {
        String value = consume(JType.class, JType.V4CIDR);
        return IpRange.parseRange(value);
    }

    private Query parseIpv6Query() throws ReaderException {
        return new Ipv6Query();
    }

    private Query parseTcpQuery() throws ReaderException {
        return new TcpQuery(0); // For now
    }

    private Query parseUdpQuery() throws ReaderException {
        return new UdpQuery(0); // For now
    }
}
