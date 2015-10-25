package toni.forth;

public class EmptyWord extends Word {

	public EmptyWord() {
		super("empty");
		setImmediate(true);
	}
	@Override
	public void say(ForthContext context) {
	}

}
