package toni.forth;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class TokenStream {
    private static final Logger LOG = LogManager
            .getLogger(TokenStream.class);

    private char zeichen[] = new char[1];
    private boolean open = true;

    Reader reader;

    public TokenStream(Reader reader) {
        super();
        this.reader = reader;
    }

    public TokenStream(String text) {
        super();
        InputStream input = new ByteArrayInputStream(text.getBytes());
        try {
            this.reader = new InputStreamReader(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Error reading from the input stream", e);
        }
    }

    public String nextToken() throws IOException {
        StringBuilder builder = new StringBuilder();
        char c;
        do {
            c = nextChar(reader);
        } while (open && Character.isWhitespace(c));
        do {
            builder.append(c);
            c = nextChar(reader);
        } while (open && !Character.isWhitespace(c));

        return builder.toString().trim();
    }

    protected char nextChar(Reader reader) throws IOException {
        if (open && reader.ready()) {
            int count = reader.read(zeichen);
            if (count == 1) {
                return zeichen[0];
            }
        }
        open = false;
        return ' ';
    }

    public String allTextUntil(char stopAtChar) throws IOException {
        StringBuilder builder = new StringBuilder();
        char c;
        boolean weiter;
        do {
            c = nextChar(reader);
            weiter = open && c != stopAtChar;
            if (weiter) {
                builder.append(c);
            }
        } while (weiter);

        return builder.toString();
    }

    public boolean isOpen() {
        return open;
    }

}
