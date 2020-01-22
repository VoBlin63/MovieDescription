package ru.buryachenko.moviedescription.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.RequiresApi;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.R;

import static ru.buryachenko.moviedescription.Constant.ALARM_KEY_MOVIE_ACTION;
import static ru.buryachenko.moviedescription.Constant.ALARM_KEY_MOVIE_ID;
import static ru.buryachenko.moviedescription.Constant.ALARM_KEY_MOVIE_TEXT;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras= intent.getExtras();
        if (extras != null) {
            String text = extras.getString(ALARM_KEY_MOVIE_TEXT,"");
            int movieId = extras.getInt(ALARM_KEY_MOVIE_ID,-1);
            if (movieId >= 0 && !text.isEmpty()) {
                FilmNotification.pushMessageAlarm(context.getString(R.string.dialogRemainAlertTitle), text, movieId);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setAlarm(Calendar alarmTime, String text, int movieId) {
        Intent intent = new Intent(App.getInstance(), AlarmReceiver.class);
        intent.setAction(ALARM_KEY_MOVIE_ACTION);
        intent.putExtra(ALARM_KEY_MOVIE_TEXT, text);
        intent.putExtra(ALARM_KEY_MOVIE_ID, movieId);
        SimpleDateFormat wholeSdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        AppLog.write("  Set alarm on movieId = " + movieId + " at " + wholeSdf.format(alarmTime.getTime()));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(App.getInstance(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager)App.getInstance().getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            manager.set(AlarmManager.RTC_WAKEUP,alarmTime.getTimeInMillis() ,pendingIntent);
        } else {
            AppLog.write("AlarmManager is null!");
        }
    }

}
