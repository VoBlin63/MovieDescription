package ru.buryachenko.moviedescription.activity.MainListRecycler;

import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.activity.MainActivity;
import ru.buryachenko.moviedescription.viemodel.MoviesViewModel;

import static ru.buryachenko.moviedescription.Constant.FRAGMENT_DETAIL;

public class MainListAdapter extends RecyclerView.Adapter<MainListHolder> {

    private MoviesViewModel viewModel;
    private int cellWidth;
    private int cellHeight;

    public MainListAdapter(LayoutInflater inflater, MoviesViewModel viewModel, int cellWidth, int cellHeight) {
        this.viewModel = viewModel;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    @NonNull
    @Override
    public MainListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout filmRow = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_mainlist_item_movie, parent, false);
        MainListHolder res = new MainListHolder(filmRow, cellWidth, cellHeight);
        filmRow.findViewById(R.id.mainListItemPicture).setOnClickListener(view -> openMovie(view, res.getAdapterPosition()));
        return res;
    }

    @Override
    public void onBindViewHolder(@NonNull MainListHolder holder, int position) {
        holder.bind(viewModel.getListMovies()[position]);
    }

    @Override
    public int getItemCount() {
        return viewModel.getListMovies().length;
    }

    private void openMovie(View view, int adapterPosition) {
        MainListAnimation.press(view);
        viewModel.setIndexForOpen(adapterPosition);
        MainActivity.callFragment(FRAGMENT_DETAIL);
    }

}
