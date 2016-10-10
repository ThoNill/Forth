package aspects.interfaces;

public interface Updateable {

    public void needUpdate();

    public void validate();

    public boolean isDirty();
}
