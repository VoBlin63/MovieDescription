package ru.buryachenko.moviedescription.utilities;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.activity.MainActivity;

import static ru.buryachenko.moviedescription.Constant.ALARM_KEY_MOVIE_ID;
import static ru.buryachenko.moviedescription.Constant.ALARM_KEY_MOVIE_TEXT;
import static ru.buryachenko.moviedescription.Constant.NOTIFICATION_CHANNEL_ID;

public class FilmNotification {
    private static int id = 0;

    public static void pushMessageAlarm(String title, String text, int movieId) {
        Intent intent = new Intent(App.getInstance(), MainActivity.class);
        intent.putExtra(ALARM_KEY_MOVIE_TEXT, text);
        intent.putExtra(ALARM_KEY_MOVIE_ID, movieId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.getInstance());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        pushMessage(title, text, R.drawable.ic_remain, resultPendingIntent);
    }

    public static void pushMessageUpdateFinished(String title, String text) {
        Intent intent = new Intent(App.getInstance(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(App.getInstance(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pushMessage(title, text, R.drawable.ic_finished, pendingIntent);
    }

    private static void pushMessage(String title, String text, int iconId, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getInstance(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(iconId)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getInstance());
        notificationManager.notify(id++, builder.build());
    }
}
