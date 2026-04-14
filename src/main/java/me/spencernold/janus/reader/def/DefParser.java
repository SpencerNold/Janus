package me.spencernold.janus.reader.def;

import me.spencernold.janus.address.Ip4Range;
import me.spencernold.janus.fw.Action;
import me.spencernold.janus.fw.Firewall;
import me.spencernold.janus.fw.Protocol;
import me.spencernold.janus.fw.Rule;
import me.spencernold.janus.generic.Arrays2;
import me.spencernold.janus.reader.AbstractParser;
import me.spencernold.janus.reader.Token;
import me.spencernold.janus.reader.exceptions.ReaderException;
import me.spencernold.janus.reader.exceptions.SyntaxException;
import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;

import java.util.ArrayList;
import java.util.List;

public class DefParser extends AbstractParser<Firewall> {

    public DefParser(me.spencernold.janus.reader.def.DefLexer lexer) throws ReaderException {
        super(lexer);
    }

    public Firewall parse() throws ReaderException {
        return parseProgram();
    }

    private Firewall parseProgram() throws ReaderException {
        // Program -> InitSection RulesSection EOF
        // FIRST(Program) = { SEC_INIT }
        if (nextIs(DefType.SEC_INIT)) {
            Firewall.Builder builder = new Firewall.Builder();
            parseInitSection(builder);
            parseRulesSection(builder);
            consume(DefType.class, DefType.EOF);
            return builder.build();
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, DefType.SEC_INIT);
        }
    }

    private void parseInitSection(Firewall.Builder builder) throws ReaderException {
        // InitSection -> SEC_INIT InitStatementList
        consume(DefType.class, DefType.SEC_INIT);
        parseInitStatementList(builder);
    }

    private void parseInitStatementList(Firewall.Builder builder) throws ReaderException {
        // InitStatementList -> InitStatement InitStatementList | epsilon
        // FIRST(InitStatementList) = { PORT, DEFAULT, PROTOCOL, epsilon }
        // FOLLOW(InitStatementList) = { SEC_RULES }
        DefType[] firstOfInitStmt = new DefType[]{DefType.PORT, DefType.DEFAULT, DefType.PROTOCOL};
        DefType[] followOfInitStmtList = new DefType[]{DefType.SEC_RULES};
        if (nextIs(firstOfInitStmt)) {
            parseInitStatement(builder);
            parseInitStatementList(builder);
        } else if (nextIs(followOfInitStmtList)) {
            // epsilon
        } else {
            throw new UnexpectedTokenException(
                    lexer,
                    lookahead,
                    Arrays2.concat(DefType.class, firstOfInitStmt, followOfInitStmtList)
            );
        }
    }

    private void parseInitStatement(Firewall.Builder builder) throws ReaderException {
        // InitStatement -> PORT PortDecl | DEFAULT ActionDecl | PROTOCOL ProtocolDecl
        // FIRST(InitStatement) = { PORT, DEFAULT, PROTOCOL }
        if (nextIs(DefType.PORT)) {
            consume(DefType.class, DefType.PORT);
            int port = parsePortDecl();
            builder.setPort(port);
        } else if (nextIs(DefType.DEFAULT)) {
            consume(DefType.class, DefType.DEFAULT);
            Action action = parseActionDecl();
            builder.setAction(action);
        } else if (nextIs(DefType.PROTOCOL)) {
            consume(DefType.class, DefType.PROTOCOL);
            Protocol protocol = parseProtocolDecl();
            builder.setProtocol(protocol);
        } else {
            throw new UnexpectedTokenException(
                    lexer,
                    lookahead,
                    DefType.PORT,
                    DefType.DEFAULT,
                    DefType.PROTOCOL
            );
        }
    }

    private int parsePortDecl() throws ReaderException {
        // PortDecl -> NUMBER
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

    private Action parseActionDecl() throws ReaderException {
        // ActionDecl -> ALLOW | DENY | TARPIT
        Token token = lookahead.copy();
        String value = consume(DefType.class, DefType.ALLOW, DefType.DENY, DefType.TARPIT);
        try {
            return Action.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            String s = String.join(", ", Arrays2.transformToString(Action.class.getEnumConstants()));
            throw new UnexpectedTokenException(lexer, token, "Unknown token '" + token.value() + "', expected one of: " + s);
        }
    }

    private Protocol parseProtocolDecl() throws ReaderException {
        // ProtocolDecl -> TCP | UDP
        Token token = lookahead.copy();
        String value = consume(DefType.class, DefType.TCP, DefType.UDP);
        try {
            return Protocol.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            String s = String.join(", ", Arrays2.transformToString(Protocol.class.getEnumConstants()));
            throw new UnexpectedTokenException(lexer, token, "Unknown token '" + token.value() + "', expected one of: " + s);
        }
    }

    private void parseRulesSection(Firewall.Builder builder) throws ReaderException {
        // RulesSection -> SEC_RULES RuleList
        consume(DefType.class, DefType.SEC_RULES);
        List<Rule> rules = parseRuleList();
        builder.setRules(rules);
    }

    private List<Rule> parseRuleList() throws ReaderException {
        // RuleList -> Rule RuleList | epsilon
        // FIRST(RuleList) = { IDENTIFIER, epsilon }
        // FOLLOW(RuleList) = { EOF }
        List<Rule> rules = new ArrayList<>();
        if (nextIs(DefType.IDENTIFIER)) {
            Rule rule = parseRule();
            rules.add(rule);
            rules.addAll(parseRuleList());
            return rules;
        } else if (nextIs(DefType.EOF)) {
            // epsilon
            return rules;
        } else {
            throw new UnexpectedTokenException(lexer, lookahead, DefType.IDENTIFIER, DefType.EOF);
        }
    }

    private Rule parseRule() throws ReaderException {
        // Rule -> IDENTIFIER COMMA ActionDecl COMMA CIDR
        String name = consume(DefType.class, DefType.IDENTIFIER);
        consume(DefType.class, DefType.COMMA);
        Action action = parseActionDecl();
        consume(DefType.class, DefType.COMMA);
        Ip4Range addresses = parseCidr();
        return new Rule(name, action, addresses);
    }

    private Ip4Range parseCidr() throws ReaderException {
        Token token = lookahead.copy();
        String value = consume(DefType.class, DefType.CIDR);
        try {
            return Ip4Range.parseRange(value);
        } catch (SyntaxException e) {
            throw new UnexpectedTokenException(lexer, token, "Unknown token '" + token.value() + "', expected valid CIDR format");
        }
    }
}
