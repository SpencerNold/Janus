package me.spencernold.janus.binding;

public abstract class Binding {

    private static boolean loaded;

    protected static void ensureBindingLoaded() {
        if (loaded)
            return;
        loaded = true;
        System.loadLibrary("binding");
    }
}
