package ru.buryachenko.moviedescription;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;

import ru.buryachenko.moviedescription.api.MovieLoader;
import ru.buryachenko.moviedescription.api.PageMoviesJson;
import ru.buryachenko.moviedescription.database.MovieRecord;

public class MovieRecordUnitTest {
    MovieRecord dataServiceSpy = Mockito.spy(MovieRecord.class);
    PageMoviesJson mockPage = Mockito.spy(PageMoviesJson.class);

    @Test
    public void getMoviesFromPage() {
    }
}