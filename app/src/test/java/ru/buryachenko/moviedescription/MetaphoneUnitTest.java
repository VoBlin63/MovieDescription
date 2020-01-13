package ru.buryachenko.moviedescription;

import org.junit.Test;

import ru.buryachenko.moviedescription.utilities.Metaphone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MetaphoneUnitTest {
    String starCode = Metaphone.code("ЗВЕЗДА");

    @Test
    public void codeExample1() {
        assertEquals("PRFTN", Metaphone.code("ПРОВИДЕНИЕ"));
    }

    @Test
    public void codeExample2() {
        assertEquals("SFSTN", Metaphone.code("ЗВЕЗДНЫЙ"));
    }

    @Test
    public void codeExampleNumber() {
        assertEquals("314", Metaphone.code("314"));
    }

    @Test
    public void codeExampleSimilar() {
        assertEquals(starCode, Metaphone.code("ЗВЕЗДЫ"));
        assertEquals(starCode, Metaphone.code("ЗВЕЗДА"));
        assertEquals(starCode, Metaphone.code("ЗВИЗДЕ"));
        assertEquals(starCode, Metaphone.code("ЗВИЗДЕ"));
        assertEquals(starCode, Metaphone.code("ЗФИЗДУ"));
    }

    @Test

    public void codeExampleUnSimilar() {
        assertNotEquals(starCode, Metaphone.code("МЕЗДЫ"));
        assertNotEquals(starCode, Metaphone.code("ОБОЗ"));
        assertNotEquals(starCode, Metaphone.code("ГВОЗДЬ"));
        assertNotEquals(starCode, Metaphone.code("ПОЛЕНО"));
    }

}