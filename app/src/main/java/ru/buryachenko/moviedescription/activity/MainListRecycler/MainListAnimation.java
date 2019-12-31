package ru.buryachenko.moviedescription.activity.MainListRecycler;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

public class MainListAnimation {

    private final  static long DURATION_PRESS = 100;

    public static void press(View view) {
        float from = 1F;
        float to = 0.91F;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, from, to);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, from, to);
        AnimatorSet setForward = new AnimatorSet();
        setForward.playTogether(scaleX, scaleY);
        setForward.setDuration(DURATION_PRESS/2);
        ObjectAnimator scaleXBack = ObjectAnimator.ofFloat(view, View.SCALE_X, to, from);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(view, View.SCALE_Y, to, from);
        AnimatorSet setBack = new AnimatorSet();
        setBack.playTogether(scaleXBack, scaleYBack);
        setBack.setDuration(DURATION_PRESS);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(setForward, setBack);
        set.start();
    }
}
