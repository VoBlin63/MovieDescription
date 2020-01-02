package ru.buryachenko.moviedescription.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.database.MovieRecord;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.viemodel.MoviesViewModel;

public class DetailFragment extends Fragment {
    private MoviesViewModel viewModel;
    private View layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(MoviesViewModel.class);
        layout = view;
        if (viewModel.getIndexForOpen() != -1) {
            MovieRecord movie = viewModel.getListMovies()[viewModel.getIndexForOpen()];
            AppLog.write("Will work with " + movie.getTitle());
            ((TextView)layout.findViewById(R.id.detailTitle)).setText(movie.getTitle());
            ((TextView)layout.findViewById(R.id.detailOverview)).setText(movie.getOverview());
            viewModel.setIndexForOpen(-1);
        } else {
            ((TextView)layout.findViewById(R.id.detailTitle)).setText("empty");
            AppLog.write("No movie for open");
        }
    }
}
