package ru.buryachenko.moviedescription.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.utilities.Config;

import static ru.buryachenko.moviedescription.Constant.FRAGMENT_MAIN_LIST;

public class ConfigFragment extends Fragment {
    private View layout;
    private Config config = Config.getInstance();

    private CheckBox isPerfectFilterOnly;
    private CheckBox isUseOverview;
    private SeekBar sleepSecondsBetweenLoadPages;
    private RadioButton wifi;
    private RadioButton all;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout = view;

        isPerfectFilterOnly = layout.findViewById(R.id.configIsPerfectFilterOnly);
        isPerfectFilterOnly.setChecked(config.isShowOnlyFitFilter());

        isUseOverview = layout.findViewById(R.id.configUseOverview);
        isUseOverview.setChecked(config.isUseOverview());

        sleepSecondsBetweenLoadPages = layout.findViewById(R.id.configSleepSecondsBetweenLoadPages);
        setPromptSleepSecondsBetweenLoadPages(sleepSecondsBetweenLoadPages.getProgress());
        sleepSecondsBetweenLoadPages.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setPromptSleepSecondsBetweenLoadPages(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sleepSecondsBetweenLoadPages.setProgress(config.getSleepSecondsBetweenLoadPages());

        RadioGroup connectionMode = layout.findViewById(R.id.configConnectionMode);
        connectionMode.clearCheck();
        wifi = layout.findViewById(R.id.configConnectionModeWiFi);
        all = layout.findViewById(R.id.configConnectionModeAll);
        wifi.setChecked(config.isUseOnlyWiFi());
        all.setChecked(!config.isUseOnlyWiFi());

//        connectionMode.setOnCheckedChangeListener((group, checkedId) -> {
//            switch (checkedId) {
//                case -1:
//                    break;
//                case R.id.configConnectionModeWiFi:
//                    AppLog.write("set mode WiFi");
//                    break;
//                case R.id.configConnectionModeAll:
//                    AppLog.write("set mode All");
//                    break;
//            }
//        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        ((MainActivity) getActivity()).hideSearchField();
        ((MainActivity) getActivity()).setTitle(getString(R.string.menu_config));
        inflater.inflate(R.menu.menu_config, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuConfigGoMain:
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                ((MainActivity) getActivity()).callFragment(FRAGMENT_MAIN_LIST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        config.setShowOnlyFitFilter(isPerfectFilterOnly.isChecked());
        config.setSleepSecondsBetweenLoadPages(sleepSecondsBetweenLoadPages.getProgress());
        config.setUseOnlyWiFi(wifi.isChecked());
        config.setUseOverview(isUseOverview.isChecked());
        config.save();
    }

    private void setPromptSleepSecondsBetweenLoadPages(int value) {
        TextView promptSleepSecondsBetweenLoadPages = layout.findViewById(R.id.configPromptSleepSecondsBetweenLoadPages);
        promptSleepSecondsBetweenLoadPages.setText(getString(R.string.configSleepSecondsBetweenLoadPages1)
                + value + getString(R.string.configSleepSecondsBetweenLoadPages2));
    }
}
