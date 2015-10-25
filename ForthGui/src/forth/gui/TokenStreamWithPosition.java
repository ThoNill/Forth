package forth.gui;

import java.io.IOException;
import java.io.Reader;
import toni.forth.TokenStream;

public class TokenStreamWithPosition extends TokenStream {
	private int currentPosition;
	private int countSigns;
	private int startPosition;
	
	public TokenStreamWithPosition(String text) {
		super(text);
		startPosition = countSigns = currentPosition = 0;
	}
	
	
	@Override
	protected char nextChar(Reader reader) throws IOException {
		char c = super.nextChar(reader);
		currentPosition++;
		if (Character.isWhitespace(c) && countSigns == 0) {
			startPosition =  currentPosition;
		} else {
			countSigns++;
		}
		return c;
	}
	
	@Override
	public String nextToken() throws IOException {
		countSigns=0;
		startPosition =  currentPosition;
		return  super.nextToken();
	}


	public int getStartPosition() {
		return startPosition;
	}


	public int getEndPosition() {
		return startPosition + countSigns - 1;
	}

}
