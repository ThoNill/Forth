package toni.forth;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MiniForth extends ForthContext {
    private static final Logger LOG = LogManager.getLogger(MiniForth.class);

    enum Primitive {
        SWAP, DUP, DROP, DUP2, DROP2, ROT, NROT, ROT2, OVER, OVER2, UNDER, PICK, ROLL, IFDUP, DEPTH, TORET, FROMRET, COPYRET, K, ADD, MULT, MULTDIV, MULTDIVMOD, SUB, DIV, DIVMOD, PLUS1, MINUS1, PLUS2, MINUS2, ABS, NEGATE, MIN, MAX, LT0, GT0, EQ0, LT, GT, EQ, NEQ, AND, OR, NOT,

        EXIT, NEWWORD, WORDEND, KOMMA, WORD2STACK, COMPILEWORD, IMMEDIATE, CONSTANT, CONSTANTLAUFZEIT, BUILDS, DOES,

        IF, THEN, ELSE, BEGIN, WHILE, UNTIL, AGAIN, DO, LOOP, RECURSE, IS, DEFER, EMPTY, LOG,

        FETCH, STORE, POINT, INTCONSTANT, CREATESTRING, CREATESTRINGOBJ, STRINGCONSTANT, TYPE, CR, SPACE, SPACES, EMIT, STR2OBJ, STACK2OBJ, OBJ2STACK, POINTOBJ, IMPORT, OBJCONSTLAUFZEIT, OBJFETCCH, OBJSTORE,

        OSWAP, ODUP, ODROP, OROT, ONROT, OOVER, OUNDER, OPICK, OROLL, FETCHMETHOD, CALL, FETCHCONSTRUCTOR, NEW

    };

    private Word returnWord;
    private Word ifWord;
    private Word elseWord;
    private Word whileWord;
    private Word untilWord;
    private Word againWord;
    private Word loopWord;
    private Word doWord;

    private Word intConstant;
    private Word stringConstant;
    ByteBuffer buffer = ByteBuffer.allocate(100);
    Charset chars = Charset.forName("ISO-8859-15");
    private Word constWord;
    private Word objConstWord;
    private Word callWord;
    private Word newWord;

    public MiniForth(int dictSize, int dataSize, int returnSize) {
        super(dictSize, dataSize, returnSize);
        returnWord = primitive(Primitive.EXIT);
        primitive(Primitive.SWAP);
        primitive(Primitive.DUP);
        primitive(Primitive.DROP);
        primitive("2DROP", Primitive.DROP2);
        primitive("2DUP", Primitive.DUP2);
        primitive(Primitive.ROT);
        primitive("-ROT", Primitive.NROT);
        primitive("2ROT", Primitive.ROT2);
        primitive(Primitive.OVER);
        primitive("2OVER", Primitive.OVER2);
        primitive(Primitive.UNDER);
        primitive(Primitive.PICK);
        primitive(Primitive.ROLL);
        primitive("?DUP", Primitive.IFDUP);
        primitive(Primitive.DEPTH);

        primitive(">R", Primitive.TORET);
        primitive("R>", Primitive.FROMRET);
        primitive("R@", Primitive.COPYRET);
        primitive("J", Primitive.COPYRET); // Schleifenindex
        primitive("K", Primitive.K);

        primitive("+", Primitive.ADD);
        primitive("*", Primitive.MULT);
        primitive("*/", Primitive.MULTDIV);
        primitive("*/MOD", Primitive.MULTDIVMOD);
        primitive("-", Primitive.SUB);
        primitive("/", Primitive.DIV);
        primitive("/MOD", Primitive.DIVMOD);

        primitive("1+", Primitive.PLUS1);
        primitive("1-", Primitive.MINUS1);
        primitive("2+", Primitive.PLUS2);
        primitive("2-", Primitive.MINUS2);
        primitive(Primitive.ABS);
        primitive(Primitive.NEGATE);
        primitive(Primitive.MIN);
        primitive(Primitive.MAX);

        primitive("<0", Primitive.LT0);
        primitive(">0", Primitive.GT0);
        primitive("=0", Primitive.EQ0);
        primitive("<", Primitive.LT);
        primitive(">", Primitive.GT);
        primitive("=", Primitive.EQ);
        primitive("!=", Primitive.NEQ);
        primitive("<>", Primitive.NEQ);
        primitive("&&", Primitive.AND);
        primitive(Primitive.AND);
        primitive("||", Primitive.OR);
        primitive(Primitive.OR);
        primitive("~", Primitive.NOT);
        primitive(Primitive.NOT);
        primitive(":", Primitive.NEWWORD);
        immediate(";", Primitive.WORDEND);
        immediate(",", Primitive.KOMMA);
        immediate("'", Primitive.WORD2STACK);
        immediate("COMPILE", Primitive.COMPILEWORD);
        primitive(Primitive.IMMEDIATE);
        primitive("CONSTANT:", Primitive.CONSTANT);
        constWord = primitive("(CONSTANT)", Primitive.CONSTANTLAUFZEIT);
        primitive("<BUILDS", Primitive.BUILDS);
        primitive("<BUILDS", Primitive.BUILDS);
        primitive("DOES>", Primitive.DOES);
        primitive("@", Primitive.FETCH);
        primitive("!", Primitive.STORE);
        primitive(".", Primitive.POINT);
        primitive(".LOG", Primitive.LOG);
        primitive(Primitive.TYPE);
        primitive(Primitive.CR);
        primitive(Primitive.SPACE);
        primitive(Primitive.SPACES);
        primitive(Primitive.EMIT);

        primitive("S>OBJ", Primitive.STR2OBJ);
        primitive(">OBJ", Primitive.STACK2OBJ);
        primitive("OBJ>", Primitive.OBJ2STACK);
        primitive(".OBJ", Primitive.POINTOBJ);
        primitive("OBJ@", Primitive.OBJFETCCH);
        primitive("OBJ!", Primitive.OBJSTORE);
        primitive("IMPORT:", Primitive.IMPORT);
        objConstWord = primitive("(IMPORT)", Primitive.OBJCONSTLAUFZEIT);
        primitive(Primitive.POINTOBJ);

        primitive(Primitive.OSWAP);
        primitive(Primitive.ODUP);
        primitive(Primitive.ODROP);
        primitive(Primitive.OROT);
        primitive("-ROT", Primitive.ONROT);
        primitive(Primitive.OOVER);
        primitive(Primitive.OUNDER);
        primitive(Primitive.OROLL);
        primitive("METHOD:", Primitive.FETCHMETHOD);
        callWord = primitive(Primitive.CALL);
        primitive("CONSTRUCTOR:", Primitive.FETCHCONSTRUCTOR);
        newWord = primitive(Primitive.NEW);

        immediate(Primitive.RECURSE);
        primitive(Primitive.DEFER);

        primitive(Primitive.IS);
        emptyWord = primitive("(EMPTY)", Primitive.EMPTY);

        immediate("\"", Primitive.CREATESTRING);
        immediate("o\"", Primitive.CREATESTRINGOBJ);

        intConstant = immediate(Primitive.INTCONSTANT);
        stringConstant = primitive(Primitive.STRINGCONSTANT);

        ifWord = immediate(Primitive.IF);
        immediate(Primitive.THEN);
        elseWord = immediate(Primitive.ELSE);

        immediate(Primitive.BEGIN);
        whileWord = immediate(Primitive.WHILE);

        untilWord = immediate(Primitive.UNTIL);
        againWord = immediate(Primitive.AGAIN);

        doWord = immediate(Primitive.DO);
        loopWord = immediate(Primitive.LOOP);
    }

    public void say(Primitive key) throws IOException {

        switch (key) {
        case SWAP:
            datenStack.swap();
            break;
        case DUP:
            datenStack.dup();
            break;
        case DROP:
            datenStack.drop();
            return;
        case DROP2:
            datenStack.drop2();
            return;

        case DUP2:
            datenStack.dup2();
            return;
        case ROT:
            datenStack.rot();
            return;
        case NROT:
            datenStack.nrot();
            return;
        case ROT2:
            datenStack.rot2();
            return;
        case OVER:
            datenStack.over();
            return;
        case OVER2:
            datenStack.over2();
            return;
        case PICK:
            datenStack.pick();
            return;
        case ROLL:
            datenStack.roll();
            return;
        case UNDER:
            datenStack.under();
            return;
        case IFDUP:
            datenStack.ifNot0dup();
            return;
        case DEPTH:
            datenStack.depth();
            return;
        case FROMRET:
            pushDS(returnStack.pop());
            return;
        case TORET:
            returnStack.push(popDS());
            return;
        case COPYRET:
            pushDS(returnStack.peek());
            return;
        case K:
            pushDS(returnStack.pick(2));
            return;
        case ADD:
            datenStack.add();
            return;
        case MULT:
            datenStack.mult();
            return;
        case MULTDIV:
            datenStack.multdiv();
            return;
        case MULTDIVMOD:
            datenStack.multdivmod();
            return;
        case SUB:
            datenStack.sub();
            return;
        case DIV:
            datenStack.div();
            return;
        case DIVMOD:
            datenStack.divmod();
            return;
        case PLUS1:
            datenStack.plus1();
            return;
        case MINUS1:
            datenStack.minus1();
            return;
        case PLUS2:
            datenStack.plus2();
            return;
        case MINUS2:
            datenStack.minus2();
            return;
        case ABS:
            datenStack.abs();
            return;
        case NEGATE:
            datenStack.negate();
            return;
        case MIN:
            datenStack.min();
            return;
        case MAX:
            datenStack.max();
            return;
        case LT0:
            datenStack.lt0();
            return;
        case GT0:
            datenStack.gt0();
            return;
        case EQ0:
            datenStack.equals0();
            return;
        case LT:
            datenStack.lt();
            return;
        case GT:
            datenStack.gt();
            return;
        case EQ:
            datenStack.equalOnStack();
            return;
        case NEQ:
            datenStack.unequalOnStack();
            return;
        case AND:
            datenStack.and();
            return;
        case OR:
            datenStack.or();
            return;
        case NOT:
            datenStack.not();
            return;
        case EXIT:
            exitWord();
            return;
        case NEWWORD:
            newCompositWord();
            compilationOn();
            break;
        case WORD2STACK:
            wordToStack();
            break;
        case COMPILEWORD:
            compileWord();
            break;
        case WORDEND:
            wordDict.compile(returnWord);
            interpreterOn();
            break;
        case KOMMA:
            wordDict.compile(popDS());
            break;
        case IMMEDIATE:
            wordDict.lastIsImediate();
            break;
        case CONSTANT:
            doConstant();
            break;
        case CONSTANTLAUFZEIT:
            doConstantLaufzeit();
            break;
        case BUILDS:
            build();
            break;
        case DOES:
            does();
            break;
        case IF:
            doIf();
            break;
        case THEN:
            doThen();
            break;
        case ELSE:
            doElse();
            break;
        case BEGIN:
            doBegin();
            break;
        case WHILE:
            doWhile();
            break;
        case UNTIL:
            doUntil();
            break;
        case AGAIN:
            doAgain();
            break;
        case DO:
            doDo();
            break;
        case LOOP:
            doLoop();
            break;
        case DEFER:
            doDefer();
            break;
        case RECURSE:
            doRecurse();
            break;
        case IS:
            doIs();
            break;
        case EMPTY:
            break;
        case LOG:
            log();
            break;
        case FETCH:
            pushDS(wordDict.fetch(popDS()));
            break;
        case STORE:
            int pos = popDS();
            int value = popDS();
            wordDict.store(pos, value);
            break;
        case POINT:
            output.print(Integer.toString(popDS()));
            break;
        case INTCONSTANT:
            integerConstant();
            break;
        case CREATESTRING:
            createString();
            break;
        case CREATESTRINGOBJ:
            createStringObj();
            break;
        case STRINGCONSTANT:
            stringConstant();
            break;
        case TYPE:
            type();
            break;
        case CR:
            output.print("\r\n");
            break;
        case SPACE:
            output.print(" ");
            break;
        case SPACES:
            spaces();
            break;
        case EMIT:
            emit();
            break;
        case STR2OBJ:
            str2Obj();
            break;
        case STACK2OBJ:
            stack2Obj();
            break;
        case OBJ2STACK:
            obj2Stack();
            break;
        case POINTOBJ:
            printObj();
            break;
        case IMPORT:
            importClass();
            break;
        case OBJCONSTLAUFZEIT:
            importLaufzeit();
            break;
        case OBJFETCCH:
            objFetch();
            break;
        case OBJSTORE:
            objStore();
            break;
        case OSWAP:
            objectStack.swap();
            break;
        case ODUP:
            objectStack.dup();
            break;
        case ODROP:
            objectStack.drop();
            return;
        case OROT:
            objectStack.rot();
            return;
        case ONROT:
            objectStack.nrot();
            return;
        case OOVER:
            objectStack.over();
            return;
        case OPICK:
            objectStack.pick(popDS());
            return;
        case OROLL:
            objectStack.roll(popDS());
            return;
        case OUNDER:
            objectStack.under();
            return;
        case FETCHMETHOD:
            doFetchMethod();
            return;
        case CALL:
            doCallMethod();
            return;
        case FETCHCONSTRUCTOR:
            doFetchConstructor();
            return;
        case NEW:
            doCallConstructor();
            return;
        default:
            break;

        }

    }

    private void doCallMethod() {
        Method m = (Method) objectStack.pop();
        Object obj = getObjectStack().pop();
        int anz = m.getParameterCount();
        Object[] args = new Object[anz];
        for (int i = 1; i <= anz; i++) {
            args[anz - i] = objectStack.pop();
        }
        try {
            objectStack.push(m.invoke(obj, args));
        } catch (IllegalAccessException e) {
            LOG.error("Exception in doCallMethod", e);
            throw new ForthException("Darf die Methode " + m.getName()
                    + " in der Klasse " + m.getDeclaringClass().getName()
                    + " für das Object " + obj.toString() + " nicht aufrufen");
        } catch (IllegalArgumentException e) {
            LOG.error("Exception in doCallMethod", e);
            throw new ForthException("Falsche Argumente für die Methode "
                    + m.getName() + " in der Klasse "
                    + m.getDeclaringClass().getName() + " für das Object "
                    + obj.toString() + " nicht aufrufen");

        } catch (InvocationTargetException e) {
            LOG.error("Exception in doCallMethod", e);
            throw new ForthException("Falsches Zielob"
                    + "jekt für die Methode " + m.getName() + " in der Klasse "
                    + m.getDeclaringClass().getName() + " für das Object "
                    + obj.toString() + " nicht aufrufen");

        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void doFetchMethod() throws IOException {
        int anz = popDS();
        Class[] parameterTypes = new Class[anz];
        for (int i = 1; i <= anz; i++) {
            parameterTypes[anz - i] = (Class) objectStack.pop();
        }
        String name = (String) objectStack.pop();
        Class cl = (Class) objectStack.pop();
        try {
            Method m = cl.getMethod(name, parameterTypes);
            int position = objectHeap.insert(m);
            createConstantWord(position, objConstWord, callWord);

        } catch (NoSuchMethodException e) {
            LOG.error("Exception in doFetchMethod", e);
            throw new ForthException("Konnte die Methode " + name
                    + " in der Klasse " + cl.getName() + " nicht finden");
        } catch (SecurityException e) {
            LOG.error("Exception in doFetchMethod", e);
            throw new ForthException("Die Methode " + name + " in der Klasse "
                    + cl.getName() + " ist geschützt");
        }
    }

    @SuppressWarnings({ "rawtypes" })
    private void doCallConstructor() {
        Constructor m = (Constructor) objectStack.pop();
        int anz = m.getParameterCount();
        Object[] args = new Object[anz];
        for (int i = 1; i <= anz; i++) {
            args[anz - i] = objectStack.pop();
        }
        try {
            objectStack.push(m.newInstance(args));
        } catch (IllegalAccessException e) {
            LOG.error("Exception in doCallConstructor", e);
            throw new ForthException("Darf den Kontruktor in der Klasse "
                    + m.getDeclaringClass().getName() + "  nicht aufrufen");
        } catch (IllegalArgumentException e) {
            LOG.error("Exception in doCallConstructor", e);
            throw new ForthException(
                    "Falsche Argumente für den Kontruktor der Klasse "
                            + m.getDeclaringClass().getName());

        } catch (InstantiationException e) {
            LOG.error("Exception in doCallConstructor", e);
            throw new ForthException("Der Kontruktor der Klasse "
                    + m.getDeclaringClass().getName()
                    + " kann das Objekt nicht erzeugen");
        } catch (InvocationTargetException e) {
            LOG.error("Exception in doCallConstructor", e);
            throw new ForthException("Der Kontruktor der Klasse "
                    + m.getDeclaringClass().getName()
                    + " passt nicht zur Klasse");
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void doFetchConstructor() throws IOException {
        int anz = popDS();
        Class[] parameterTypes = new Class[anz];
        for (int i = 1; i <= anz; i++) {
            parameterTypes[anz - i] = (Class) objectStack.pop();
        }
        Class cl = (Class) objectStack.pop();
        try {
            Constructor m = cl.getDeclaredConstructor(parameterTypes);
            int position = objectHeap.insert(m);
            createConstantWord(position, objConstWord, newWord);

        } catch (NoSuchMethodException e) {
            LOG.error("Exception in doFetchConstructor", e);
            throw new ForthException("Konnte den Konstruktor in der Klasse "
                    + cl.getName() + " nicht finden");
        } catch (SecurityException e) {
            LOG.error("Exception in doFetchConstructor", e);
            throw new ForthException("Die Kontruktor in der Klasse "
                    + cl.getName() + " ist geschützt");
        }

    }

    private void objStore() {
        int pos = popDS();
        Object obj = popOS();
        objectHeap.store(pos, obj);

    }

    private void objFetch() {
        int pos = popDS();
        pushOS(objectHeap.fetch(pos));
    }

    private void importClass() throws IOException {
        String name = input.nextToken();
        try {
            Class cl = loadClassFromName(name);
            int position = objectHeap.insert(cl);
            createConstantWord(name, position, objConstWord);
        } catch (ClassNotFoundException e) {
            LOG.error("Exception in importClass", e);
            throw new ForthException("Die Klasse " + name
                    + " wurde nicht gefunden");
        }
    }

    private Class loadClassFromName(String name) throws ClassNotFoundException {
        getClass();
        return Class.forName(name);
    }

    private void importLaufzeit() {
        pushOS(objectHeap.fetch(popDS()));
    }

    private void doConstant() throws IOException {
        int position = popDS();
        createConstantWord(position);
    }

    private void doConstantLaufzeit() {
        int c = wordDict.fetch(getProgrammStack().peek() + 1);
        pushDS(c);
        next();
    }

    protected void createConstantWord(int position, Word... zurLaufzeit)
            throws IOException {
        String name = input.nextToken();
        createConstantWord(name, position, zurLaufzeit);
    }

    protected void createConstantWord(String name, int position,
            Word... zurLaufzeit) {

        Word w = new CompositWord(name);
        wordDict.insert(w);
        wordDict.compile(w);
        wordDict.compile(constWord);
        wordDict.compile(position);
        for (Word lw : zurLaufzeit) {
            wordDict.compile(lw);
        }
        wordDict.compile(returnWord);
    }

    private void printObj() {
        Object obj = popOS();
        if (obj != null) {
            output.print(obj.toString());
        }
    }

    private void obj2Stack() {
        Object obj = popOS();
        if (obj == null) {
            pushDS(0);
        } else {
            if (obj instanceof Integer) {
                pushDS(((Integer) obj).intValue());
            } else {
                String s = obj.toString();
                int objPosition = objectHeap.insert(s);
                pushDS(s.length());
                pushDS(objPosition);
            }
        }

    }

    private void stack2Obj() {
        pushOS(popDS());
    }

    private void str2Obj() {
        popDS();
        int strPosition = popDS();
        Object obj = objectHeap.fetch(strPosition);
        pushOS(obj);
    }

    protected int popDS() {
        return datenStack.pop();
    }

    public void pushDS(int n) {
        datenStack.push(n);
    }

    protected int popRS() {
        return returnStack.pop();
    }

    public void pushRS(int n) {
        returnStack.push(n);
    }

    protected Object popOS() {
        return objectStack.pop();
    }

    public void pushOS(Object obj) {
        objectStack.push(obj);
    }

    private void doIs() throws IOException {
        int positionZielWort = popDS();
        String name = input.nextToken();
        Word w = wordDict.search(name);
        if (w != null) {
            int wordNumber = wordDict.fetch(positionZielWort);
            wordDict.store(w.getHeapPosition() + 1, wordNumber); // Ersetzen von
                                                                 // emptyword
        } else {
            throw new ForthException("Das Wort " + name
                    + " ist nicht vorhanden");
        }
    }

    private void doRecurse() {
        wordDict.compileRecursiverAufruf();

    }

    private void doDefer() throws IOException {
        newCompositWord();
        // wordDict.compile(deferedWord);
        wordDict.compile(emptyWord);
        wordDict.compile(returnWord);
    }

    private void emit() {
        byte zeichenByte = (byte) 67;
        byte kleinerBuffer[] = new byte[1];
        kleinerBuffer[0] = zeichenByte;
        ByteBuffer buffer = ByteBuffer.wrap(kleinerBuffer);

        // Charset chars = Charset.forName("ISO-8859-15");
        CharsetDecoder decoder = chars.newDecoder();

        CharBuffer cb;
        try {
            cb = decoder.decode(buffer);
            String text = cb.toString();
            output.print(text);
        } catch (CharacterCodingException e) {
            LOG.error("Exception in emit", e);
            throw new ForthException("Das Zeichen mit dem Code " + zeichenByte
                    + " läßt sich nicht umwandeln");
        }

    }

    protected void spaces() {
        {
            for (int n = popDS(); n > 0; n--) {
                output.print(" ");
            }
        }
    }

    private void type() {
        int len = popDS();
        String text = (String) objectHeap.fetch(popDS());
        if (len < text.length()) {
            text = text.substring(0, len);
        }
        output.print(text);
        while (len > text.length()) {
            len--;
            output.print(" ");
        }
    }

    private void createString() throws IOException {
        String text = input.allTextUntil('"');
        if (modus == INTERPRET) {
            pushDS(objectHeap.insert(text));
            pushDS(text.length());
            next();
        } else {
            wordDict.compile(stringConstant);
            wordDict.compile(objectHeap.insert(text));
        }

    }

    private void createStringObj() throws IOException {
        String text = input.allTextUntil('"');
        if (modus == INTERPRET) {
            pushOS(text);
            next();
        } else {
            wordDict.compile(objConstWord);
            wordDict.compile(objectHeap.insert(text));
        }
    }

    private void stringConstant() throws IOException {
        int programmPosition = getProgrammStack().peek();
        int heapPosition = wordDict.fetch(programmPosition + 1);

        String text = (String) objectHeap.fetch(heapPosition);
        pushDS(heapPosition);
        pushDS(text.length());

        next();
    }

    private void integerConstant() {
        if (modus == INTERPRET) {
            int programmPosition = getProgrammStack().peek();
            pushDS(wordDict.fetch(programmPosition + 1));
            next();
        } else {
            wordDict.compile(intConstant);
            wordDict.compile(popDS());
        }

    }

    private void doLoop() {
        if (modus == INTERPRET) {
            returnStack.plus1();
            pushDS(returnStack.pop());
            pushDS(returnStack.pop());
            gotoWord();

        } else {
            wordDict.compile(loopWord);
            int doPosition = returnStack.pop();

            wordDict.store(doPosition, wordDict.getTopHeap());

            wordDict.compile(doPosition - 2);
        }
    }

    private void doDo() {
        if (modus == INTERPRET) {

            datenStack.dup2();
            datenStack.lt();

            if (popDS() == 0) {
                datenStack.drop2();
                gotoWord();
            } else {
                returnStack.push(popDS());
                returnStack.push(popDS());
                next();
            }

        } else {
            wordDict.compile(doWord);
            wordDict.compile(0);
            returnStack.push(wordDict.getTopHeap() - 1);
        }

    }

    private void doWhile() {
        if (modus == INTERPRET) {
            if (popDS() == 0) {
                next();
            } else {
                gotoWord();
            }

        } else {
            wordDict.compile(whileWord);
            wordDict.compile(returnStack.pop());
        }

    }

    private void doUntil() {
        if (modus == INTERPRET) {
            if (popDS() == 0) {
                gotoWord();
            } else {
                next();
            }

        } else {
            wordDict.compile(untilWord);
            wordDict.compile(returnStack.pop());
        }

    }

    private void doAgain() {
        if (modus == INTERPRET) {
            gotoWord();

        } else {
            wordDict.compile(againWord);
            wordDict.compile(returnStack.pop());

        }

    }

    private void doBegin() {
        if (modus == COMPILE) { // Von WHILE zum ersten Wort nach BEGIN springen
            returnStack.push(wordDict.getTopHeap() - 1);
        }

    }

    private void doIf() {
        if (modus == INTERPRET) {
            if (popDS() > 0) {
                next();
            } else {
                gotoWord();
            }

        } else {

            wordDict.compile(ifWord);
            wordDict.compile(0);
            returnStack.push(wordDict.getTopHeap() - 1);
        }
    }

    private void doElse() {
        if (modus == INTERPRET) {
            gotoWord();
        } else {
            int positionIf = returnStack.pop();
            int positionElse = wordDict.getTopHeap();
            wordDict.store(positionIf, positionElse + 1); // Von IF Bedingung
                                                          // falsch = hinter
                                                          // das ELSE springen

            wordDict.compile(elseWord);
            wordDict.compile(0);
            returnStack.push(wordDict.getTopHeap() - 1);
        }
    }

    private void doThen() {
        if (modus == COMPILE) { // Von ELSE zum letzten Wort vor THEN springen,
                                // da kein Wort für THEn eingefügt wird
            int positionElse = returnStack.pop();
            int positionThen = wordDict.getTopHeap();
            wordDict.store(positionElse, positionThen - 1);
        }
    }

    protected void compileWord() throws IOException {
        String name = input.nextToken();
        Word w = searchWord(name);
        wordDict.compile(w);

    }

    protected void wordToStack() throws IOException {
        String name = input.nextToken();
        Word w = searchWord(name);
        pushDS(w.getHeapPosition());
    }

    protected void newCompositWord() throws IOException {
        Word w = CreateWord();
        w.setHeapPosition(wordDict.getTopHeap());
        wordDict.compile(w);
    }

    protected Word CreateWord() throws IOException {
        String name = input.nextToken();
        Word w = new CompositWord(name);
        wordDict.insert(w);
        return w;
    }

    protected void build() throws IOException {
        String name = input.nextToken();
        Word w = new CreateWord(name);
        wordDict.insert(w);
        w.setHeapPosition(wordDict.getTopHeap());
        wordDict.compile(w);

        returnStack.push(wordDict.getTopHeap());
    }

    protected void does() throws IOException {
        CreateWord w = (CreateWord) wordDict.getLastInsertedWord();
        w.setC(returnStack.pop());
        popFromProgrammStack(w);
    }

    public void log() {
        wordDict.log();
        returnStack.log();
        datenStack.log();
    }

    private Word primitive(Primitive p) {
        return wordDict.insertAndCompile(new PrimitiveWord(p.name(), p));
    }

    private Word primitive(String name, Primitive p) {
        return wordDict.insertAndCompile(new PrimitiveWord(name, p));
    }

    private Word immediate(String name, Primitive p) {
        return wordDict.insertAndCompile(new PrimitiveWord(name, p, true));
    }

    private Word immediate(Primitive p) {
        return wordDict.insertAndCompile(new PrimitiveWord(p.name(), p, true));
    }

}
