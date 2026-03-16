package me.spencernold.janus.reader;

import me.spencernold.janus.fw.*;
import me.spencernold.janus.generic.Arrays2;
import me.spencernold.janus.reader.exceptions.ReaderException;
import me.spencernold.janus.reader.exceptions.SyntaxException;

import java.util.ArrayList;
import java.util.List;

public class Parser extends AbstractParser {

    public Parser(me.spencernold.janus.reader.Lexer lexer) throws ReaderException {
        super(lexer);
    }

    public Firewall parse() throws ReaderException {
        Firewall.Builder builder = new Firewall.Builder();
        parseInit(builder);
        parseRules(builder);
        return builder.build();
    }

    private void parseInit(Firewall.Builder builder) throws ReaderException {
        consume(Type.SEC_INIT);
        while (nextIs(Type.PORT, Type.DEFAULT, Type.PROTOCOL)) {
            if (lookahead.type() == Type.PORT) {
                consume(Type.PORT);
                int port = parsePort();
                builder.setPort(port);
            } else if (lookahead.type() == Type.DEFAULT) {
                consume(Type.DEFAULT);
                Action action = parseEnum(Action.class, Type.ALLOW, Type.DENY, Type.TARPIT);
                builder.setAction(action);
            } else if (lookahead.type() == Type.PROTOCOL) {
                consume(Type.PROTOCOL);
                Protocol protocol = parseEnum(Protocol.class, Type.TCP, Type.UDP);
                builder.setProtocol(protocol);
            }
        }
    }

    private void parseRules(Firewall.Builder builder) throws ReaderException {
        consume(Type.SEC_RULES);
        List<Rule> rules = new ArrayList<>();
        while (lookahead.type() != Type.EOF) {
            String name = consume(Type.IDENTIFIER);
            consume(Type.COMMA);
            Action action = parseEnum(Action.class, Type.ALLOW, Type.DENY, Type.TARPIT);
            consume(Type.COMMA);
            IpRange addresses = parseCidr();
            rules.add(new Rule(name, action, addresses));
        }
        builder.setRules(rules);
    }

    private IpRange parseCidr() throws ReaderException {
        String value = consume(Type.CIDR);
        return IpRange.parseRange(value);
    }

    private <T extends Enum<T>> T parseEnum(Class<T> clazz, Type... types) throws ReaderException {
        String value = consume(types);
        try {
            return Enum.valueOf(clazz, value.toUpperCase());
        } catch (Exception e) {
            String s = String.join(", ", Arrays2.transformToString(clazz.getEnumConstants()));
            throw new SyntaxException("expected '%s', instead found: " + value, s);
        }
    }

    private int parsePort() throws ReaderException {
        try {
            String number = consume(Type.NUMBER);
            int port = Integer.parseInt(number);
            if (port < 0 || port > 65535)
                throw new SyntaxException("port is out of range (0-65535)");
            return port;
        } catch (NumberFormatException e) {
            throw new SyntaxException("port is out of range (0-65535)");
        }
    }
}
