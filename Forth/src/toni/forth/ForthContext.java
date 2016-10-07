package toni.forth;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ForthContext {
    private static final Logger LOG = LogManager.getLogger(ForthContext.class);

    final static int INTERPRET = 0;
    final static int COMPILE = 1;
    static Word emptyWord = new EmptyWord();

    int modus = INTERPRET;
    // int aktuellAusgeführtesWort;

    protected Dictionary wordDict;
    protected IntStack datenStack;
    protected IntStack returnStack;
    private IntStack programmStack;
    protected ObjectHeap objectHeap;
    protected ObjectStack objectStack;

    protected TokenStream input;
    protected Output output;

    public ForthContext(int dictSize, int dataSize, int returnSize) {
        super();
        this.wordDict = new Dictionary(dictSize);
        this.objectHeap = new ObjectHeap(dictSize);
        this.datenStack = new IntStack(dataSize);
        this.objectStack = new ObjectStack(dataSize);

        this.programmStack = new IntStack(returnSize);
        this.returnStack = new IntStack(returnSize);
        output = new SystemOutput();
    }

    public void say(TokenStream stream) {

        setTokenStream(stream);
        while (input.isOpen() || !programmStack.isEmpty()) {
            sayNextWord();
        }

    }

    public void sayNextWord() {
        Word w = getNextWord();
        if (w != null) {
            sayThisWordAndCheck(w);
        }
    }

    public Word getNextWord() {
        if (modus == COMPILE) {
            return nextWordFromInputStream();
        } else {
            if (programmStack.isEmpty()) {
                return nextWordFromInputStream();
            } else {
                return nextWordFromProgrammStack();
            }
        }
    }

    private Word nextWordFromProgrammStack() {
        return wordDict.getWord(programmStack.peek());
    }

    private Word nextWordFromInputStream() {
        String name;
        try {
            name = input.nextToken();
            if (name != null) {
                if ("".equals(name)) {
                    return emptyWord;
                }
                if (isNumber(name)) {
                    datenStack.push(Integer.parseInt(name));
                    if (modus == COMPILE) {
                        return wordDict.search("INTCONSTANT");
                    }
                    return emptyWord;
                } else {
                    return searchWord(name);
                }
            }
        } catch (IOException e) {
            LOG.error("Input Exception in nextWordFromInputStream", e);
            throw new IllegalStateException(
                    "Bei Einlesen vom Imput Stream ging etwas schief");
        }

        return null;
    }

    public Word searchWord(String name) {
        Word w = wordDict.search(name);
        if (w == null) {
            clear();
            throw new IllegalArgumentException("Can not find the word: " + name);
        }
        return w;
    }

    private void sayThisWordAndCheck(Word w) {
        if (w == null) {
            clear();
            throw new IllegalArgumentException("word is null");
        }
        try {
            sayThisWord(w);
        } catch (Exception ex) {
            clear();
            LOG.error("Exception in sayThisWordAndCheck", ex);
            throw new IllegalArgumentException(
                    "Error while execution of word: " + w.getName() + " "
                            + ex.getClass().getSimpleName() + " "
                            + ex.getMessage());
        }
    }

    private void sayThisWord(Word w) throws IOException {
        if (modus == INTERPRET) {
            w.say(this);
            next();
        } else {
            if (w.isImmediate()) {
                w.say(this);
            } else {
                wordDict.compile(w);
            }
        }
    }

    protected void gotoWord() {
        int nextPosition = programmStack.peek() + 1; // Nach dem Wort IF, ELSE
                                                     // usw. steht der
                                                     // Verweis auf
                                                     // Position wohin der
                                                     // Interpreter gehen
                                                     // soll.
        programmStack.pop();
        pushToProgrammStack(wordDict.fetch(nextPosition));
    }

    protected void next() {
        if (!programmStack.isEmpty()) {
            programmStack.plus1();
        }
    }

    protected void exitWord() {
        // if (!programmStack.isEmpty())
        {
            programmStack.pop();
        }
    }

    public void pushHeapPosition(Word w) {
        pushToProgrammStack(w.getHeapPosition());
    }

    public void pushGotoPos(CreateWord w) {
        pushToProgrammStack(w.getGotoPos());
    }

    private void pushToProgrammStack(int wordPosition) {
        programmStack.push(wordPosition);
    }

    protected void popFromProgrammStack(CreateWord w) {
        int aktuellesWort = programmStack.pop();
        w.setGotoPos(aktuellesWort); // merken um die Worte nach does> später
                                     // abzuarbeiten
                                     // Abbruch der Verarbeitung nach does>
    }

    private boolean isNumber(String token) {
        for (char c : token.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        programmStack.clear();
        returnStack.clear();
        datenStack.clear();
    }

    public void setTokenStream(TokenStream stream) {
        input = stream;
    }

    public IntStack getDatenStack() {
        return datenStack;
    }

    public IntStack getReturnStack() {
        return returnStack;
    }

    public IntStack getProgrammStack() {
        return programmStack;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public Dictionary getDictionary() {
        return wordDict;
    }

    public ObjectHeap getObjectHeap() {
        return objectHeap;
    }

    protected void compilationOn() {
        modus = COMPILE;
    }

    protected void interpreterOn() {
        if (!getProgrammStack().isEmpty()) {
            new ForthException("Darf nicht sein");
        }
        modus = INTERPRET;
    }

    public ObjectStack getObjectStack() {
        return objectStack;
    }
}
