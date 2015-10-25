package forth.gui;

public class SimpleIntegerConverter implements IntegerConverter {
	
	/* (non-Javadoc)
	 * @see forth.gui.IntegerConverter#convert(int)
	 */
	@Override
	public String convert(int n) {
		return Integer.toString(n);
	}
}
