package toni.forth;

import java.io.IOException;

public abstract class Word implements Routine {
    String name;
    int position;
    int heapPosition;
    boolean immediate = false;

    public Word(String name) {
        super();
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see toni.forth.Routine#say(toni.forth.ForthContext)
     */
    @Override
    abstract public void say(ForthContext context) throws IOException;

    public int getHeapPosition() {
        return heapPosition;
    }

    public void setHeapPosition(int heapPosition) {
        this.heapPosition = heapPosition;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    @Override
    public String toString() {
        return "Word [name=" + name + ", position=" + position
                + ", heapPosition=" + heapPosition + ", immediate=" + immediate
                + "]";
    }
}