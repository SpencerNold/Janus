package me.spencernold.janus;

import java.io.PrintStream;

public class Printer {

    // At some point, I'm going to use this to make errors PRETTY!

    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String RESET = "\u001B[0m";

    private static final PrintStream defaultPrintStream = System.out;
    private static PrintStream stream = defaultPrintStream;

    public static void printBanner() {
        stream.println(GREEN + """
         ██╗ █████╗ ███╗   ██╗██╗   ██╗███████╗
         ██║██╔══██╗████╗  ██║██║   ██║██╔════╝
         ██║███████║██╔██╗ ██║██║   ██║███████╗
    ██   ██║██╔══██║██║╚██╗██║██║   ██║╚════██║
    ╚█████╔╝██║  ██║██║ ╚████║╚██████╔╝███████║
     ╚════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝
        """ + RESET);
    }

    public static void setStream(PrintStream stream) {
        Printer.stream = stream;
    }

    public static void resetStream() {
        Printer.stream = defaultPrintStream;
    }

    public static void colorln(String color, String message) {
        color(color, message + "\n");
    }

    public static void color(String color, String message) {
        colorf(color, message);
    }

    public static void colorf(String color, String format, Object... args) {
        String message = String.format(format, args);
        stream.print(color + message + RESET);
    }
}
