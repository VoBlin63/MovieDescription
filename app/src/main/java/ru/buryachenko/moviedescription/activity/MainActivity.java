package ru.buryachenko.moviedescription.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

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
import androidx.lifecycle.ViewModelProviders;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.viemodel.MoviesViewModel;

import static ru.buryachenko.moviedescription.Constant.FRAGMENT_ABOUT;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_CONFIG;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_DETAIL;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_FAQ;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_MAIN_LIST;

public class MainActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    private MoviesViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        viewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        viewModel.init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.drawerOpen, R.string.drawerClose);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> clickDrawerMenu(item.getItemId()));

        callFragment(FRAGMENT_MAIN_LIST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchView mSearchView = findViewById(R.id.searchItem);
        mSearchView.setQueryHint(getString(R.string.searchHint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
            case R.id.menuFaq:
                callFragment(FRAGMENT_FAQ);
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

    public static void callFragment(String screenTag) {
        Fragment toCall = fragmentManager.findFragmentByTag(screenTag);
        if (toCall != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, toCall, screenTag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            return;
        }
        switch (screenTag) {
            case FRAGMENT_CONFIG:
                toCall = new ConfigFragment();
                break;
            case FRAGMENT_MAIN_LIST:
                toCall = new MainListFragment();
                break;
            case FRAGMENT_DETAIL:
                toCall = new DetailFragment();
                break;
            case FRAGMENT_FAQ:
                toCall = new FaqFragment();
                break;
            case FRAGMENT_ABOUT:
                toCall = new AboutFragment();
                break;
            default:
                toCall = null;
        }
        if (toCall != null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragmentContainer, toCall, screenTag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
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
}
