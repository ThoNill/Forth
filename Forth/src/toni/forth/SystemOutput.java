package toni.forth;

public class SystemOutput implements Output {

    @Override
    public void print(String text) {
        System.out.print(text);
    }

}
