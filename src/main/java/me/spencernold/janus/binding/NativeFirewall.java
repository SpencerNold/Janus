package me.spencernold.janus.binding;

import me.spencernold.janus.fw.Action;
import me.spencernold.janus.fw.Firewall;
import me.spencernold.janus.fw.IpRange;
import me.spencernold.janus.fw.Rule;

import java.io.IOException;

public class NativeFirewall extends Binding implements AutoCloseable {

    static {
        ensureBindingLoaded();
    }

    private final long handle;

    public NativeFirewall(Firewall firewall) throws IOException {
        int protocol = firewall.protocol().ordinal();
        int port = firewall.port();
        int action = firewall.action().ordinal();
        this.handle = fwStart(protocol, port, action);
        if (handle == 0L)
            throw new IOException("internal memory allocation and/or file read error");
    }

    public void write(Rule rule) throws IOException {
        Action action = rule.action();
        IpRange target = rule.target();
        int result = fwRule(handle, action.ordinal(), target.min(), target.max());
        if (result != 0) {
            fwStop(handle);
            throw new IOException("internal error, shutting down firewall");
        }
    }

    public void commit() throws IOException {
        int result = fwCommit(handle);
        if (result != 0) {
            fwStop(handle);
            throw new IOException("internal error, shutting down firewall");
        }
    }

    @Override
    public void close() {
        fwStop(handle);
    }

    private static native long fwStart(int protocol, int port, int action);
    private static native int fwRule(long handle, int action, int network, int broadcast);
    private static native int fwCommit(long handle);
    private static native void fwStop(long handle);
}
