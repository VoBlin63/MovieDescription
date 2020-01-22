package ru.buryachenko.moviedescription.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.database.MovieRecord;
import ru.buryachenko.moviedescription.utilities.AlarmReceiver;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.viemodel.MoviesViewModel;

import static ru.buryachenko.moviedescription.Constant.EMPTY_MOVIE_ID;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_MAIN_LIST;

public class DetailFragment extends Fragment {
    private MoviesViewModel viewModel;
    private View layout;
    private MovieRecord movie;
    private AlarmReceiver alarmReceiver;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDetailRemain:
                setRemain();
                break;
            case R.id.menuDetailShare:
                shareMovie(movie.getTitle(), movie.getOriginalTitle());
                break;
            case R.id.menuDetailGoMain:
                viewModel.setMode(MoviesViewModel.ModeView.MAIN_LIST);
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                ((MainActivity) getActivity()).callFragment(FRAGMENT_MAIN_LIST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setRemain() {
        final Dialog dialog = new Dialog(getActivity());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 15);
        dialog.setContentView(R.layout.dialog_remain);
        dialog.findViewById(R.id.dialogRemainCancel).setOnClickListener(view -> dialog.dismiss());
        TextView timePrompt = dialog.findViewById(R.id.dialogRemainTitle);
        SeekBar hours = dialog.findViewById(R.id.dialogRemainHours);
        hours.setProgress(calendar.get(Calendar.HOUR_OF_DAY));
        Button saveButton = dialog.findViewById(R.id.dialogRemainSet);
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                calendar.set(Calendar.HOUR_OF_DAY, hours.getProgress());
                setPromptTime(calendar, timePrompt, saveButton);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        hours.setOnSeekBarChangeListener(listener);
        setPromptTime(calendar, timePrompt, saveButton);
        ((CalendarView) dialog.findViewById(R.id.dialogRemainCalendar)).setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            setPromptTime(calendar, timePrompt, saveButton);
        });
        saveButton.setOnClickListener(view ->
        {
            alarmReceiver.setAlarm(calendar, getString(R.string.dialogRemainAlertText) + " " + movie.getTitle(), movie.getId());
            dialog.dismiss();
        });
        dialog.show();
    }

    private void setPromptTime(Calendar time, TextView timePrompt, Button saveButton) {
        SimpleDateFormat remainSdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String timeRemain = remainSdf.format(time.getTime());
        timePrompt.setText(timeRemain);
        saveButton.setEnabled(isFuture(time));
    }

    private boolean isFuture(Calendar calendar) {
        Calendar current = Calendar.getInstance();
        return current.getTimeInMillis() < calendar.getTimeInMillis();
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
        alarmReceiver = new AlarmReceiver();
        layout = view;
        movie = viewModel.getMovieById(viewModel.getIdForOpenDetail());
        if (movie != null) {
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
            ImageView imageLiked = layout.findViewById(R.id.detailLiked);
            imageLiked.setVisibility(movie.isLiked() ? View.VISIBLE : View.GONE);

            imageBackdrop.setOnClickListener(item -> {
                viewModel.turnLiked(movie);
                imageLiked.setVisibility(movie.isLiked() ? View.VISIBLE : View.GONE);
            });

            ((TextView) layout.findViewById(R.id.detailTitle)).setText(movie.getTitle());
            ((TextView) layout.findViewById(R.id.detailOverview)).setText(movie.getOverview());

            ((TextView) layout.findViewById(R.id.detailPopularity)).setText(movie.getPopularityTransformed());
            ((TextView) layout.findViewById(R.id.detailReleaseDate)).setText(movie.getReleaseDateTransformed());
            viewModel.setIdForOpen(EMPTY_MOVIE_ID);
        }
    }
}
