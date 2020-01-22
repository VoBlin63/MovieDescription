package ru.buryachenko.moviedescription.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.activity.MainListRecycler.MainListAdapter;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.viemodel.MoviesViewModel;

import static ru.buryachenko.moviedescription.Constant.ALARM_KEY_MOVIE_ID;
import static ru.buryachenko.moviedescription.Constant.ALARM_KEY_MOVIE_TEXT;
import static ru.buryachenko.moviedescription.Constant.EMPTY_MOVIE_ID;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_ABOUT;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_CONFIG;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_DETAIL;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_MAIN_LIST;

public class MainActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    private MoviesViewModel viewModel;
    private Toolbar toolbar;
    private SearchView search;

    private static Map<String, Fragment> fragments = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        viewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        viewModel.init();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.drawerOpen, R.string.drawerClose);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> clickDrawerMenu(item.getItemId()));

        App.getInstance().setUpUpdateDatabase(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            acceptMovie(extras);
        }
    }

    private void acceptMovie(Bundle data) {
        String text = data.getString(ALARM_KEY_MOVIE_TEXT,"");
        int movieId = data.getInt(ALARM_KEY_MOVIE_ID, EMPTY_MOVIE_ID);
        viewModel.setIdForOpen(movieId);
        AppLog.write("Got movieId for open : " + movieId);
        if (!text.isEmpty()) {
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragments.clear();
        fragments.put(FRAGMENT_ABOUT, new AboutFragment());
        fragments.put(FRAGMENT_CONFIG, new ConfigFragment());
        fragments.put(FRAGMENT_MAIN_LIST, new MainListFragment());
        fragments.put(FRAGMENT_DETAIL, new DetailFragment());
        callFragment(FRAGMENT_MAIN_LIST);
    }

    public void hideSearchField() {
        search.setVisibility(View.GONE);
    }

    public void showSearchField() {
        search.setVisibility(View.VISIBLE);
    }

    public Point getScreenSize() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        search = findViewById(R.id.searchItem);
        search.setQueryHint(getString(R.string.searchHint));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setFilter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private boolean clickDrawerMenu(int idMenuItem) {
        switch (idMenuItem) {
            case R.id.menuAboutApp:
                callFragment(FRAGMENT_ABOUT);
                break;
            case R.id.menuLikedList:
                viewModel.setMode(MoviesViewModel.ModeView.LIKED_LIST);
                callFragment(FRAGMENT_MAIN_LIST);
                break;
            case R.id.menuMainScreen:
                viewModel.setMode(MoviesViewModel.ModeView.MAIN_LIST);
                callFragment(FRAGMENT_MAIN_LIST);
                break;
            case R.id.menuQuit:
                tryToQuit();
                break;
            case R.id.menuConfig:
                callFragment(FRAGMENT_CONFIG);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                super.onBackPressed();
            } else {
                tryToQuit();
            }
        }
    }

    public void callFragment(String screenTag) {
        Fragment toCall = fragmentManager.findFragmentByTag(screenTag);
        if (toCall != null) {
            fragmentManager
                    .beginTransaction()
                    .remove(toCall)
                    .add(R.id.fragmentContainer, toCall, screenTag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else {
            toCall = fragments.get(screenTag);
            if (toCall != null) {
                fragmentManager
                        .beginTransaction()
                        .add(R.id.fragmentContainer, toCall, screenTag)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public void tryToQuit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogInterface.OnClickListener listener =
                (dialog, which) -> {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        viewModel.pushLiked(true);
                    }
                    dialog.dismiss();
                };
        builder.setMessage(getResources().getString(R.string.mainActivityAskToExit));
        builder.setNegativeButton(getResources().getString(R.string.mainActivityAskToExitYes), listener);
        builder.setPositiveButton(getResources().getString(R.string.mainActivityAskToExitCancel), listener);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }
}
