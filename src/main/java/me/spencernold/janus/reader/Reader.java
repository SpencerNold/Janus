package me.spencernold.janus.reader;

import me.spencernold.janus.fw.Firewall;
import me.spencernold.janus.reader.Lexer;
import me.spencernold.janus.reader.exceptions.ReaderException;

import java.io.InputStream;
import java.io.InputStreamReader;

public class Reader {

    public static Firewall read(InputStream input) throws ReaderException {
        Lexer lexer = new Lexer(new InputStreamReader(input));
        Parser parser = new Parser(lexer);
        return parser.parse();
    }
}
