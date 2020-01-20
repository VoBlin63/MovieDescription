package ru.buryachenko.moviedescription;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import ru.buryachenko.moviedescription.api.PageMoviesJson;

public class MovieRecordUnitTest {
    PageMoviesJson mockPage = Mockito.mock(PageMoviesJson.class);

    @Test
    public void getMoviesFromPage() {
        Set<Integer> liked = new HashSet<>();
    }
}