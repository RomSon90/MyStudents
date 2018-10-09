package com.englishalternative.mystudents.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.englishalternative.mystudents.R;

/**
 * Created by Roman on 07.03.2017.
 */



public class DayDialog extends DialogFragment {

    public interface DayDialogListener {
        public void getDialogData(boolean[] selectedDays);
    }

    public static DayDialog newInstance(boolean[] selectedDays) {
        DayDialog dialog = new DayDialog();

        Bundle args = new Bundle();
        args.putBooleanArray("days", selectedDays);
        dialog.setArguments(args);
        return dialog;
    }

    private boolean[] selectedDays;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Bundle args = getArguments();
        selectedDays = args.getBooleanArray("days");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_day)
                .setMultiChoiceItems(R.array.days, selectedDays,
                        new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            selectedDays[which] = isChecked;
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DayDialogListener activity = (DayDialogListener) getActivity();
                            activity.getDialogData(selectedDays);
                        }
                    });
            return builder.create();
        }
    }


