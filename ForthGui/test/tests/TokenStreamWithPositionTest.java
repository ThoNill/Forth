package tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import forth.gui.TokenStreamWithPosition;

public class TokenStreamWithPositionTest {

	@Test
	public void testTheStream() {
		TokenStreamWithPosition stream = new TokenStreamWithPosition(" 1 2 3 4");
		try {
			stream.nextToken();
			assertEquals(1,stream.getStartPosition());
			assertEquals(2,stream.getEndPosition());
		} catch (IOException e) {
			fail("Exception aufgetreten");
		}
		try {
			stream.nextToken();
			assertEquals(3,stream.getStartPosition());
			assertEquals(4,stream.getEndPosition());
		} catch (IOException e) {
			fail("Exception aufgetreten");
		}		
		
	}
}
