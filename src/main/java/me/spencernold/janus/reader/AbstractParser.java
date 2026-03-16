package me.spencernold.janus.reader;

import me.spencernold.janus.reader.exceptions.ReaderException;
import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;

import java.io.IOException;
import java.util.Arrays;

public abstract class AbstractParser {

    protected final me.spencernold.janus.reader.Lexer lexer;
    protected Token lookahead;

    public AbstractParser(me.spencernold.janus.reader.Lexer lexer) throws ReaderException {
        this.lexer = lexer;
        this.lookahead = next();
    }

    private Token next() throws ReaderException {
        try {
            return lexer.yylex();
        } catch (IOException e) {
            throw new UnexpectedTokenException("Unexpected <<EOF>>");
        }
    }

    protected String consume(Type... expected) throws ReaderException {
        boolean contains = nextIs(expected);
        if (!contains)
            throw new UnexpectedTokenException("Unexpected Token: Found '%s', need '%s'", lookahead.type().name(), expected.length == 1 ? expected[0].name() : Arrays.toString(expected));
        String value = lookahead.value();
        lookahead = next();
        return value;
    }

    protected boolean nextIs(Type... types) {
        for (Type t : types) {
            if (lookahead.type() == t)
                return true;
        }
        return false;
    }
}
