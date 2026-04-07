package me.spencernold.janus.fw;

import java.util.ArrayList;
import java.util.List;

public record Firewall(Protocol protocol, int port, Action action, List<Rule> rules) {

    public static class Builder {

        // Default values no matter what
        private Protocol protocol = Protocol.TCP;
        private int port = 80;
        private Action action = Action.DENY;
        private List<Rule> rules = new ArrayList<>();

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setAction(Action action) {
            this.action = action;
            return this;
        }

        public Builder setRules(List<Rule> rules) {
            this.rules = rules;
            return this;
        }

        public Builder setProtocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Firewall build() {
            return new Firewall(protocol, port, action, rules);
        }
    }
}
