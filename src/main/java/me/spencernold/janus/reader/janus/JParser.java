package me.spencernold.janus.reader.janus;

import me.spencernold.janus.address.Ip4Range;
import me.spencernold.janus.address.MacAddress;
import me.spencernold.janus.fw.Protocol;
import me.spencernold.janus.generic.Arrays2;
import me.spencernold.janus.interrupt.Query;
import me.spencernold.janus.interrupt.query.*;
import me.spencernold.janus.reader.AbstractParser;
import me.spencernold.janus.reader.Token;
import me.spencernold.janus.reader.exceptions.ReaderException;
import me.spencernold.janus.reader.exceptions.SyntaxException;
import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JParser extends AbstractParser<List<Query>> {

    public JParser(me.spencernold.janus.reader.janus.JLexer lexer) throws ReaderException {
        super(lexer);
    }

    public List<Query> parse() throws ReaderException {
        return parseProgram();
    }

    private List<Query> parseProgram() throws ReaderException {
        // Program -> QueryList EOF
        // FIRST(Program) = { ETH, IPV4, IPV6, TCP, UDP, EOF }
        JType[] firstOfProgram = new JType[]{JType.ETH, JType.IPV4, JType.IPV6, JType.TCP, JType.UDP, JType.EOF};
        if (nextIs(firstOfProgram)) {
            List<Query> queries = parseQueryList();
            consume(JType.class, JType.EOF);
            return queries;
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, firstOfProgram);
        }
    }

    private List<Query> parseQueryList() throws ReaderException {
        // QueryList -> Query QueryList | epsilon
        // FIRST(QueryList) = { ETH, IPV4, IPV6, TCP, UDP, epsilon }
        // FOLLOW(QueryList) = { EOF }
        List<Query> queries = new ArrayList<>();
        JType[] firstOfQuery = {JType.ETH, JType.IPV4, JType.IPV6, JType.TCP, JType.UDP};
        JType[] followOfQueryList = {JType.EOF};
        if (nextIs(firstOfQuery)) {
            Query query = parseQuery();
            queries.add(query);
            queries.addAll(parseQueryList());
            return queries;
        } else if (nextIs(followOfQueryList)) {
            // epsilon
            return queries;
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, Arrays2.concat(JType.class, firstOfQuery, followOfQueryList));
        }
    }

    private Query parseQuery() throws ReaderException {
        // Query -> EthernetQuery | Ipv4Query | Ipv6Query | TcpQuery | UdpQuery
        // FIRST(Query) = { ETH, IPV4, IPV6, TCP, UDP }
        if (nextIs(JType.ETH)) {
            return parseEthernetQuery();
        } else if (nextIs(JType.IPV4)) {
            return parseIpv4Query();
        } else if (nextIs(JType.IPV6)) {
            return parseIpv6Query();
        } else if (nextIs(JType.TCP)) {
            return parseTcpQuery();
        } else if (nextIs(JType.UDP)) {
            return parseUdpQuery();
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, JType.ETH, JType.IPV4, JType.IPV6, JType.TCP, JType.UDP);
        }
    }

    private Query parseEthernetQuery() throws ReaderException {
        // EthernetQuery -> ETH DOT ADDRESS EQ_OPER Mac
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
        // Ipv4Query -> IPV4 DOT ADDRESS EQ_OPER V4CIDR
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
        // Ipv6Query -> IPV6 DOT ADDRESS EQ_OPER V6CIDR
        return new Ipv6Query(); // TODO Implement
    }

    private Query parseTcpQuery() throws ReaderException {
        // TcpQuery -> TCP DOT TcpQueryTail
        consume(JType.class, JType.TCP);
        consume(JType.class, JType.DOT);
        return parseTransportQueryTail(Protocol.TCP);
    }

    private Query parseUdpQuery() throws ReaderException {
        // TcpQuery -> TCP DOT UdpQueryTail
        consume(JType.class, JType.UDP);
        consume(JType.class, JType.DOT);
        return parseTransportQueryTail(Protocol.UDP);
    }

    private Query parseTransportQueryTail(Protocol protocol) throws ReaderException {
        // TransportQueryTail -> ADDRESS AddrExpr | BODY BodyExpr
        // FIRST(TransportQueryTail) = { ADDRESS, BODY }
        if (nextIs(JType.ADDRESS)) {
            return parseAddrExpr(protocol);
        } else if (nextIs(JType.BODY)) {
            return parseBodyExpr(protocol);
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, JType.ADDRESS, JType.BODY);
        }
    }

    private Query parseAddrExpr(Protocol protocol) throws ReaderException {
        // AddrExpr -> EQ_OPER NUMBER
        consume(JType.class, JType.ADDRESS);
        consume(JType.class, JType.EQ_OPER);
        int port = parsePort();
        return new TransportAddressQuery(protocol, port);
    }

    private int parsePort() throws ReaderException {
        int port = parseNumber();
        if (port >= 0 && port <= 65535) return port;
        throw new UnexpectedTokenException(lexer, lookahead, "Port out of range '" + lookahead.value() + "'");
    }

    private BodyQuery parseBodyExpr(Protocol protocol) throws ReaderException {
        // BodyExpr -> BodyClause BodyExprTail
        consume(JType.class, JType.BODY);
        List<BodyQuery.Test> tests = new ArrayList<>();
        tests.add(parseBodyClause());
        tests.addAll(parseBodyExprTrail());
        return new BodyQuery(protocol, tests.toArray(new BodyQuery.Test[0]));
    }

    private List<BodyQuery.Test> parseBodyExprTrail() throws ReaderException {
        // FIRST(BodyExprTail) = { AND, epsilon }
        // If LogicOp -> AND | OR, then FIRST(LogicOp BodyClause BodyExprTail) = { AND, OR }
        // FOLLOW(BodyExprTail) = { ETH, IPV4, IPV6, TCP, UDP, EOF }
        JType[] followOfBodyExprTail = new JType[]{JType.ETH, JType.IPV4, JType.IPV6, JType.TCP, JType.UDP, JType.EOF};
        if (nextIs(JType.AND)) {
            consume(JType.class, JType.AND);
            List<BodyQuery.Test> tests = new ArrayList<>();
            tests.add(parseBodyClause());
            tests.addAll(parseBodyExprTrail());
            return tests;
        } else if (nextIs(followOfBodyExprTail)) {
            // epsilon
            return new ArrayList<>();
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, Arrays2.concat(JType.class, new JType[]{JType.AND}, followOfBodyExprTail));
        }
    }

    private BodyQuery.Test parseBodyClause() throws ReaderException {
        // Condition -> Not CONTAINS BodyTail
        boolean negated = parseNot();
        consume(JType.class, JType.CONTAINS);
        return parseBodyTail(negated);
    }

    private boolean parseNot() throws ReaderException {
        // Not -> NOT_OPER | epsilon
        // FIRST(Not) = { NOT_OPER, epsilon }
        // FOLLOW(Not) = { EQ_OPER, CONTAINS }
        if (nextIs(JType.NOT_OPER)) {
            consume(JType.class, JType.NOT_OPER);
            return true;
        } else if (nextIs(JType.EQ_OPER, JType.CONTAINS)) {
            // epsilon
            return false;
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, JType.NOT_OPER, JType.EQ_OPER, JType.CONTAINS);
        }
    }

    private BodyQuery.Test parseBodyTail(boolean negated) throws ReaderException {
        // BodyTail -> Offset O_BRACKET Target C_BRACKET
        // FIRST(BodyTail) = { O_CURLY, O_BRACKET }
        JType[] firstOfBodyTail = new JType[]{JType.O_CURLY, JType.O_BRACKET};
        if (nextIs(firstOfBodyTail)) {
            int offset = parseOffset();
            consume(JType.class, JType.O_BRACKET);
            List<Byte> target = parseTarget();
            consume(JType.class, JType.C_BRACKET);
            return new BodyQuery.Test(negated, offset, Arrays2.toByteArray(target));
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, firstOfBodyTail);
        }
    }

    private int parseOffset() throws ReaderException {
        // Offset -> O_CURLY NUMBER C_CURLY | epsilon
        // FIRST(Offset) = { O_CURLY, epsilon }
        // FOLLOW(Offset) = { O_BRACKET }
        if (nextIs(JType.O_CURLY)) {
            consume(JType.class, JType.O_CURLY);
            int number = parseNumber();
            consume(JType.class, JType.C_CURLY);
            return number;
        } else if (nextIs(JType.O_BRACKET)) {
            return 0; // Default no-offset value
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, JType.O_CURLY, JType.O_BRACKET);
        }
    }

    private List<Byte> parseTarget() throws ReaderException {
        // Target -> NUMBER TargetTail | epsilon
        // FIRST(Target) = { NUMBER, epsilon }
        // FOLLOW(Target) = { C_BRACKET }
        List<Byte> numbers = new ArrayList<>();
        if (nextIs(JType.NUMBER)) {
            Token token = lookahead.copy();
            int n = parseNumber();
            if (n > 255 | n < 0)
                throw new UnexpectedTokenException(lexer, token, "Unknown token '" + token.value() + "', numbers in the range of 0-255");
            numbers.add((byte) (n & 0xFF));
            numbers.addAll(parseTargetTail());
            return numbers;
        } else if (nextIs(JType.C_BRACKET)) {
            return numbers;
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, JType.NUMBER, JType.C_BRACKET);
        }
    }

    private List<Byte> parseTargetTail() throws ReaderException {
        // TargetTail -> COMMA NUMBER TargetTail | epsilon
        // FIRST(TargetTail) = { COMMA, epsilon }
        // FOLLOW(TargetTail) = { C_BRACKET }
        List<Byte> numbers = new ArrayList<>();
        if (nextIs(JType.COMMA)) {
            consume(JType.class, JType.COMMA);
            Token token = lookahead.copy();
            int n = parseNumber();
            if (n > 255 | n < 0)
                throw new UnexpectedTokenException(lexer, token, "Unknown token '" + token.value() + "', numbers in the range of 0-255");
            numbers.add((byte) (n & 0xFF));
            numbers.addAll(parseTargetTail());
            return numbers;
        } else if (nextIs(JType.C_BRACKET)) {
            // epsilon
            return numbers;
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, JType.COMMA, JType.C_BRACKET);
        }
    }

    private int parseNumber() throws ReaderException {
        Token token = lookahead.copy();
        String value = consume(JType.class, JType.NUMBER);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            throw new UnexpectedTokenException(lexer, token, "Unknown token '" + token.value() + "', expected valid number");
        }
    }
}
