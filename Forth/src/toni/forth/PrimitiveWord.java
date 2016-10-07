package toni.forth;

import java.io.IOException;

public class PrimitiveWord extends Word {

    public PrimitiveWord(String name, MiniForth.Primitive id) {
        this(name, id, false);
    }

    public PrimitiveWord(String name, MiniForth.Primitive id, boolean immediate) {
        super(name);
        this.id = id;
        this.immediate = immediate;
    }

    MiniForth.Primitive id;

    @Override
    public void say(ForthContext context) throws IOException {
        if (context instanceof MiniForth) {
            ((MiniForth) context).say(id);
        }

    }

}
