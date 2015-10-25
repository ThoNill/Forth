package toni.forth;

import java.util.Vector;

public class Dictionary {
	private Vector<Word> wordlist;
	private int words[] = null;
	private int topHeap;
	private Word lastInsertedWord = null;

	public Dictionary(int count) {
		super();
		this.wordlist = new Vector<>();
		words = new int[count];
		topHeap = 0;
	}

	public void insert(Word word) {
		word.setPosition(wordlist.size());
		word.setHeapPosition(topHeap);
		wordlist.add(word);
		lastInsertedWord = word;
	}

	public void compile(Word word) {
		compile(word.getPosition());
	}

	public void compile(int n) {
		words[topHeap] = n;
		topHeap++;
	}

	public Word insertAndCompile(Word word) {
		insert(word);
		compile(word);
		return word;
	}

	public Word search(String name) {
		int anz = wordlist.size();
		for (int i = anz - 1; i >= 0; i--) {
			Word w = wordlist.get(i);
			if (name.equals(w.getName())) {
				return w;
			}
		}
		return null;
	}

	
	public Word getWord(int i) {
		int wordNr = words[i];
		return wordlist.get(wordNr);
	}

	public void lastIsImediate() {
		lastInsertedWord.setImmediate(true);
	}

	public int getTopHeap() {
		return topHeap;
	}

	public Routine getLastInsertedWord() {
		return lastInsertedWord;
	}
	
	public void compileRecursiverAufruf() {
		compile(lastInsertedWord);
	}

	public int fetch(int pos) {
		return words[pos];
	}

	public void store(int pos, int value) {
		words[pos] = value;
	}

	public void log() {
		for (int i = 0; i < topHeap; i++) {
			System.out.println("d[" + i + "]=" + getDescription(i));
		}
	}

	public String getDescription(int i) {
		return "" + i + ": " + words[i] + " " + getWordName(i);
	}

	public String getWordName(int i) {
		int wi = words[i];
		return (wi < wordlist.size()) ? wordlist.get(wi).getName() : "";
	}

}
