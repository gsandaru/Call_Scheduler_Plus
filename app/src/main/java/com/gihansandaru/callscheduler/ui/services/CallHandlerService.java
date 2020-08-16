package com.gihansandaru.callscheduler.ui.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.gihansandaru.callscheduler.SchedulerActivity;

public class CallHandlerService extends Service {

    private static final String TAG = "CallHandlerService";

    private TelephonyManager telephony;
    private PhonecallStartEndDetector customPhoneListener;


    public CallHandlerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "In onCreate");
        Toast.makeText(this, "Service In onCreate.", Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        telephony = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        customPhoneListener = new PhonecallStartEndDetector(getApplicationContext());
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        Log.i(TAG, "In onStartCommand");
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        Toast.makeText(this, "Service In onTaskRemoved.", Toast.LENGTH_LONG).show();
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "com.gihansandaru.callscheduler";
        String channelName = "Call Scheduler Service ";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        Intent intent = new Intent(getApplicationContext(), SchedulerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 2020, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Call Scheduler is Running on the Background")
                .setContentText("Tap to manually schedule a tentative meeting")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(2, notification);
    }
}
