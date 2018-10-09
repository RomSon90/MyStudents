package com.englishalternative.mystudents.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.englishalternative.mystudents.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roman on 11.06.2017.
 */

public class ChartDialog extends DialogFragment {

    public static final String TITLE = "title";
    public static final String NAMES = "names";
    public static final String VALUES = "values";

    public static final int MAX_SHOWN = 5;

    String title;
    String[] names;
    int[] values;

    public static ChartDialog getInstance(String title, String[] names, int[] values) {

        // Usual dialog instanciation
        ChartDialog dialog = new ChartDialog();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putStringArray(NAMES, names);
        bundle.putIntArray(VALUES, values);
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        title = bundle.getString(TITLE);
        names = bundle.getStringArray(NAMES);
        values = bundle.getIntArray(VALUES);

        LayoutInflater inflater = getParentFragment().getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_graph, null);

        // Create data for the chart
        List<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < names.length ; i++) {

            if (i < MAX_SHOWN) {
                PieEntry entry = new PieEntry(values[i], names[i]);
                entries.add(entry);
            } else {
                int other = 0;
                for ( ; i < names.length ; i ++) {
                    other += values[i];
                }
                PieEntry entry = new PieEntry(other, getString(R.string.other));
                entries.add(entry);
                break;
            }

        }

        // Create the graph and feed it with data
        PieChart chart = (PieChart) v.findViewById(R.id.graph);
        PieDataSet set = new PieDataSet(entries, title);
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);

        chart.setDescription(null);
        chart.setData(data);
        chart.invalidate();


        // Return new dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentFragment().getActivity());
        Dialog dialog = builder.setView(v).setTitle(title).create();

        return dialog;
    }

}
