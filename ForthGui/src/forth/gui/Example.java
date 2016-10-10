package forth.gui;

public enum Example {

    HALLOWELT("Hallo Welt!",
            " : HalloWelt! \" Hallo Welt!\" TYPE ; HalloWelt! "), DUPDUP(
            "dupdup", " : dupdup 1 DUP DUP  ; dupdup   "), LOOP("loop",
            " : loop 1 10 DO J . CR LOOP ; loop   "), LOOP2("loop2",
            " : loop 1 10 DO J . SPACE LOOP ; loop   "), FIBONACCI("Fibonacci",
            " : fibonacci 3 OVER > IF ELSE 1- DUP 1- RECURSE SWAP RECURSE + THEN  ; "), FAKULTÄT(
            "Fakultät", " : fakultät 1+ 1 1 -ROT DO  J * LOOP ; "), EMIT(
            "Emit", " 67 EMIT   ");

    private String description;
    private String programm;

    private Example(String d, String p) {
        this.description = d;
        this.programm = p;
    }

    public String getProgramm() {
        return programm;
    }

    public String getDescription() {
        return description;
    }

}
