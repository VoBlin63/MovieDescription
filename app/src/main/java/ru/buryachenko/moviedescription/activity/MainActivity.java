package ru.buryachenko.moviedescription.activity;

import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.utilities.ConvertibleTerms;
import ru.buryachenko.moviedescription.utilities.Metaphone;
import ru.buryachenko.moviedescription.viemodel.MoviesViewModel;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MoviesViewModel viewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        viewModel.init();

        AppLog.write(ConvertibleTerms.topWord(Metaphone.metaphone("драка")));

        EditText filter = findViewById(R.id.filter);
        findViewById(R.id.button).setOnClickListener(view -> {
            viewModel.setFilter(filter.getText().toString(), true);
        });
        androidx.lifecycle.LiveData<Boolean> isListReady = viewModel.getListReady();
        isListReady.observe(this, status -> {
            if (status) {
                AppLog.write("List is ready:");
                for (int i = 0; i < viewModel.getListMovies().length; i++) {
                    AppLog.write(viewModel.getListMovies()[i].getId() + " [" + viewModel.getListMovies()[i].getUsefulness() + "] " + viewModel.getListMovies()[i].getTitle());
                }
            } else {
                AppLog.write("List is reforming...");
            }
        });

    }

}
