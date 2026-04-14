package me.spencernold.janus;

import me.spencernold.janus.commands.CommandRegistry;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Register commands here
        Sys sys = new Sys();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("stop"))
                break;
            CommandRegistry.invoke(line.split(" "));
        }
        sys.close();
    }
}
