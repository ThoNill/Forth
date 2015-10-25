package forth.gui;

import javafx.scene.control.TextArea;
import toni.forth.Output;

public class TextAreaOutput implements Output {
	TextArea ausgabe;
	
	public TextAreaOutput(TextArea ausgabe) {
		super();
		this.ausgabe = ausgabe;
	}

	@Override
	public void print(String text) {
		ausgabe.appendText(text);

	}

}
