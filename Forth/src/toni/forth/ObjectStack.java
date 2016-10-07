package toni.forth;

public class ObjectStack {
    private Object[] values;
    private int top = -1;

    public ObjectStack(int size) {
        values = new Object[size];
    }

    public Object peek() {
        return values[top];
    }

    public Object pop() {
        Object obj = values[top];
        top--;
        return obj;
    }

    public void push(Object obj) {
        top++;
        values[top] = obj;
    }

    public Object size() {
        return top + 1;
    }

    public void clear() {
        top = -1;
    }

    public void swap() {
        Object a = values[top];
        Object b = values[top - 1];
        values[top] = b;
        values[top - 1] = a;
    }

    public void dup() {
        push(values[top]);
    }

    public void drop() {
        top--;
    }

    public void rot() {
        Object a = values[top];
        Object b = values[top - 1];
        Object c = values[top - 2];
        values[top] = b;
        values[top - 1] = c;
        values[top - 2] = a;
    }

    public void nrot() {
        Object a = values[top];
        Object b = values[top - 1];
        Object c = values[top - 2];
        values[top] = c;
        values[top - 1] = a;
        values[top - 2] = b;
    }

    public void over() {
        push(values[top - 1]);
    }

    public void under() {
        Object a = values[top];
        swap();
        push(a);
    }

    public void pick(int n) {
        Object a = values[top - n];
        values[top] = a;
    }

    public void roll(int anz) {
        Object a = values[top - 1];
        int untergrenze = top - anz;
        int i = top - 1;
        while (i > untergrenze) {
            values[i] = values[i - 1];
            i--;
        }
        values[untergrenze] = a;
    }

}
