package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

import toni.forth.Dictionary;
import toni.forth.Word;

public class DictionaryTest {

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInsert() {
		Dictionary dict = new Dictionary(100);
		Word w = new TestWord("Test");
		dict.insert(w);
		assertEquals(0, w.getPosition());
		w = new TestWord("Test2");
		dict.insert(w);
		assertEquals(1, w.getPosition());
		w = new TestWord("Test");
		dict.insert(w);
		assertEquals(2, w.getPosition());
	}

	@Test
	public void testCompile() {
		Dictionary dict = new Dictionary(100);
		Word w = new TestWord("Test");
		dict.insert(w);
		w = new TestWord("Test2");
		dict.insert(w);
		dict.compile(w);
		w = new TestWord("Test");
		dict.insert(w);
		dict.compile(w);

		w = dict.getWord(0);

		assertEquals("Test2", w.getName());

		w = dict.getWord(1);

		assertEquals("Test", w.getName());

	}

	@Test
	public void testSearch() {
		Dictionary dict = new Dictionary(100);
		Word w = new TestWord("Test1");
		dict.insert(w);
		w = new TestWord("Test2");
		dict.insert(w);
		w = new TestWord("Test3");
		dict.insert(w);

		w = dict.search("Test1");

		assertEquals("Test1", w.getName());

	}

}
