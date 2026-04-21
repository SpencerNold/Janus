package me.spencernold.gradle.binding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Commandlet {

    public static List<String> execute(String... command) {
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            String output;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n")).trim();
            }
            int code = process.waitFor();
            if (code != 0)
                throw new IllegalStateException("pcap finder failed (exit " + code + "). Output:\n" + output);
            if (output.isBlank()) throw new IllegalStateException("pcap finder produced no output");
            return Arrays.stream(output.split("\\s+")).filter(s -> !s.isBlank()).toList();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
