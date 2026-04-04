package me.spencernold.janus.binding;

import java.io.IOException;

public class PacketCapture extends Binding implements AutoCloseable {

    static {
        ensureBindingLoaded();
    }

    private static PacketCapture instance;

    private final long handle;
    private boolean listening;

    private PacketCapture() throws IOException {
        instance = this;
        handle = pcOpen();
        if (handle == 0L)
            throw new IOException("internal memory allocation and/or file read error");
    }

    public boolean send(byte[] bytes, int offset, int length) {
        return pcSend(handle, bytes, offset, length);
    }

    public boolean send(byte[] bytes) {
        return pcSend(handle, bytes, 0, bytes.length);
    }

    public void listen(Listener listener) {
        if (listening)
            return;
        listening = true;
        pcListen(handle, listener);
    }

    public void ignore() {
        if (!listening)
            return;
        listening = false;
        pcIgnore(handle);
    }

    public String getInterfaceName() {
        return pcGetInterfaceName(handle);
    }

    @Override
    public void close() {
        if (listening)
            pcIgnore(handle);
        pcClose(handle);
    }

    public static PacketCapture create() throws IOException {
        if (instance == null)
            return new PacketCapture();
        return instance;
    }

    public static String getInterfaceDeviceName() {
        if (instance == null)
            return null;
        return instance.getInterfaceName();
    }

    private static native long pcOpen();
    private static native boolean pcSend(long handle, byte[] data, int offset, int length);
    private static native String pcGetInterfaceName(long handle);
    private static native void pcListen(long handle, Listener listener);
    private static native void pcIgnore(long handle);
    private static native void pcClose(long handle);

    @FunctionalInterface
    public interface Listener {
        void listen(long handle, byte[] bytes, int length);
    }
}
