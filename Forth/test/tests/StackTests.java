package tests;


import static org.junit.Assert.*;
import toni.forth.ObjectStack;
import org.junit.Test;

public class StackTests {

	@Test
	public void test() {
		ObjectStack stack = new ObjectStack(10);
		
		assertEquals(0,stack.size());
		stack.push(2);
		assertEquals(1,stack.size());
		stack.push(3);
		assertEquals(2,stack.size());
		
		assertEquals(3,getIntValue(stack));
		assertEquals(2,getIntValue(stack));

	}

	protected int getIntValue(ObjectStack stack) {
		return ((Integer)stack.pop()).intValue();
	}
	
	@Test
	public void testUnderflow() {
		ObjectStack stack = new ObjectStack(10);
		
		
		stack.push(3);
			
		
		try {
			stack.pop();
			stack.pop();
			
			fail("Keine Exception aufgetreten");
		} catch (Exception ex) {
			
		}

	}
	@Test
	public void testOverflow() {
		ObjectStack stack = new ObjectStack(3);
		
		
		stack.push(1);
		stack.push(2);
		stack.push(3);
		
		try {
			stack.push(4);
					
			fail("Keine Exception aufgetreten");
		} catch (Exception ex) {
			
		}

	}

}
