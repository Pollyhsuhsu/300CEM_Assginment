package com.example.a300cem_android_assignment.Wareable;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.a300cem_android_assignment.Login;
import com.example.a300cem_android_assignment.R;

public class CountingService extends IntentService {

    public static final String REPORT_KEY = "REPORT_KEY";
    public static final String INTENT_KEY = "com.example.mywearables.BROADCAST";

    public CountingService() {
        super("BackgroundCounting");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int count = 0;
        while (count < 10) {
            synchronized (this) {
                try {
                    wait(1000);
                    count++;
                    Log.d(Login.DEBUG_KEY, Integer.toString(count));
                    String CHANNEL_ID = "my_channel_01";
                    CharSequence name = "Channel human readable title";// The user-visible name of the channel.
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel channel = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        channel = new NotificationChannel(CHANNEL_ID, name,
                                importance);
                    }
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationManager.createNotificationChannel(channel);
                    }

                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(this, CHANNEL_ID)
                                    .setSmallIcon(R.mipmap.ic_corgi_foreground)
                                    .setContentTitle("My Wearables")
                                    .setContentText("Time elapsed: " + Integer.toString(count) + " seconds.");
                    Intent resultIntent = new Intent(this, Login.class);
                    resultIntent.putExtra(REPORT_KEY, Integer.toString(count));

                    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
                    taskStackBuilder.addParentStack(Login.class);
                    taskStackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(resultPendingIntent);

                    notificationManager.notify(123123, builder.build());
                } catch (Exception e) {
                }
            }
        }
        Log.d(Login.DEBUG_KEY, "service finished");

    }
}
