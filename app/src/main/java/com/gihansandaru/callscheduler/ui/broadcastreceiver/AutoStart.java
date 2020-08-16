package com.gihansandaru.callscheduler.ui.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.gihansandaru.callscheduler.ui.services.CallHandlerService;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1) {
        Toast.makeText(context, "Starting Call Scheduler+.", Toast.LENGTH_LONG).show();

        try {
            Intent intent = new Intent(context, CallHandlerService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
