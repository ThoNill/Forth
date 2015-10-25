package tests;

import static org.junit.Assert.*;


import java.io.IOException;

import org.junit.After;
import org.junit.Test;
import toni.forth.TokenStream;

public class TokenStreamTest  {

	@After
	public void tearDown() throws Exception {
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}

}
