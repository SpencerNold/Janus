package me.spencernold.janus.reader;

import me.spencernold.janus.fw.Firewall;
import me.spencernold.janus.interrupt.Query;
import me.spencernold.janus.reader.def.DefLexer;
import me.spencernold.janus.reader.def.DefParser;
import me.spencernold.janus.reader.exceptions.ReaderException;
import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;
import me.spencernold.janus.reader.janus.JLexer;
import me.spencernold.janus.reader.janus.JParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Reader {

    public static Firewall readFirewall(InputStream input) throws ReaderException {
        return readFirewall(new InputStreamReader(input));
    }

    public static Firewall readFirewall(java.io.Reader reader) throws ReaderException {
        DefLexer lexer = new DefLexer(reader);
        DefParser parser = new DefParser(lexer);
        return parser.parse();
    }

    public static List<Query> readInterruptions(InputStream input) throws ReaderException {
        return readInterruptions(new InputStreamReader(input));
    }

    public static List<Query> readInterruptions(java.io.Reader reader) throws ReaderException {
        JLexer lexer = new JLexer(reader);
        JParser parser = new JParser(lexer);
        return parser.parse();
    }
}
