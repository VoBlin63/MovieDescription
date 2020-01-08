package ru.buryachenko.moviedescription.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ru.buryachenko.moviedescription.database.TagRecord;
import ru.buryachenko.moviedescription.database.MovieRecord;

public class SonicUtils {

    public static List<TagRecord> makeCodes(MovieRecord movie) {
        Set<TagRecord> res = new HashSet<>();
        for (String word : getWordsList(movie.getTitle())) {
            res.add(new TagRecord(movie.getId(), ConvertibleTerms.topWord(Metaphone.code(word)), true));
        }
        for (String word : getWordsList(movie.getOverview())) {
            res.add(new TagRecord(movie.getId(), ConvertibleTerms.topWord(Metaphone.code(word)), false));
        }
        return new ArrayList<>(res);
    }

    public static List<String> getWordsList(String text) {
        LinkedList<String> res = new LinkedList<>();
        for (String word : text.toUpperCase().replace("Ё","Е").split("[^A-ZА-Я0-9]+")) {
            if (word.length() >= 3) {
                res.add(word);
            }
        }
        return res;
    }

}
