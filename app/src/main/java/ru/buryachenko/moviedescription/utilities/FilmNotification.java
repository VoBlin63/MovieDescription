package ru.buryachenko.moviedescription.utilities;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.activity.MainActivity;

import static ru.buryachenko.moviedescription.Constant.NOTIFICATION_CHANNEL_ID;

public class FilmNotification {
    private static int id = 0;

    public static void pushMessage(String title, String text) {
        Intent intent = new Intent(App.getInstance(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(App.getInstance(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getInstance(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_finished)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getInstance());
        notificationManager.notify(id++, builder.build());
    }
}
