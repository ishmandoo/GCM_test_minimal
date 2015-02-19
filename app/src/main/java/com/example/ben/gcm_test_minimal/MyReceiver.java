package com.example.ben.gcm_test_minimal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Created by ben on 2/14/15.
 */
public class MyReceiver extends BroadcastReceiver {
    public static final String TAG = MyReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // assumes WordService is a registered service

        String friendName = intent.getStringExtra("friendName");

        Log.i(TAG, "Fuck you received from " + intent.getStringExtra("friendName"));

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setVibrate(new long[] {0, 300, 200, 300, 10000, 10000})
                        .setContentTitle(friendName + " says fuck you!")
                        .setContentText(friendName + " says fuck you!");


        int mNotificationId = 001;

        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);


        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());





        Intent loginIntent = new Intent("com.example.ben.gcm_test_minimal.updateMain");
        context.sendBroadcast(loginIntent);
    }
}