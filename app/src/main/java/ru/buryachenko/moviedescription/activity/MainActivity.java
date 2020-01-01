package ru.buryachenko.moviedescription.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.viemodel.MoviesViewModel;

import static ru.buryachenko.moviedescription.Constant.FRAGMENT_CONFIG;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_DETAIL;
import static ru.buryachenko.moviedescription.Constant.FRAGMENT_MAIN_LIST;

public class MainActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        MoviesViewModel viewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        viewModel.init();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> clickDrawerMenu(item.getItemId()));

        callFragment(FRAGMENT_MAIN_LIST);
        //callFragment(FRAGMENT_CONFIG);

//        EditText filter = findViewById(R.id.filter);
//        findViewById(R.id.button).setOnClickListener(view -> {
//            viewModel.setFilter(filter.getText().toString(), true);
//        });
//        androidx.lifecycle.LiveData<Boolean> isListReady = viewModel.getListReady();
//        isListReady.observe(this, status -> {
//            if (status) {
//                AppLog.write("List is ready:");
//                for (int i = 0; i < viewModel.getListMovies().length; i++) {
//                    AppLog.write(viewModel.getListMovies()[i].getId() + " [" + viewModel.getListMovies()[i].getUsefulness() + "] " + viewModel.getListMovies()[i].getTitle());
//                }
//            } else {
//                AppLog.write("List is reforming...");
//            }
//        });

    }

    private boolean clickDrawerMenu(int idMenuItem) {
        switch (idMenuItem) {
            case R.id.menuAboutApp:
                break;
            case R.id.menuFaq:
                break;
            case R.id.menuLikedList:
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
            super.onBackPressed();
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
                        System.exit(0);
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
