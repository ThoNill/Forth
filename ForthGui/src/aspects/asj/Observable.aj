package aspects.asj;

import toni.forth.IntStack;
import aspects.interfaces.Updateable;

public aspect Observable {
//	declare parents : ((toni.forth.IntStack) || (toni.forth.Dictionary) || (toni.forth.ObjectStack)	) implements aspects.interfaces.Updateable;

    declare parents : IntStack implements Updateable;


	private boolean IntStack.dirty = false;
	
	public void IntStack.needUpdate() {
		System.out.println("need update");
		dirty = true;
	}
	
	public void IntStack.validate() {
		System.out.println("validate");
	//	dirty = false;
	}
	
	public boolean IntStack.isDirty() {
		return true; //dirty;
	}
/*	
	private boolean toni.forth.ObjectStack.dirty = false;
	
	public void toni.forth.ObjectStack.needUpdate() {
		System.out.println("need update");
		toni.forth.ObjectStack.dirty = true;
	}
	
	public void toni.forth.ObjectStack.validate() {
		System.out.println("validate");
		toni.forth.ObjectStack.dirty = false;
	}
	
	public boolean toni.forth.ObjectStack.isDirty() {
		return toni.forth.ObjectStack.dirty;
	}
	
	private boolean toni.forth.Dictionary.dirty = false;
	
	public void toni.forth.Dictionary.needUpdate() {
		System.out.println("need update");
		toni.forth.Dictionary.dirty = true;
	}
	
	public void toni.forth.Dictionary.validate() {
		System.out.println("validate");
		toni.forth.Dictionary.dirty = false;
	}
	
	public boolean toni.forth.Dictionary.isDirty() {
		return toni.forth.Dictionary.dirty;
	}	
	*/
	
	before(aspects.interfaces.Updateable obj) : (call(public void push(..)) || call(public void pop(..) )) && this(aspects.interfaces.Updateable) && this(obj)  {
		obj.needUpdate();
	}
	
}