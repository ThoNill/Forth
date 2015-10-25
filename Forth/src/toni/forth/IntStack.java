package toni.forth;

public class IntStack	{
	private int[] values;
	private int top=-1; 

	public IntStack(int size) {
		values = new int[size];
		init();
	}
	
	public void init() {
		top = -1;
	}

	public int peek() {
		return values[top];
	}
	
	public int peek(int pos) {
		return values[pos];
	}
	
	public int pick(int pos) {
		return values[top- pos];
	}
	
	public void pick() {
		int a = values[top- values[top]];
		values[top] = a;
	}
	
	public void roll() {
		int anz = values[top];
		int a = values[top-1];
		int untergrenze = top - anz;
		int i = top-1;
		while(i > untergrenze){
			values[i] = values[i-1];
			i--;
		}
		values[untergrenze] = a;
		top--;
	}
	
	public int pop() {
		int n =  values[top];
		top--;
		return n;
	}
	
	public void push(int n) {
		top++;
		values[top] = n;
	}
	
	public boolean isEmpty() {
		return top < 0;
	}

	public int size() {
		return top+1;
	}

	public void clear() {
		top = -1;
	}
	
	public void log() {
		System.out.println("");
		for (int i = 0; i <= top; i++) {
			System.out.println("stack[" + i + "]=" + values[i] );
		}
	}
	
	public void swap() {
		int a = values[top];
		int b = values[top-1];
		values[top] = b;
		values[top-1] = a;
	}
	
	public void dup() {
		push(values[top]);
	}
	
	public void ifNot0dup() {
		int a = values[top];
		if ( a != 0) {
			push(values[top]);
		}
	}
	
	public void dup2() {
		int a = values[top];
		int b = values[top-1];
		push(b);
		push(a);
	}
	
	public void drop() {
		top--;
	}
	
	public void drop2() {
		top-=2;
	}
	
	public void rot() {
		int a = values[top];
		int b = values[top-1];
		int c = values[top-2];
		values[top] = b;
		values[top-1] = c;
		values[top-2] = a;
	}
	
	
	
	public void nrot() {
		int a = values[top];
		int b = values[top-1];
		int c = values[top-2];
		values[top] = c;
		values[top-1] = a;
		values[top-2] = b;
	}
	
	public void rot2() {
		int a1 = values[top];
		int a2 = values[top-1];
		int b1 = values[top-2];
		int b2 = values[top-3];
		int c1 = values[top-4];
		int c2 = values[top-5];
		
		values[top] = b1;
		values[top-1] = b2;
		values[top-2] = c1;
		values[top-3] = c2;
		values[top-4] = a1;
		values[top-5] = a2;
	}
	
	public void over() {
		push(values[top-1]);
	}
	
	public void over2() {
		int a = values[top-2];
		int b = values[top-3];
		push(b);
		push(a);
	}
	
	public void under() {
		int a = values[top];
		swap();
		push(a);
	}
	
	public void depth() {
		push(top+1);
	}

	public void min() {
		int a = values[top];
		int b = values[top-1];
		values[top-1] = Math.min(a, b);
		top--;
	}
	
	public void max() {
		int a = values[top];
		int b = values[top-1];
		values[top-1] = Math.max(a, b);
		top--;
	}

	
	public void add() {
		int a = values[top];
		int b = values[top-1];
		values[top-1] = a + b;
		top--;
	}
	
	public void mult() {
		int a = values[top];
		int b = values[top-1];
		values[top-1] = a * b;
		top--;
	}
	
	public void sub() {
		int a = values[top];
		int b = values[top-1];
		values[top-1] = b - a;
		top--;
	}
	
	public void div() {
		int a = values[top];
		int b = values[top-1];
		values[top-1] = b / a;
		top--;
	}
	
	public void divmod() {
		int a = values[top];
		int b = values[top-1];
		values[top] = b / a;
		values[top-1] = b % a;
	}
	
	public void multdiv() {
		long q = values[top];
		long a = values[top-1];
		long b = values[top-2];
		long prod = a * b;
		values[top-2] = (int)(prod / q);
		top--;
		top--;
	}
	
	public void multdivmod() {
		long q = values[top];
		long a = values[top-1];
		long b = values[top-2];
		long prod = a * b;
		values[top-1] = (int)(prod / q);
		values[top-2] = (int)(prod % q);
		top--;
	}
	
	public void plus1() {
		values[top]++;
	}
	
	public void minus1() {
		values[top]--;
	}
	
	public void plus2() {
		values[top]+=2;
	}
	
	public void minus2() {
		values[top]-=2;
	}
	
	public void abs() {
		values[top] = Math.abs(values[top]);
	}
	
	public void negate() {
		values[top] = - values[top];
	}	

	public void lt0() {
		values[top] = (values[top] < 0) ? 1 : 0;
	}
	
	public void gt0() {
		values[top] = (values[top] > 0) ? 1 : 0;
	}
	
	public void equals0() {
		values[top] = (values[top] == 0) ? 1 : 0;
	}
	
	public void lt() {
		values[top-1] = (values[top] > values[top-1]) ? 1 : 0;
		top--;
	}
	
	public void gt() {
		values[top-1] = (values[top] < values[top-1]) ? 1 : 0;
		top--;
	}
	
	public void equal() {
		values[top-1] = (values[top] == values[top-1]) ? 1 : 0;
		top--;
	}
	
	public void unequal() {
		values[top-1] = (values[top] != values[top-1]) ? 1 : 0;
		top--;
	}
	
	public void not() {
		values[top] = (values[top] == 0 ) ? 1 : 0;
	}
	
	public void or() {
		values[top-1] = (values[top] > 0 || values[top-1] > 0) ? 1 : 0;
		top--;
	}
	
	public void and() {
		values[top-1] = (values[top] > 0  && values[top-1] > 0) ? 1 : 0;
		top--;
	}

	
	
}
