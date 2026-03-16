package me.spencernold.janus.fw;

public enum Action {

    ALLOW, DENY, TARPIT;

    public static Action parseAction(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
