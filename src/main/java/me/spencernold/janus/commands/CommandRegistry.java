package me.spencernold.janus.commands;

import me.spencernold.janus.generic.Arrays2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private static final Map<String, Executor> EXECUTORS = new HashMap<>();

    public static boolean register(Executor executor) {
        Class<? extends Executor> clazz = executor.getClass();
        if (!clazz.isAnnotationPresent(Command.class))
            return false;
        Command command = clazz.getAnnotation(Command.class);
        EXECUTORS.put(command.label(), executor);
        return true;
    }

    public static void invoke(String[] data) {
        if (data.length < 1)
            return; // Missing label, ignore
        Executor executor = EXECUTORS.get(data[0]);
        if (executor == null) {
            error("Unknown command.");
            return;
        }
        data = Arrays.copyOfRange(data, 1, data.length);
        Class<? extends Executor> clazz = executor.getClass();
        Command command = clazz.getAnnotation(Command.class);
        String[] label = command.args();
        if (label.length != (data.length - 1)) {
            String[] array = Arrays2.transform(data, s -> String.format("<%s>", s));
            error("Usage: " + data[0] + String.join(" ", array));
            return;
        }
        executor.execute(data);
    }

    public static void error(String message) {

    }
}
