package me.spencernold.janus.reader.def;

import me.spencernold.janus.address.Ip4Range;
import me.spencernold.janus.fw.Action;
import me.spencernold.janus.fw.Firewall;
import me.spencernold.janus.fw.Protocol;
import me.spencernold.janus.fw.Rule;
import me.spencernold.janus.generic.Arrays2;
import me.spencernold.janus.reader.AbstractParser;
import me.spencernold.janus.reader.exceptions.ReaderException;
import me.spencernold.janus.reader.exceptions.SyntaxException;

import java.util.ArrayList;
import java.util.List;

public class DefParser extends AbstractParser {

    public DefParser(me.spencernold.janus.reader.def.DefLexer lexer) throws ReaderException {
        super(lexer);
    }

    public Firewall parse() throws ReaderException {
        Firewall.Builder builder = new Firewall.Builder();
        parseInit(builder);
        parseRules(builder);
        return builder.build();
    }

    private void parseInit(Firewall.Builder builder) throws ReaderException {
        consume(DefType.class, DefType.SEC_INIT);
        while (nextIs(DefType.PORT, DefType.DEFAULT, DefType.PROTOCOL)) {
            if (lookahead.is(DefType.PORT)) {
                consume(DefType.class, DefType.PORT);
                int port = parsePort();
                builder.setPort(port);
            } else if (lookahead.is(DefType.DEFAULT)) {
                consume(DefType.class, DefType.DEFAULT);
                Action action = parseEnum(Action.class, DefType.ALLOW, DefType.DENY, DefType.TARPIT);
                builder.setAction(action);
            } else if (lookahead.is(DefType.PROTOCOL)) {
                consume(DefType.class, DefType.PROTOCOL);
                Protocol protocol = parseEnum(Protocol.class, DefType.TCP, DefType.UDP);
                builder.setProtocol(protocol);
            }
        }
    }

    private void parseRules(Firewall.Builder builder) throws ReaderException {
        consume(DefType.class, DefType.SEC_RULES);
        List<Rule> rules = new ArrayList<>();
        while (!lookahead.is(DefType.EOF)) {
            String name = consume(DefType.class, DefType.IDENTIFIER);
            consume(DefType.class, DefType.COMMA);
            Action action = parseEnum(Action.class, DefType.ALLOW, DefType.DENY, DefType.TARPIT);
            consume(DefType.class, DefType.COMMA);
            Ip4Range addresses = parseCidr();
            rules.add(new Rule(name, action, addresses));
        }
        builder.setRules(rules);
    }

    private Ip4Range parseCidr() throws ReaderException {
        String value = consume(DefType.class, DefType.CIDR);
        return Ip4Range.parseRange(value);
    }

    private <T extends Enum<T>> T parseEnum(Class<T> clazz, DefType... types) throws ReaderException {
        String value = consume(DefType.class, types);
        try {
            return Enum.valueOf(clazz, value.toUpperCase());
        } catch (Exception e) {
            String s = String.join(", ", Arrays2.transformToString(clazz.getEnumConstants()));
            throw new SyntaxException("expected '%s', instead found: " + value, s);
        }
    }

    private int parsePort() throws ReaderException {
        try {
            String number = consume(DefType.class, DefType.NUMBER);
            int port = Integer.parseInt(number);
            if (port < 0 || port > 65535)
                throw new SyntaxException("port is out of range (0-65535)");
            return port;
        } catch (NumberFormatException e) {
            throw new SyntaxException("port is out of range (0-65535)");
        }
    }
}
