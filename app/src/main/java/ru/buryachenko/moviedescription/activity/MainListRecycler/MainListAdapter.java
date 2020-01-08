package ru.buryachenko.moviedescription.activity.MainListRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.activity.MainActivity;
import ru.buryachenko.moviedescription.database.MovieRecord;
import ru.buryachenko.moviedescription.viemodel.MoviesViewModel;

import static ru.buryachenko.moviedescription.Constant.FRAGMENT_DETAIL;

public class MainListAdapter extends RecyclerView.Adapter<MainListHolder> {

    private MovieRecord[] moviesList;
    private MoviesViewModel viewModel;
    private int cellWidth;
    private int cellHeight;
    private MainActivity activity;

    public MainListAdapter(LayoutInflater inflater, MoviesViewModel viewModel, int cellWidth, int cellHeight, MainActivity activity) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.viewModel = viewModel;
        this.moviesList = viewModel.getListMovies();
        this.activity = activity;
    }

    @NonNull
    @Override
    public MainListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout filmRow = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_mainlist_item_movie, parent, false);
        MainListHolder res = new MainListHolder(filmRow, cellWidth, cellHeight);
        filmRow.findViewById(R.id.mainListItemPicture).setOnClickListener(view -> openMovie(view, res.getAdapterPosition()));
        filmRow.findViewById(R.id.mainListItemPicture).setOnLongClickListener(view -> turnLiked(view, res.getAdapterPosition()));
        return res;
    }

    @Override
    public void onBindViewHolder(@NonNull MainListHolder holder, int position) {
        holder.bind(moviesList[position]);
    }

    @Override
    public int getItemCount() {
        return moviesList.length;
    }

    private void openMovie(View view, int adapterPosition) {
        MainListAnimation.press(view);
        viewModel.setIndexForOpen(adapterPosition);
        activity.callFragment(FRAGMENT_DETAIL);
    }

    private boolean turnLiked(View view, int adapterPosition) {
        MainListAnimation.press(view);
        viewModel.turnLiked(adapterPosition);
        notifyItemChanged(adapterPosition);
        return true;
    }

}
