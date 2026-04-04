package me.spencernold.janus.reader;

import me.spencernold.janus.reader.exceptions.ReaderException;
import me.spencernold.janus.reader.exceptions.UnexpectedTokenException;

import java.io.IOException;
import java.util.Arrays;

public abstract class AbstractParser {

    protected final AbstractLexer lexer;
    protected Token lookahead;

    public AbstractParser(AbstractLexer lexer) throws ReaderException {
        this.lexer = lexer;
        this.lookahead = next();
    }

    private Token next() throws ReaderException {
        try {
            return lexer.yylex();
        } catch (IOException e) {
            throw new UnexpectedTokenException("Unexpected <<EOF>>");
        } catch (Error e) {
            // TODO Make this more explicit in error reading
            throw new UnexpectedTokenException("Unknown token at line: n");
        }
    }

    @SafeVarargs
    protected final <T extends Enum<T>> String consume(Class<T> clazz, T... expected) throws ReaderException {
        final int length = expected.length;
        int[] array = new int[length];
        for (int i = 0; i < length; i++)
            array[i] = expected[i].ordinal();
        return consume(clazz, array);
    }

    protected <T extends Enum<T>> String consume(Class<T> clazz, int... expected) throws ReaderException {
        boolean contains = nextIs(expected);
        if (!contains) {
            T[] types = clazz.getEnumConstants();
            throw new UnexpectedTokenException("Unexpected Token: Found '%s', need '%s'", types[lookahead.type()], expected.length == 1 ? types[expected[0]].name() : Arrays.toString(expected));
        }
        String value = lookahead.value();
        lookahead = next();
        return value;
    }

    @SafeVarargs
    protected final <T extends Enum<T>> boolean nextIs(T... types) {
        final int length = types.length;
        int[] array = new int[length];
        for (int i = 0; i < length; i++)
            array[i] = types[i].ordinal();
        return nextIs(array);
    }

    protected boolean nextIs(int... types) {
        for (int t : types) {
            if (lookahead.type() == t)
                return true;
        }
        return false;
    }
}
