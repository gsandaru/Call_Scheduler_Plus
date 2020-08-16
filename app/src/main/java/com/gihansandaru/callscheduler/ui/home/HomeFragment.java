package com.gihansandaru.callscheduler.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gihansandaru.callscheduler.R;
import com.gihansandaru.callscheduler.models.CallLogData;
import com.gihansandaru.callscheduler.ui.dashboard.DashboardFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_CONTACTS;

public class HomeFragment extends Fragment {
    private static final int REQUEST_READ_CONTACTS = 0;
    private HomeViewModel homeViewModel;
    private View root;
    private RecyclerView recyclerView;
    private SwitchMaterial switchHideKnownContacts;
    private CallLogAdapter callLogAdapter;
    private List<CallLogData> callLogs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);


        switchHideKnownContacts = root.findViewById(R.id.switchHideKnownContacts);


        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callLogs = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        callLogAdapter = new CallLogAdapter(new ArrayList<>());
        recyclerView.setAdapter(callLogAdapter);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( requireActivity(),
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    REQUEST_READ_CONTACTS
            );
        } else {
            readContacts();
        }

        homeViewModel.getCallLogList().observe(getViewLifecycleOwner(), callLogData -> {
            if (callLogData != null) {
                callLogs = callLogData;
                showCallDetails(callLogData);
            }
        });

        switchHideKnownContacts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(callLogs.size() > 0){
                if(isChecked) {
                    List<CallLogData> filteredList = callLogs.stream().filter(item -> item.getName() == null).collect(Collectors.toList());
                    showCallDetails(filteredList);
                }else{
                    showCallDetails(callLogs);
                }
            }
        });

        switchHideKnownContacts.setChecked(false);
    }

    private void showCallDetails(List<CallLogData> newData) {
        callLogAdapter.setCallLogData(newData);
        callLogAdapter.notifyDataSetChanged();
    }

    private void readContacts() {
        List<CallLogData> callLogData = new ArrayList<>();

        String[] projection = new String[]{
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        Cursor managedCursor = requireContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, null);
        while (managedCursor.moveToNext()) {
            String name = managedCursor.getString(0); //name
            String number = managedCursor.getString(1); // number
            String type = managedCursor.getString(2); // type
            String date = managedCursor.getString(3); // time
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
            String dateString = formatter.format(new Date(Long.parseLong(date)));

            String duration = managedCursor.getString(4); // duration

            String dir = null;
            int dircode = Integer.parseInt(type);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    continue;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }

            CallLogData data = new CallLogData(name, number, dir, dateString, duration);
            callLogData.add(data);
        }
        homeViewModel.setCallLogList(callLogData);
        int a = 0;
    }

    class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {
        List<CallLogData> callLogDataList;

        public CallLogAdapter(List<CallLogData> callLogDataList) {
            this.callLogDataList = callLogDataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_cell, parent, false);
            return new ViewHolder(layoutview);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CallLogData callLogData = callLogDataList.get(position);
            holder.txtNumberOrName.setText(callLogData.getName() == null ? callLogData.getNumber() : callLogData.getName());
            holder.txtDateTime.setText(callLogData.getDateTime());
            if (callLogData.getCallType() != null && callLogData.getCallType().equals("MISSED")) {
                Glide.with(holder.imgCallType.getContext()).asDrawable()
                        .load(R.drawable.missed_call)
                        .into(holder.imgCallType);
                holder.imgCallType.setColorFilter(Color.parseColor("#FFAF4C4C"));
            } else {
                Glide.with(holder.imgCallType.getContext()).asDrawable()
                        .load(R.drawable.incoming_call)
                        .into(holder.imgCallType);
                holder.imgCallType.setColorFilter(Color.parseColor("#4CAF50"));
            }
            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("Number",callLogData.getNumber());
                bundle.putString("Name",callLogData.getName());
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_navigation_home_to_navigation_dashboard,bundle);
            });
            holder.txtCallDuration.setText(getDurationString(Integer.parseInt(callLogData.getCallDuration())));
        }

        @Override
        public int getItemCount() {
            return callLogDataList.size();
        }

        public void setCallLogData(List<CallLogData> newData) {
            this.callLogDataList = newData;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtDateTime;
            TextView txtNumberOrName;
            ImageView imgCallType;
            TextView txtCallDuration;
            ImageButton btnAddAppointment;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtDateTime = itemView.findViewById(R.id.txtDateTime);
                txtNumberOrName = itemView.findViewById(R.id.txtNumberOrName);
                imgCallType = itemView.findViewById(R.id.imgCallType);
                txtCallDuration = itemView.findViewById(R.id.txtCallDuration);
                btnAddAppointment = itemView.findViewById(R.id.btnAddAppointment);
            }
        }

        private String getDurationString(int seconds) {
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            seconds = seconds % 60;
            return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
        }

        private String twoDigitString(int number) {
            if (number == 0) {
                return "00";
            }
            if (number / 10 == 0) {
                return "0" + number;
            }
            return String.valueOf(number);
        }
    }
}
