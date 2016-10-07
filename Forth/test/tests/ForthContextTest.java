package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import toni.forth.MiniForth;
import toni.forth.TokenStream;
import toni.forth.Word;

public class ForthContextTest {
    private static final Logger LOG = LogManager
            .getLogger(ForthContextTest.class);
    private static final String ERWARTETE_EXCEPTION = "Erwartete Exception";

    private static final int MAX_DICT = 1000;

    @Test
    public void next() {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        f.getReturnStack().push(10);
        f.getReturnStack().plus1();
        int pos = f.getReturnStack().pop();

        assertEquals(11, pos);
    }

    @Test
    public void word() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        TokenStream stream = new TokenStream(" DUP ");
        f.setTokenStream(stream);
        Word w = f.getNextWord();

        assertEquals("DUP", w.getName());
    }

    @Test
    public void say() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        f.getDatenStack().push(10);
        say(f, " DUP ");

        assertEquals(2, f.getDatenStack().size());
    }

    @Test
    public void sayRet() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        f.getProgrammStack().push(2); // DUP in MiniForth
        f.getDatenStack().push(10);
        f.sayNextWord();

        testDSSize(f, 2);

        f.getProgrammStack().push(3); // DROP in MiniForth
        f.sayNextWord();

        testDSSize(f, 1);
    }

    @Test
    public void composit() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : firstWord DUP DUP ; ");

        testExistence(f, "firstWord");

        f.getDatenStack().push(10);

        say(f, " firstWord ");

        testDSSize(f, 3);
    }

    @Test
    public void intTest() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " 10 20 30 ");
        ;

        testDSSize(f, 3);
        assertEquals(30, f.getDatenStack().peek());
    }

    @Test
    public void compositCompositWords() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : firstWord DUP DUP ; ");
        say(f, " : secondWord DUP DUP DUP ; ");
        say(f, " : compWord firstWord secondWord ; ");

        testExistence(f, "firstWord");
        testExistence(f, "secondWord");
        testExistence(f, "compWord");

        f.getDatenStack().push(10);

        say(f, " compWord ");

        testDSSize(f, 3 + 2 + 1);
    }

    @Test
    public void compositCompositWords2() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : firstWord DUP DUP ; ");

        say(f, " : secondWord DUP DUP DUP ; ");

        say(f, " : compWord firstWord secondWord ; ");

        testExistence(f, "firstWord");
        testExistence(f, "secondWord");
        testExistence(f, "compWord");

        say(f, " 10 compWord ");

        testDSSize(f, 3 + 2 + 1);
    }

    @Test
    public void createBuilds() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : constant: <BUILDS COMPILE , DOES>  @ ; ");

        testExistence(f, "constant:");
        assertEquals(0, f.getReturnStack().size());

        say(f, " 3  ");

        assertEquals(3, f.getDatenStack().peek());

        say(f, " constant: drei ");

        testExistence(f, "drei");
        assertEquals(0, f.getReturnStack().size());

        say(f, " drei");

        assertEquals(0, f.getReturnStack().size());
        assertEquals(0, f.getProgrammStack().size());

        testDS(f, 3);
        f.getDatenStack().clear();

        say(f, " : drei2 drei ; drei2 ");

        assertEquals(0, f.getReturnStack().size());
        assertEquals(0, f.getProgrammStack().size());

        testDS(f, 3);

    }

    @Test
    public void wrong1() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : firstWord DUP DUP ; ");

        say(f, " : secondWord DUP DUP DUP ; ");

        say(f, " : compWord firstWord secondWord ; ");

        testExistence(f, "firstWord");
        testExistence(f, "secondWord");
        testExistence(f, "compWord");

        try {
            say(f, " compWord ");
            fail("No Exception");
        } catch (Exception ex) {
            LOG.error(ERWARTETE_EXCEPTION, ex);
        }

        testDSSize(f, 0);
    }

    @Test
    public void wrong2() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : firstWord DUP DUP ; ");

        say(f, " : secondWord DUP DUP DUP ; ");

        say(f, " : compWord firstWord secondWord ; ");

        testExistence(f, "firstWord");
        testExistence(f, "secondWord");
        testExistence(f, "compWord");

        f.getDatenStack().push(10);
        f.getDatenStack().push(10);

        try {
            say(f, " compWo ");

            fail("No Exception");
        } catch (Exception ex) {
            LOG.error(ERWARTETE_EXCEPTION, ex);

        }

        testDSSize(f, 0);
    }

    @Test
    public void ifelsetehn() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : ifelsethen .LOG IF DUP DUP ELSE DUP DROP DROP THEN ; ");

        testExistence(f, "ifelsethen");

        assertEquals(0, f.getReturnStack().size());
        testDSSize(f, 0);

        System.out.println("Programmstack");

        say(f, " 33 0 ");

        testDSSize(f, 2);

        System.out.println("Programmstack2");

        say(f, " ifelsethen ");

        testDSSize(f, 0);

        say(f, " 1 1 ifelsethen ");

        testDSSize(f, 3);

    }

    @Test
    public void ifelsetehn2() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : ifelsethen IF ELSE DUP THEN ; ");

        testExistence(f, "ifelsethen");
        System.out.println("Ret Stack");

        assertEquals(0, f.getReturnStack().size());
        testDSSize(f, 0);

        say(f, " 33 1 ");

        testDSSize(f, 2);

        say(f, " ifelsethen ");

        System.out.println("Ret Stack2");

        testDSSize(f, 1);

        say(f, " 1 0 ifelsethen ");

        testDSSize(f, 3);

        System.out.println("end of ");
    }

    @Test
    public void ifelsetehn3() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : ifelsethen IF DUP ELSE THEN ; ");

        testExistence(f, "ifelsethen");
        System.out.println("Ret Stack");

        assertEquals(0, f.getReturnStack().size());
        testDSSize(f, 0);

        say(f, " 33 0 ");

        testDSSize(f, 2);

        say(f, " ifelsethen ");

        testDSSize(f, 1);

        say(f, " 1 1 ifelsethen ");

        testDSSize(f, 3);

    }

    @Test
    public void ifelsetehn4() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : ifelsethen IF DUP THEN ; ");

        testExistence(f, "ifelsethen");
        System.out.println("Ret Stack");

        assertEquals(0, f.getReturnStack().size());
        testDSSize(f, 0);

        say(f, " 33 0 ");

        testDSSize(f, 2);

        say(f, " ifelsethen ");

        testDSSize(f, 1);

        say(f, " 1 1 ifelsethen ");

        testDSSize(f, 3);

    }

    @Test
    public void beginwhile() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : beginwhile BEGIN DUP 1- DUP >0 WHILE  ; ");

        testExistence(f, "beginwhile");
        System.out.println("Ret Stack");

        say(f, " 3 beginwhile ");

        testDSSize(f, 4);
    }

    @Test
    public void beginUntil() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : beginuntil DUP DROP DUP DROP BEGIN DUP 1- DUP =0 UNTIL  ; ");

        testExistence(f, "beginuntil");
        System.out.println("Ret Stack");

        say(f, " 3 beginuntil ");

        testDSSize(f, 4);
    }

    @Test
    public void beginAgain() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f,
                " : beginAgain BEGIN DUP 1- .LOG DUP  =0 IF EXIT THEN .LOG AGAIN  ; ");

        testExistence(f, "beginAgain");

        say(f, " 3 beginAgain ");

        testDSSize(f, 4);
    }

    @Test
    public void beginAgain2() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : beginAgain BEGIN DUP 1- .LOG  AGAIN  ; ");

        testExistence(f, "beginAgain");

        try {

            say(f, " 3 beginAgain ");
            fail("Keine Exception");
        } catch (Exception ex) {
            LOG.error(ERWARTETE_EXCEPTION, ex);
        }

        testDSSize(f, 0);
    }

    @Test
    public void loop() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : loop DO 1+ J . LOOP  ; ");

        testExistence(f, "loop");

        say(f, " 0 1 1 loop ");

        testDS(f, 0);

        say(f, " 0 1 2 loop ");

        testDS(f, 1);

        say(f, " 0 1 10 loop ");

        testDS(f, 9);

        say(f, " 0 10 1 loop ");

        testDS(f, 0);
    }

    @Test
    public void loop2() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : loop DO R> R> 2DUP >R >R DO 1+ LOOP LOOP  ; ");

        testExistence(f, "loop");

        say(f, " 0  1 1 loop ");

        testDS(f, 0);

        say(f, " 1 1 2 loop ");

        testDS(f, 2);

        say(f, " 0 1 10 loop ");

        testDS(f, 45);

    }

    @Test
    public void integerConstant() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : intconst 2 3  ; ");

        testExistence(f, "intconst");

        say(f, " intconst ");

        testDS(f, 3);
        testDS(f, 2);

        say(f, " 2 3 ");

        testDS(f, 3);
        testDS(f, 2);
    }

    @Test
    public void text() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " \" das ist ein Test\" ");

        testDSSize(f, 2);
        testDS(f, "das ist ein Test".length());

        int pos = f.getDatenStack().pop();
        String text = (String) f.getObjectHeap().fetch(pos);
        assertEquals("das ist ein Test", text);

    }

    @Test
    public void compileText() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " : text \" das ist ein Test\" ; ");

        testExistence(f, "text");

        say(f, " text ");

        testDSSize(f, 2);
        testDS(f, "das ist ein Test".length());

        int pos = f.getDatenStack().pop();
        String text = (String) f.getObjectHeap().fetch(pos);
        assertEquals("das ist ein Test", text);

    }

    @Test
    public void emit() {
        byte bi = (byte) 67;
        byte b[] = new byte[1];
        b[0] = bi;
        ByteBuffer buffer = ByteBuffer.wrap(b);

        Charset chars = Charset.forName("ISO-8859-15");
        CharsetDecoder decoder = chars.newDecoder();

        CharBuffer cb;
        try {
            cb = decoder.decode(buffer);
            String text = cb.toString();
            assertEquals("C", text);
        } catch (CharacterCodingException e) {
            LOG.error("Unerwartete Exception", e);
            fail("Ausnahme aufgetreten");
        }
    }

    @Test
    public void recurse() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 100, 100);

        say(f,
                " : fibonacci 3 OVER > IF ELSE 1- DUP 1- RECURSE SWAP RECURSE + THEN  ; ");

        say(f, " 1 fibonacci ");

        testDS(f, 1);

        say(f, " 2 fibonacci ");

        testDS(f, 2);

        say(f, " 3 fibonacci ");

        testDS(f, 3);

        say(f, " 4 fibonacci ");

        testDS(f, 5);

        say(f, " 5 fibonacci ");

        testDS(f, 8);

    }

    @Test
    public void deferAndIS() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " DEFER 1stelligeFunktion  ");

        testExistence(f, "1stelligeFunktion");

        say(f, " 2 1stelligeFunktion ");

        testDS(f, 2);

        say(f, " : 3mal 3 * ; ");

        say(f, " ' 3mal IS 1stelligeFunktion  ");

        say(f, " 2 1stelligeFunktion ");

        testDS(f, 6);

    }

    @Test
    public void constant() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " 3 CONSTANT: drei ");

        testExistence(f, "drei");

        say(f, " drei ");

        testDS(f, 3);
    }

    @Test
    public void importClass() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " IMPORT: java.lang.String ");

        testExistence(f, "java.lang.String");

        say(f, " java.lang.String ");

        Object cl = f.getObjectStack().pop();

        assertEquals(java.lang.String.class, cl);

    }

    @Test
    public void method() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 10, 10);

        say(f, " IMPORT: java.lang.String ");

        testExistence(f, "java.lang.String");

        say(f, " java.lang.String o\" length\" 0 METHOD: strlen ");

        testExistence(f, "strlen");

        say(f, "  o\" length\"  strlen  ");

        assertEquals("6", f.getObjectStack().pop().toString());

    }

    @Test
    public void constructor() throws IOException {
        MiniForth f = new MiniForth(MAX_DICT, 20, 20);

        say(f, " IMPORT: java.lang.String ");

        testExistence(f, "java.lang.String");

        say(f, " java.lang.String java.lang.String 1 CONSTRUCTOR: fromString ");

        testExistence(f, "fromString");

        say(f, "  o\" neuer String\"  fromString  ");

        assertEquals("neuer String", f.getObjectStack().pop().toString());

    }

    protected void say(MiniForth f, String text) {
        TokenStream stream = new TokenStream(text);
        f.say(stream);
        f.say(stream);
    }

    protected void testExistence(MiniForth f, String wordName) {
        assertEquals(wordName, f.searchWord(wordName).getName());
    }

    protected void testDS(MiniForth f, int n) {
        assertEquals(n, f.getDatenStack().pop());
    }

    protected void testDSSize(MiniForth f, int n) {
        assertEquals(n, f.getDatenStack().size());
    }

}
