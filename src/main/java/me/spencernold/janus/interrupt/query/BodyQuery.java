package me.spencernold.janus.interrupt.query;

import me.spencernold.janus.fw.Protocol;
import me.spencernold.janus.interrupt.InetFrame;
import me.spencernold.janus.interrupt.Query;

public class BodyQuery extends Query {

    private final Protocol protocol;
    private final Test[] tests;

    public BodyQuery(Protocol protocol, Test... tests) {
        this.protocol = protocol;
        this.tests = tests;
    }

    @Override
    public boolean test(InetFrame frame) {
        if (frame.getProtocol() != protocol)
            return false;
        for (Test test : tests) {
            if (!test.test(frame))
                return false;
        }
        return true;
    }

    public static class Test {

        private final int offset;
        private final boolean invert;
        private final byte[] target;

        public Test(boolean invert, int offset, byte[] target) {
            this.offset = offset;
            this.invert = invert;
            this.target = target;
        }

        public boolean test(InetFrame frame) {
            int frameOffset = frame.getTransportLayerStart() + offset;
            byte[] data = frame.getData();
            int length = frame.getLength();
            if (frameOffset + target.length > length)
                return false;
            boolean match = true;
            for (int i = 0; i < target.length; i++) {
                if (data[frameOffset + i] != target[i]) {
                    match = false;
                    break;
                }
            }
            return invert != match;
        }
    }
}
