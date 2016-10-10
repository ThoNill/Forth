package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import forth.gui.TokenStreamWithPosition;

public class TokenStreamWithPositionTest {
    private static final Logger LOG = LogManager
            .getLogger(TokenStreamWithPositionTest.class);

    @Test
    public void testTheStream() {
        TokenStreamWithPosition stream = new TokenStreamWithPosition(" 1 2 3 4");
        try {
            stream.nextToken();
            assertEquals(1, stream.getStartPosition());
            assertEquals(2, stream.getEndPosition());
        } catch (IOException e) {
            LOG.error("Unerwartete Ausnahme", e);
            fail("Exception aufgetreten");
        }
        try {
            stream.nextToken();
            assertEquals(3, stream.getStartPosition());
            assertEquals(4, stream.getEndPosition());
        } catch (IOException e) {
            LOG.error("Unerwartete Ausnahme", e);
            fail("Exception aufgetreten");
        }

    }
}
