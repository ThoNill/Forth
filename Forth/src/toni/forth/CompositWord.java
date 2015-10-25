package toni.forth;

public class CompositWord extends Word {
	public CompositWord(String name) {
		super(name);
		
	}
	
	@Override
	public void say(ForthContext context) {
		context.pushHeapPosition(this);
	}

}
