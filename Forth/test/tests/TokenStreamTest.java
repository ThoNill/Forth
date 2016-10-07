package tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import toni.forth.TokenStream;

public class TokenStreamTest {
    private static final String UNGEWOLLTE_AUSNAHME = "ungewollte Ausnahme";
    private static final Logger LOG = LogManager
            .getLogger(TokenStreamTest.class);

    @After
    public void tearDown() {
    }

    @Test
    public void test() {

        TokenStream stream = new TokenStream(" eins zwei drei");
        String t;
        try {
            t = stream.nextToken();
            assertEquals("eins", t);
            t = stream.nextToken();
            assertEquals("zwei", t);
            t = stream.nextToken();
            assertEquals("drei", t);
        } catch (IOException e) {
            LOG.error(UNGEWOLLTE_AUSNAHME, e);
        }
    }

    @Test
    public void utilChar() {

        TokenStream stream = new TokenStream(" eins zwei drei");
        String t;
        try {
            t = stream.allTextUntil('s');
            assertEquals(" ein", t);
            t = stream.nextToken();
            assertEquals("zwei", t);
            t = stream.nextToken();
            assertEquals("drei", t);
        } catch (IOException e) {
            LOG.error(UNGEWOLLTE_AUSNAHME, e);
        }
    }

    @Test
    public void utilChar2() {

        TokenStream stream = new TokenStream(" eins zwei drei");
        String t;
        try {
            t = stream.allTextUntil('"');
            assertEquals(" eins zwei drei", t);

        } catch (IOException e) {
            LOG.error(UNGEWOLLTE_AUSNAHME, e);
        }
    }

}
