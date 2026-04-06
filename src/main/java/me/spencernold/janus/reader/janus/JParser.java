package me.spencernold.janus.reader.janus;

import me.spencernold.janus.address.Ip4Range;
import me.spencernold.janus.address.MacAddress;
import me.spencernold.janus.interrupt.Query;
import me.spencernold.janus.interrupt.query.*;
import me.spencernold.janus.reader.AbstractParser;
import me.spencernold.janus.reader.Token;
import me.spencernold.janus.reader.exceptions.ReaderException;
import me.spencernold.janus.reader.exceptions.SyntaxException;
import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;

import java.util.ArrayList;
import java.util.List;

public class JParser extends AbstractParser {

    public JParser(me.spencernold.janus.reader.janus.JLexer lexer) throws ReaderException {
        super(lexer);
    }

    public List<Query> parse() throws ReaderException {
        List<Query> queries = new ArrayList<>();
        while (nextIs(JType.ETH, JType.IPV4, JType.IPV6, JType.TCP, JType.UDP)) {
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
        if (!nextIs(JType.EOF))
            throw new UnexpectedTokenException(lexer, lookahead, JType.ETH, JType.IPV4, JType.IPV6, JType.TCP, JType.UDP);
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
        Token token = lookahead.copy();
        String address = consume(JType.class, JType.MAC);
        try {
            return MacAddress.parseMac(address);
        } catch (SyntaxException e) {
            throw new UnexpectedTokenException(lexer, token, "Unknown token '" + token.value() + "', expected valid MAC format");
        }
    }

    private Query parseIpv4Query() throws ReaderException {
        consume(JType.class, JType.IPV4);
        consume(JType.class, JType.DOT);
        consume(JType.class, JType.ADDRESS);
        consume(JType.class, JType.EQ_OPER);
        Ip4Range ipv4 = parseV4Cidr();
        return new Ipv4Query(ipv4);
    }

    private Ip4Range parseV4Cidr() throws ReaderException {
        Token token = lookahead.copy();
        String value = consume(JType.class, JType.V4CIDR);
        try {
            return Ip4Range.parseRange(value);
        } catch (SyntaxException e) {
            throw new UnexpectedTokenException(lexer, token, "Unknown token '" + token.value() + "', expected valid CIDR format");
        }
    }

    private Query parseIpv6Query() throws ReaderException {
        return new Ipv6Query(); // TODO Implement
    }

    private Query parseTcpQuery() throws ReaderException {
        consume(JType.class, JType.TCP);
        consume(JType.class, JType.DOT);
        consume(JType.class, JType.ADDRESS);
        consume(JType.class, JType.EQ_OPER);
        int port = parsePort();
        return new TcpQuery(port);
    }

    private Query parseUdpQuery() throws ReaderException {
        consume(JType.class, JType.UDP);
        consume(JType.class, JType.DOT);
        consume(JType.class, JType.ADDRESS);
        consume(JType.class, JType.EQ_OPER);
        int port = parsePort();
        return new UdpQuery(port); // For now
    }

    private int parsePort() throws ReaderException {
        Token token = lookahead.copy();
        String value = consume(JType.class, JType.PORT);
        try {
            int port = Integer.parseInt(value);
            if (port >= 0 && port <= 65535)
                return port;
        } catch (NumberFormatException ignored) { // Will go to error state
        }
        throw new UnexpectedTokenException(lexer, token, "Unknown token '" + token.value() + "', expected valid port number");
    }
}
