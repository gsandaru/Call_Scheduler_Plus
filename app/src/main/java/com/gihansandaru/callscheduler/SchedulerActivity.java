package com.gihansandaru.callscheduler;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gihansandaru.callscheduler.ui.home.HomeFragment;
import com.gihansandaru.callscheduler.ui.services.CallHandlerService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SchedulerActivity extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private CallHandlerService callHandlerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        checkAndRequestPermissions();
        //callHandlerService = new CallHandlerService();
        Intent serviceIntent = new Intent(this,CallHandlerService.class);
        if (!isMyServiceRunning(CallHandlerService.class)) {
            startService(serviceIntent);
        }

        if(getIntent()!=null){
            String number = getIntent().getStringExtra("Number");
            if( number != null && !number.equals("")){
                Bundle bundle = new Bundle();
                bundle.putString("Number",number);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.action_navigation_home_to_navigation_dashboard,bundle);
            }
        }


        try {
            String dateStr = "16/8/2020";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date startday = sdf.parse(dateStr);
            Date today = new Date();

            int difference=
                    ((int)((today.getTime()/(24*60*60*1000))
                            -(int)(startday.getTime()/(24*60*60*1000))));

            if(difference > 5) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Trial Mode Evaluated");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.setMessage("Please contact developer");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Exit",
                        (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        });
                alertDialog.show();
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }




    private  boolean checkAndRequestPermissions() {
        int readPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int read_call_log = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (read_call_log != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}