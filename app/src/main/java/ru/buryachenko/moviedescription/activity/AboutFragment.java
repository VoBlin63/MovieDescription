package ru.buryachenko.moviedescription.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.buryachenko.moviedescription.R;

import static ru.buryachenko.moviedescription.Constant.FRAGMENT_MAIN_LIST;

public class AboutFragment extends Fragment {
    private View layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        ((MainActivity) getActivity()).hideSearchField();
        ((MainActivity) getActivity()).setTitle(getString(R.string.menu_aboutApp));
        inflater.inflate(R.menu.menu_about, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAboutGoMain:
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                ((MainActivity) getActivity()).callFragment(FRAGMENT_MAIN_LIST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout = view;
        String htmlAsString = getString(R.string.aboutHtml);
        Spanned htmlAsSpanned = Html.fromHtml(htmlAsString);
        TextView container = layout.findViewById(R.id.aboutContainer);
        container.setText(htmlAsSpanned);
        container.setMovementMethod(new ScrollingMovementMethod());
    }

}
