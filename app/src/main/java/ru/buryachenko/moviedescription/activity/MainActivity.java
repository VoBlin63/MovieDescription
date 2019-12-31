package ru.buryachenko.moviedescription.activity;

import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
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
            case  FRAGMENT_DETAIL:
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

}
