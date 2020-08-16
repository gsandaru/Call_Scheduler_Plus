package com.gihansandaru.callscheduler.ui.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.gihansandaru.callscheduler.MainActivity;
import com.gihansandaru.callscheduler.R;
import com.gihansandaru.callscheduler.SchedulerActivity;

import java.util.Date;

public class PhonecallStartEndDetector extends PhoneStateListener {
    int lastState = TelephonyManager.CALL_STATE_IDLE;
    Date callStartTime;
    boolean isIncoming;
    String savedNumber;  //because the passed incoming is only valid in ringing
    private static final String TAG = "PhonecallStartEndDetect";
    private Context context;

    public PhonecallStartEndDetector(Context context) {
        this.context = context;
    }


    //The outgoing number is only sent via a separate intent, so we need to store it out of band
    public void setOutgoingNumber(String number) {
        savedNumber = number;
    }

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = incomingNumber;
                //onIncomingCallStarted(incomingNumber, callStartTime);
                Log.d(TAG, "onCallStateChanged: ");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing donw on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    //onOutgoingCallStarted(savedNumber, callStartTime);
                    Log.d(TAG, "onCallStateChanged: ");

                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    //onMissedCall(savedNumber, callStartTime);
                    meetingcreateNotification();

                    Log.d(TAG, "onCallStateChanged: ");
                } else if (isIncoming) {
                    //onIncomingCallEnded(savedNumber, callStartTime, new Date());
                    Log.d(TAG, "onCallStateChanged: ");
                    meetingcreateNotification();

                } else {
                    //onOutgoingCallEnded(savedNumber, callStartTime, new Date());
                    Log.d(TAG, "onCallStateChanged: ");
                }
                break;
        }
        lastState = state;
    }

    private void meetingcreateNotification() {
        Intent intent = new Intent(context, SchedulerActivity.class);
        intent.putExtra("Number",savedNumber);
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        showNotification(context, "Call from " + savedNumber + " ", "Schedule a meeting for " + savedNumber + " ? ", intent, m);
    }


    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "com.gihansandaru.callscheduler";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "com.gihansandaru.callschedular";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
        Log.d("showNotification", "showNotification: " + reqCode);
    }

}
