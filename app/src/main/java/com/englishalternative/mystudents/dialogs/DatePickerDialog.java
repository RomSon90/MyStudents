package com.englishalternative.mystudents.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Roman on 14.03.2017.
 */

public class DatePickerDialog extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {

    public interface DatePickerListener {
        // Communicate date to the caller
        public void dateSet(int year, int month, int day);
    }

    public static DatePickerDialog getDateInstance(Calendar c) {
        // Use calendar instance to get default or selected date
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Set date as arguments
        Bundle args = new Bundle();
        args.putInt("YEAR", year);
        args.putInt("MONTH", month);
        args.putInt("DAY", day);

        // Create instance
        DatePickerDialog datePicker = new DatePickerDialog();
        datePicker.setArguments(args);
        return datePicker;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get arguments and use them to create DatePickerDialog
        Bundle args = getArguments();
        int year = args.getInt("YEAR");
        int month = args.getInt("MONTH");
        int day = args.getInt("DAY");


        // Create a new instance of DatePickerDialog and return it
        return new android.app.DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        DatePickerListener listener = (DatePickerListener) getActivity();
        listener.dateSet(year, month, day);
    }
}
