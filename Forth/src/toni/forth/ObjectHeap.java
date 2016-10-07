package toni.forth;

public class ObjectHeap {
    private int top;
    private Object objects[];

    public ObjectHeap(int size) {
        top = -1;
        objects = new Object[size];
    }

    public int insert(Object obj) {
        top++;
        objects[top] = obj;
        return top;
    }

    public void store(int pos, Object obj) {
        if (pos > top || pos < 0) {
            throw new IllegalArgumentException(
                    "Indexüberschreitung store im ObjektHeap " + pos);
        }
        objects[pos] = obj;
    }

    public Object fetch(int pos) {
        if (pos > top || pos < 0) {
            throw new IllegalArgumentException(
                    "Indexüberschreitung fetch im ObjektHeap " + pos);
        }
        return objects[pos];
    }

    public int getTop() {
        return top;
    }
}
