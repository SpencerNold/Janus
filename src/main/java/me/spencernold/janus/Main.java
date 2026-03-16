package me.spencernold.janus;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Sys sys = new Sys();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("stop")) // TODO Make this shit more fancy at some point
                break;
        }
        sys.close();
    }
}
