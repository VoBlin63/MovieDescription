package ru.buryachenko.moviedescription.activity.MainListRecycler;

import android.graphics.Point;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.database.MovieRecord;

public class MainListHolder extends RecyclerView.ViewHolder {

    private View layout;
    private ImageView picture;
    private ImageView liked;

    public MainListHolder(@NonNull View itemView, int cellWidth, int cellHeight) {
        super(itemView);
        layout = itemView;
        picture = itemView.findViewById(R.id.mainListItemPicture);
        ConstraintLayout.LayoutParams pictureParams = (ConstraintLayout.LayoutParams) picture.getLayoutParams();
        pictureParams.width = cellWidth;
        pictureParams.height = cellHeight;
        picture.setLayoutParams(pictureParams);

        liked = itemView.findViewById(R.id.mainListItemLiked);
        ConstraintLayout.LayoutParams likedParams = (ConstraintLayout.LayoutParams) liked.getLayoutParams();
        likedParams.width = cellWidth / 7;
        likedParams.height = cellHeight / 9;
        liked.setLayoutParams(likedParams);
    }

    void bind(MovieRecord movie) {
        Glide.with(itemView.getContext())
                .load(movie.getPosterPath())
                .fitCenter()
                .placeholder(R.drawable.ic_loading_poster)
                .error(R.drawable.ic_poster_blank)
                .into(picture);
        liked.setVisibility(movie.isLiked()? View.VISIBLE : View.INVISIBLE);
    }

}
