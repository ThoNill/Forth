package toni.forth;

import java.io.IOException;

public interface Routine {

	void say(ForthContext context) throws IOException;

}