package forth.gui;

import toni.forth.Dictionary;
import toni.forth.Word;

public class DictionaryConverter implements IntegerConverter {
    Dictionary dict;

    public DictionaryConverter(Dictionary dict) {
        super();
        this.dict = dict;
    }

    @Override
    public String convert(int n) {
        Word w = dict.getWord(n);
        if (w != null) {
            return w.getName();
        }
        return "???";
    }

}
