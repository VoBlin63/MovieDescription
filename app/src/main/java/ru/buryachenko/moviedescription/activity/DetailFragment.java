package ru.buryachenko.moviedescription.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.database.MovieRecord;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.viemodel.MoviesViewModel;

import static ru.buryachenko.moviedescription.Constant.FRAGMENT_MAIN_LIST;

public class DetailFragment extends Fragment {
    private MoviesViewModel viewModel;
    private View layout;
    private MovieRecord movie;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        ((MainActivity) getActivity()).hideSearchField();
        ((MainActivity) getActivity()).setTitle(getString(R.string.titleFragmentDetail));
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDetailShare:
                //TODO ссылку куда дать ?
                shareMovie(movie.getTitle(), movie.getOriginalTitle());
                break;
            case R.id.menuDetailGoMain:
                viewModel.setMode(MoviesViewModel.ModeView.MAIN_LIST);
                ((MainActivity) getActivity()).callFragment(FRAGMENT_MAIN_LIST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareMovie(String title, String link) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.shareMoviePrompt) + " " + title));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(MoviesViewModel.class);
        layout = view;

//+        private Integer id;
//+        private Double popularity;
//-        private Integer voteCount;
//-        private String posterPath;
//        private Boolean adult;
//        private String originalLanguage;
//        private String originalTitle;
//+        private String title;
//-        private Float voteAverage;
//+        private String overview;
//+        private String getReleaseDateTransformed;
//+        private String backdropPath;
//        private boolean liked;


        if (viewModel.getIndexForOpenDetail() != -1) {
            movie = viewModel.getListMovies()[viewModel.getIndexForOpenDetail()];
            ImageView imageBackdrop = layout.findViewById(R.id.detailBackdrop);
            Point size = ((MainActivity) getActivity()).getScreenSize();
            Glide.with(this)
                    .load(movie.getBackdropPath())
                    .fitCenter()
                    .placeholder(R.drawable.ic_loading_poster)
                    .error(R.drawable.ic_poster_blank)
                    .into(imageBackdrop);
            ViewGroup.LayoutParams params = imageBackdrop.getLayoutParams();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                params.width = Math.round(size.x - getResources().getDimension(R.dimen.horizontal_margin) * 2);
            } else {
                params.width = size.x / 2;
            }
            params.height = 9 * params.width / 16;
            imageBackdrop.setLayoutParams(params);

            ((TextView) layout.findViewById(R.id.detailTitle)).setText(movie.getTitle());
            ((TextView) layout.findViewById(R.id.detailOverview)).setText(movie.getOverview());
//            ((TextView) layout.findViewById(R.id.detailOverview)).setMovementMethod(new ScrollingMovementMethod());

            ((TextView) layout.findViewById(R.id.detailPopularity)).setText(movie.getPopularityTransformed());
            ((TextView) layout.findViewById(R.id.detailReleaseDate)).setText(movie.getReleaseDateTransformed());

            viewModel.setIndexForOpen(-1);
        } else {
            ((TextView) layout.findViewById(R.id.detailTitle)).setText("empty");
            AppLog.write("No movie for open");
        }
    }
}
