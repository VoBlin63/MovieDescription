package ru.buryachenko.moviedescription.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.activity.MainListRecycler.MainListAdapter;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.viemodel.MoviesViewModel;

import static ru.buryachenko.moviedescription.Constant.FRAGMENT_LIKED;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_MAIN_LIST;

public class MainListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private MoviesViewModel viewModel;
    private View layout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresher;
    private int spanCountWidth;
    private int cellWidth;
    private int cellHeight;
    private MainListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
        swipeRefresher = layout.findViewById(R.id.mainListSwipeRefresh);
        swipeRefresher.setOnRefreshListener(this);
        swipeRefresher.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        viewModel.getChangedItem().observe(this, index-> adapter.notifyItemChanged(index));
        viewModel.getListReady().observe(this, status -> {
            if (status) {
                getActivity().invalidateOptionsMenu();
                viewModel.resetList();
                adapter = new MainListAdapter(LayoutInflater.from(layout.getContext()), viewModel, cellWidth, cellHeight, (MainActivity) getActivity());
                recyclerView.setAdapter(adapter);
            }
        });

        UUID updater = App.getInstance().setUpUpdateDatabase(false);
        if (updater != null) {
            swipeRefresher.setRefreshing(true);
            setUpBusyStatus(updater);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        ((MainActivity) getActivity()).showSearchField();
        if (viewModel.getMode() == MoviesViewModel.ModeView.LIKED_LIST) {
            ((MainActivity) getActivity()).setTitle(getString(R.string.titleFragmentLiked));
            inflater.inflate(R.menu.menu_liked, menu);
        } else {
            ((MainActivity) getActivity()).setTitle(getString(R.string.menu_mainScreen));
            inflater.inflate(R.menu.menu_main_list, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLikedGoMain:
                viewModel.setMode(MoviesViewModel.ModeView.MAIN_LIST);
                ((MainActivity) getActivity()).callFragment(FRAGMENT_MAIN_LIST);
                break;
            case R.id.menuMainListGoLiked:
                viewModel.setMode(MoviesViewModel.ModeView.LIKED_LIST);
                ((MainActivity) getActivity()).callFragment(FRAGMENT_LIKED);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setUpBusyStatus(UUID updater) {
        WorkManager.getInstance().getWorkInfoByIdLiveData(updater).observe(this, workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                swipeRefresher.setRefreshing(false);
            } else {
                AppLog.write("UpdateDatabase finished status : " + workInfo.getState());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.pushLiked(false);
    }

    @Override
    public void onRefresh() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        DialogInterface.OnClickListener listener =
                (dialog, which) -> {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        viewModel.pushLiked(false);
                        UUID updater = App.getInstance().setUpUpdateDatabase(true);
                        setUpBusyStatus(updater);
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

    private void setSpanCountsAndSizes() {
        int spanCountHeight;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCountWidth = 3;
            spanCountHeight = 4;
        } else {
            spanCountWidth = 5;
            spanCountHeight = 2;
        }
        int shiftX = Math.round(
                (getResources().getDimension(R.dimen.horizontal_margin) * 2 + getResources().getDimension(R.dimen.cell_border_space) * 2 * spanCountWidth)
        );
        int shiftY = Math.round(
                (getResources().getDimension(R.dimen.horizontal_margin) * 2 + getResources().getDimension(R.dimen.cell_border_space) * 2 * spanCountHeight)
        );
        Point size = ((MainActivity) getActivity()).getScreenSize();
        cellWidth = (size.x - shiftX) / spanCountWidth;
        cellHeight = (size.y - shiftY) / spanCountHeight;
    }
}
