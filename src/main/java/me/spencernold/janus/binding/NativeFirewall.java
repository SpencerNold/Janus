package me.spencernold.janus.binding;

import me.spencernold.janus.address.IpRange;
import me.spencernold.janus.fw.Action;
import me.spencernold.janus.fw.Firewall;
import me.spencernold.janus.fw.Protocol;
import me.spencernold.janus.fw.Rule;

import java.io.IOException;

public class NativeFirewall extends Binding implements AutoCloseable {

    static {
        ensureBindingLoaded();
    }

    private static NativeFirewall instance;

    private final long handle;

    private NativeFirewall(Firewall firewall) throws IOException {
        instance = this;
        Protocol protocol = firewall.protocol();
        int port = firewall.port();
        int action = firewall.action().ordinal();
        this.handle = fwStart(protocol.ordinal(), port, action);
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

    public static NativeFirewall create(Firewall firewall) throws IOException {
        if (instance == null)
            return new NativeFirewall(firewall);
        return instance;
    }

    private static native long fwStart(int protocol, int port, int action);
    private static native int fwRule(long handle, int action, long network, long broadcast);
    private static native int fwCommit(long handle);
    private static native void fwStop(long handle);
}
