package ru.buryachenko.moviedescription.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
            ImageView imageBack = layout.findViewById(R.id.detailBack);
            Point size = getScreenSize();
            Glide.with(this)
                    .load(movie.getBackdropPath())
                    .fitCenter()
                    .placeholder(R.drawable.ic_loading_poster)
                    .error(R.drawable.ic_poster_blank)
                    .into(imageBack);
            ViewGroup.LayoutParams params = imageBack.getLayoutParams();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                params.width = Math.round(size.x - getResources().getDimension(R.dimen.horizontal_margin) * 2);
            } else {
                params.width = size.x / 2;
            }
            params.height = 9 * params.width / 16;
            imageBack.setLayoutParams(params);

            ((TextView) layout.findViewById(R.id.detailTitle)).setText(movie.getTitle());
            ((TextView) layout.findViewById(R.id.detailOverview)).setText(movie.getOverview());
            viewModel.setIndexForOpen(-1);
        } else {
            ((TextView) layout.findViewById(R.id.detailTitle)).setText("empty");
            AppLog.write("No movie for open");
        }
    }

    private Point getScreenSize() {
        //TODO дублирует - как бы их в одно место
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
}
