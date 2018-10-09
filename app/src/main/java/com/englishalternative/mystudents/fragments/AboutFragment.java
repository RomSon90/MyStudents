package com.englishalternative.mystudents.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.englishalternative.mystudents.MainActivity;
import com.englishalternative.mystudents.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    String version_text;


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.fragment_about, container, false);

        // Present information about the app
        TextView version = (TextView) layout.findViewById(R.id.about_version);

        version_text = getString(R.string.about_version, MainActivity.VERSION);
        version.setText(version_text);

        TextView email = (TextView) layout.findViewById(R.id.about_email);
        email.setMovementMethod(LinkMovementMethod.getInstance());
        return layout;
    }



}
