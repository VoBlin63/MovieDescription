package ru.buryachenko.moviedescription.utilities;

import java.util.HashMap;
import java.util.Map;

public class ConvertibleTerms {
    private static Map<String, String> lexicon = new HashMap<>();

    static {
        //TODO в идеале попробовать найти API чей-нибудь
        putTerm("сражение", "война");
        putTerm("конфликт", "война");
        putTerm("драка", "война");
    }

    private static void putTerm(String word, String top) {
        String value = Metaphone.metaphone(word.toUpperCase());
        String topVal = Metaphone.metaphone(top.toUpperCase());
        if (value.isEmpty() || topVal.isEmpty() || lexicon.containsKey(topVal)) {
            return;
        }
        lexicon.put(value, topVal);
    }

    public static String topWord(String word) {
        if (lexicon.containsKey(word)) {
            return lexicon.get(word);
        } else {
            return word;
        }
    }

}
