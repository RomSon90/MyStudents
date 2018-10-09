package com.englishalternative.mystudents.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.englishalternative.mystudents.R;

/**
 * Created by Roman on 24.03.2017.
 */

public class DurationDialog extends DialogFragment {

    static final int MAX_HOURS = 8;
    static final int MINUTES_STEP = 5;
    static final int MAX_MINUTES = 55 / MINUTES_STEP; //Create 5 minute steps for minutePicker

    int durationHours;
    int durationMinutes;
    int totalTime;

    public interface DurationSetListener {
        public void durationSet(int duration);
    }

    public static DurationDialog getDurationDialog (int duration) {

        // Make new dialog supplying default or defined duration in minutes
        DurationDialog dialog = new DurationDialog();
        Bundle durationTime = new Bundle();
        durationTime.putInt("DURATION", duration);
        dialog.setArguments(durationTime);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle time = getArguments();
        //Change supplied duration time to hours and minutes
        totalTime = time.getInt("DURATION");
        durationHours = totalTime / 60;
        durationMinutes = totalTime - durationHours * 60;

        // Build custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate custom layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_duration, null);
        // Set hour and minute picker
        final NumberPicker hourPicker = (NumberPicker) layout.findViewById(R.id.hour_picker);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(MAX_HOURS);
        hourPicker.setValue(durationHours);

        final NumberPicker minutePicker = (NumberPicker) layout.findViewById(R.id.minute_picker);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(MAX_MINUTES);
        String [] minutes = new String[12];
        for (int i = 0; i < 12; i ++)
            minutes[i] = String.valueOf(i * MINUTES_STEP);
        minutePicker.setDisplayedValues(minutes);
        minutePicker.setValue(durationMinutes / MINUTES_STEP);

        builder.setView(layout);

        // On positive button set activity's duration
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int newHours = hourPicker.getValue();
                int newMinutes = minutePicker.getValue() * MINUTES_STEP;
                DurationSetListener activity = (DurationSetListener) getActivity();
                activity.durationSet(newHours * 60 + newMinutes);
            }
        });
        return builder.create();


    }
}