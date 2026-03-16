package me.spencernold.janus;

import me.spencernold.janus.binding.NativeFirewall;
import me.spencernold.janus.fw.Firewall;
import me.spencernold.janus.fw.Rule;
import me.spencernold.janus.reader.Reader;
import me.spencernold.janus.reader.exceptions.ReaderException;

import java.io.FileInputStream;
import java.io.IOException;

public class Sys implements AutoCloseable {

    private NativeFirewall nfw = null;

    public Sys() {
        try {
            Firewall firewall = Reader.read(new FileInputStream("syntax"));
            nfw = new NativeFirewall(firewall);
            for (Rule rule : firewall.rules())
                nfw.write(rule);
            nfw.commit();
        } catch (IOException e) {
            System.out.println("Internal Error: " + e.getMessage());
        } catch (ReaderException e) {
            System.out.println("Syntax Error: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (nfw != null)
            nfw.close();
    }
}
