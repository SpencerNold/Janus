package integration;

import me.spencernold.janus.address.Ip4Range;
import me.spencernold.janus.fw.Action;
import me.spencernold.janus.fw.Firewall;
import me.spencernold.janus.fw.Protocol;
import me.spencernold.janus.fw.Rule;
import me.spencernold.janus.reader.Reader;
import me.spencernold.janus.reader.exceptions.ReaderException;
import me.spencernold.janus.reader.exceptions.SyntaxException;
import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

public class FirewallParseTest {

    @Test
    public void emptyTestCase() {
        String input = "";
        StringReader reader = new StringReader(input);
        Assertions.assertThrowsExactly(UnexpectedTokenException.class, () -> Reader.readFirewall(reader));
        reader.close();
    }

    @Test
    public void validMissingValuesTestCase() {
        String input = """
                .init
                .rules
                """;
        Firewall firewall = runValidTestCase(input);
        Assertions.assertEquals(80, firewall.port());
        Assertions.assertEquals(Protocol.TCP, firewall.protocol());
        Assertions.assertEquals(Action.DENY, firewall.action());
        Assertions.assertEquals(0, firewall.rules().size());
    }

    @Test
    public void validNoRulesTestCase() {
        String input = """
                .init
                    port: 25565
                    protocol: UDP
                    default: ALLOW
                .rules
                """;
        Firewall firewall = runValidTestCase(input);
        Assertions.assertEquals(25565, firewall.port());
        Assertions.assertEquals(Protocol.UDP, firewall.protocol());
        Assertions.assertEquals(Action.ALLOW, firewall.action());
        Assertions.assertEquals(0, firewall.rules().size());
    }

    @Test
    public void validRulesTestCase() {
        String input = """
                .init
                .rules
                    ruleName0, ALLOW, 10.0.0.109
                    ruleName1, TARPIT, 255.255.255.255
                    ruleName2, DENY, 0.0.0.0/0
                """;
        Firewall firewall = runValidTestCase(input);
        Assertions.assertEquals(80, firewall.port());
        Assertions.assertEquals(Protocol.TCP, firewall.protocol());
        Assertions.assertEquals(Action.DENY, firewall.action());
        List<Rule> rules = firewall.rules();
        Assertions.assertEquals(3, rules.size());
        Rule rule = rules.getFirst();
        Assertions.assertEquals("ruleName0", rule.name());
        Assertions.assertEquals(Action.ALLOW, rule.action());
        try {
            Assertions.assertEquals(Ip4Range.parseRange("10.0.0.109"), rule.target());
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
        rule = rules.get(1);
        Assertions.assertEquals("ruleName1", rule.name());
        Assertions.assertEquals(Action.TARPIT, rule.action());
        try {
            Assertions.assertEquals(Ip4Range.parseRange("255.255.255.255"), rule.target());
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
        rule = rules.get(2);
        Assertions.assertEquals("ruleName2", rule.name());
        Assertions.assertEquals(Action.DENY, rule.action());
        try {
            Assertions.assertEquals(Ip4Range.parseRange("0.0.0.0/0"), rule.target());
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void invalidTestCase() {
        String input = "hey you, you're finally awake";
        StringReader reader = new StringReader(input);
        Assertions.assertThrowsExactly(UnexpectedTokenException.class, () -> Reader.readFirewall(reader));
        reader.close();
    }

    private Firewall runValidTestCase(String input) {
        StringReader reader = new StringReader(input);
        try {
            Firewall firewall = Reader.readFirewall(reader);
            reader.close();
            return firewall;
        } catch (ReaderException e) {
            throw new RuntimeException(e);
        }
    }
}
