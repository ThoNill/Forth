package toni.forth;

public class CreateWord extends CompositWord {
    int c;
    int gotoPos;

    public CreateWord(String name) {
        super(name);
    }

    @Override
    public void say(ForthContext context) {
        context.getDatenStack().push(c);
        context.pushGotoPos(this);
    }

    public void setC(int c) {
        this.c = c;
    }

    public void setGotoPos(int gotoPos) {
        this.gotoPos = gotoPos;
    }

    public int getGotoPos() {
        return gotoPos;
    }

}
