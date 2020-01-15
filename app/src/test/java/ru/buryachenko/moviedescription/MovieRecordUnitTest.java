package ru.buryachenko.moviedescription;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import ru.buryachenko.moviedescription.api.MovieLoader;
import ru.buryachenko.moviedescription.api.PageMoviesJson;
import ru.buryachenko.moviedescription.database.MovieRecord;

public class MovieRecordUnitTest {
    MovieRecord dataServiceSpy = Mockito.spy(MovieRecord.class);
    PageMoviesJson mockPage = Mockito.mock(PageMoviesJson.class);

    @Test
    public void getMoviesFromPage() {
        Set<Integer> liked = new HashSet<>();
        MovieLoader.getMoviesFromPage(mockPage, liked);
    }
}