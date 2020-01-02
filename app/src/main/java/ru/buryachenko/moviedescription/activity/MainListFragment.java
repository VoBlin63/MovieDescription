package ru.buryachenko.moviedescription.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.activity.MainListRecycler.MainListAdapter;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.utilities.Config;
import ru.buryachenko.moviedescription.viemodel.MoviesViewModel;

public class MainListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private MoviesViewModel viewModel;
    private View layout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresher;
    private int spanCountWidth;
    private int spanCountHeight;
    private int cellWidth;
    private int cellHeight;
    private Config config = Config.getInstance();
    MainListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mainlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(MoviesViewModel.class);
        layout = view;

        recyclerView = layout.findViewById(R.id.mainListRecycler);
        setSpanCountsAndSizes();
        final GridLayoutManager layoutManager = new GridLayoutManager(layout.getContext(), spanCountWidth);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MainListAdapter(LayoutInflater.from(layout.getContext()), viewModel, cellWidth, cellHeight);
        recyclerView.setAdapter(adapter);

//        LiveData<FilmInApp> changedFilm = viewModel.getChangedFilm();
//        changedFilm.observe(this, film -> notifyChanges(adapter, film));

        swipeRefresher = layout.findViewById(R.id.mainListSwipeRefresh);
        swipeRefresher.setOnRefreshListener(this);
        swipeRefresher.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        //serviceUpdateStatus = ServiceDb.getStatus();
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                if (true) {
//                if (STATUS_SERVICE_BUSY.equals(s)) {
                    swipeRefresher.setRefreshing(true);
                } else {
                    swipeRefresher.setRefreshing(false);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        viewModel.getListReady().observe(this, status -> {
            AppLog.write("Got list ready: " + status);
            if (status) {
                adapter = new MainListAdapter(LayoutInflater.from(layout.getContext()), viewModel, cellWidth, cellHeight);
                recyclerView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
            }
        });

        //serviceUpdateStatus.subscribe(observer);

    }

    @Override
    public void onRefresh() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        DialogInterface.OnClickListener listener =
                (dialog, which) -> {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        //callDbUpdateService();
                        App.getInstance().setUpUpdateDatabase();
                    } else {
                        swipeRefresher.setRefreshing(false);
                    }
                    dialog.dismiss();
                };
        builder.setMessage(getActivity().getString(R.string.mainListAskUpdateDb));
        builder.setNegativeButton(getActivity().getString(R.string.mainListUpdateCancel), listener);
        builder.setPositiveButton(getActivity().getString(R.string.mainListUpdateYes), listener);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private Point getScreenSize() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private void setSpanCountsAndSizes() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCountWidth = 3;
            spanCountHeight = 4;
        } else {
            spanCountWidth = 5;
            spanCountHeight = 2;
        }
        int shiftX = Math.round(
                (getResources().getDimension(R.dimen.horizontal_margin)*2 + getResources().getDimension(R.dimen.cell_border_space)*2*spanCountWidth)
        );
        int shiftY = Math.round(
                (getResources().getDimension(R.dimen.horizontal_margin)*2 + getResources().getDimension(R.dimen.cell_border_space)*2*spanCountHeight)
        );
        Point size = getScreenSize();
        cellWidth = (size.x - shiftX) / spanCountWidth;
        cellHeight = (size.y - shiftY) / spanCountHeight;
    }

}
