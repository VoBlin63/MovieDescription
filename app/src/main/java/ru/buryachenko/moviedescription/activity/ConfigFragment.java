package ru.buryachenko.moviedescription.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.utilities.AppLog;

public class ConfigFragment extends Fragment {
    private View layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout = view;

        SeekBar sleepSecondsBetweenLoadPages = layout.findViewById(R.id.configSleepSecondsBetweenLoadPages);
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

        RadioGroup connectionMode = layout.findViewById(R.id.configConnectionMode);
        connectionMode.clearCheck();
        RadioButton wifi = layout.findViewById(R.id.configConnectionModeWiFi);
        wifi.setChecked(true);

        connectionMode.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case -1:
                    break;
                case R.id.configConnectionModeWiFi:
                    AppLog.write("set mode WiFi");
                    break;
                case R.id.configConnectionModeAll:
                    AppLog.write("set mode All");
                    break;
            }
        });
    }

    private void setPromptSleepSecondsBetweenLoadPages(int value) {
        TextView promptSleepSecondsBetweenLoadPages = layout.findViewById(R.id.configPromptSleepSecondsBetweenLoadPages);
        promptSleepSecondsBetweenLoadPages.setText(getString(R.string.configSleepSecondsBetweenLoadPages1)
                + value + getString(R.string.configSleepSecondsBetweenLoadPages2));
    }
}
