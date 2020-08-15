package com.gihansandaru.callscheduler.ui.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.gihansandaru.callscheduler.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private AutoCompleteTextView autoCompleteMeetingType;
    private TextInputEditText txtReminderDate;
    private TextInputEditText txtReminderTime;
    private Button btnAddAppointment;
    private Button btnClear;
    private TextInputEditText txtNumber;
    private TextInputEditText txtCustomerName;
    private TextInputEditText txtVehicleType;
    private TextInputEditText txtAreaDesc;
    private int savehour;
    private int saveminutes;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        autoCompleteMeetingType = root.findViewById(R.id.autoCompleteMeetingType);
        txtReminderDate = root.findViewById(R.id.txtReminderDate);
        txtReminderTime = root.findViewById(R.id.txtReminderTime);
        btnAddAppointment = root.findViewById(R.id.btnAddAppointment);
        btnClear = root.findViewById(R.id.btnClear);

        txtNumber = root.findViewById(R.id.txtNumber);
        txtCustomerName = root.findViewById(R.id.txtCustomerName);
        txtVehicleType = root.findViewById(R.id.txtVehicleType);
        txtAreaDesc = root.findViewById(R.id.txtAreaDesc);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (getArguments() != null) {
            String number = getArguments().getString("Number");
            String name = getArguments().getString("Name");
            txtNumber.setText(number);
            txtCustomerName.setText(name);
        }

        final Calendar myCalendar = Calendar.getInstance();

        String[] options_list = {"Voice Call", "SMS", "Whatsapp", "Viber", "IMO", "Skype", "EMAIL"};
        autoCompleteMeetingType.setAdapter(new ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, options_list));
        txtReminderDate.setOnClickListener(v -> {
            Date dateToSelect = null;
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                dateToSelect = simpleDateFormat.parse(txtReminderDate.getText().toString());
                myCalendar.setTime(dateToSelect);
            } catch (ParseException e) {
                dateToSelect = new Date();
                e.printStackTrace();
            }

            new DatePickerDialog(requireActivity(), (view1, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                txtReminderDate.setText(sdf.format(myCalendar.getTime()));
            }, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        txtReminderTime.setOnClickListener(v -> {

            if (!txtReminderTime.getText().toString().equals("")) {

            }

            Calendar mcurrentTime = Calendar.getInstance();
            int Currenthour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int Currentminute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    savehour = hourOfDay;
                    saveminutes = minute;
                    int hour = hourOfDay;
                    int minutes = minute;
                    String timeSet = "";
                    if (hour > 12) {
                        hour -= 12;
                        timeSet = "PM";
                    } else if (hour == 0) {
                        hour += 12;
                        timeSet = "AM";
                    } else if (hour == 12) {
                        timeSet = "PM";
                    } else {
                        timeSet = "AM";
                    }

                    String min = "";
                    if (minutes < 10)
                        min = "0" + minutes;
                    else
                        min = String.valueOf(minutes);

                    String mTime = new StringBuilder().append(hour).append(':')
                            .append(min).append(" ").append(timeSet).toString();
                    txtReminderTime.setText(mTime);
                }
            }, Currenthour, Currentminute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        });

        btnAddAppointment.setOnClickListener(v -> {
            addAppointment();
        });
        btnClear.setOnClickListener(v -> {
            clearAll();
        });

//        getView().findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar beginTime = Calendar.getInstance();
//                beginTime.set(2020, 9, 16, 10, 30);
//                Calendar endTime = Calendar.getInstance();
//                endTime.set(2020, 9, 16, 11, 30);
//                Intent intent = new Intent(Intent.ACTION_INSERT)
//                        .setData(CalendarContract.Events.CONTENT_URI)
//                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
//                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
//                        .putExtra(CalendarContract.Events.TITLE, "Test Event")
//                        .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
//                        .putExtra(CalendarContract.Events.EVENT_LOCATION, "Test Location")
//                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
//                        .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
//                startActivity(intent);
//            }
//        });
    }

    private void clearAll() {

        autoCompleteMeetingType.setText("");
        txtReminderDate.setText("");
        txtReminderTime.setText("");
        txtNumber.setText("");
        txtCustomerName.setText("");
        txtVehicleType.setText("");
        txtAreaDesc.setText("");

    }

    private void addAppointment() {
        if (validateRequiredFields()) {

            String title = new StringBuilder().append("Meeting (").append(autoCompleteMeetingType.getText()).append(") With ")
                    .append(txtCustomerName.getText().toString().equals("") ? txtNumber.getText().toString() : txtCustomerName.getText().toString()).append(" , ")
                    .append(txtVehicleType.getText().toString().equals("") ? "" : "Vehicle - " + txtVehicleType.getText().toString())
                    .toString();

            String desc = new StringBuilder()
                    .append(" Customer Name : ").append(txtCustomerName.getText().toString().equals("") ? "N/A" : txtCustomerName.getText().toString()).append("\n")
                    .append(" Meeting Type : ").append(autoCompleteMeetingType.getText()).append("\n")
                    .append(" Mobile Number : ").append(txtNumber.getText().toString()).append("\n")
                    .append(" Vehicle Type : ").append(txtVehicleType.getText().toString().equals("") ? "N/A" : txtVehicleType.getText().toString()).append("\n")
                    .append(" Meeting Details : ").append(txtAreaDesc.getText().equals("") ? "N/A" : txtAreaDesc.getText()).toString();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendarex = Calendar.getInstance();
            try {
                Date dateStart = simpleDateFormat.parse(txtReminderDate.getText().toString());
                calendarex.setTime(dateStart);
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(calendarex.get(Calendar.YEAR), calendarex.get(Calendar.MONTH), calendarex.get(Calendar.DAY_OF_MONTH), savehour, saveminutes);
                Calendar endTime = Calendar.getInstance();
                endTime.set(calendarex.get(Calendar.YEAR), calendarex.get(Calendar.MONTH), calendarex.get(Calendar.DAY_OF_MONTH), savehour, saveminutes);
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, title)
                        .putExtra(CalendarContract.Events.DESCRIPTION, desc)
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
                startActivity(intent);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean validateRequiredFields() {
        if (txtNumber.getText() == null || txtNumber.getText().toString().equals("")) {
            txtNumber.setError("Required");
            return false;
        }
        if (txtReminderDate.getText() == null || txtReminderDate.getText().toString().equals("")) {
            txtReminderDate.setError("Required");
            return false;
        }
        if (txtReminderTime.getText() == null || txtReminderTime.getText().toString().equals("")) {
            txtReminderTime.setError("Required");
            return false;
        }
        if (autoCompleteMeetingType.getText() == null || autoCompleteMeetingType.getText().toString().equals("")) {
            autoCompleteMeetingType.setError("Required");
            return false;
        }
        return true;
    }
}
