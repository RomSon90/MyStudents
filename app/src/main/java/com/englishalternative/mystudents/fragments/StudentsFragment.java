package com.englishalternative.mystudents.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.englishalternative.mystudents.ExpandableListAdapter;
import com.englishalternative.mystudents.LessonActivity;
import com.englishalternative.mystudents.R;
import com.englishalternative.mystudents.Student;
import com.englishalternative.mystudents.StudentActivity;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentsFragment extends Fragment  implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "StudentsFragmentTag";

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private static int spinnerPosition = -1;

    private static long lastOpenTime = 0;
    Spinner daySpinner;


    public StudentsFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            spinnerPosition = savedInstanceState.getInt("spinner_position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_students, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Override listeners from ExpandableListAdapter to separate group from buttons
        expandableListAdapter = new ExpandableListAdapter(getActivity()){

            //When lesson is clicked open LessonActivity with lesson ID
            @Override
            public void onLessonDetailClick(int studentId, int lessonId) {
                Intent lessonDetails = new Intent(getActivity(), LessonActivity.class);
                lessonDetails.putExtra("STUDENT_ID", studentId);
                lessonDetails.putExtra("LESSON_ID", lessonId);
                startActivity(lessonDetails);
            }

            //When student nameView on the list is clicked expand the list
            @Override
            public void onGroupClick(boolean isExpanded, int position) {
                // If it's the last item on the list, add new student
                if (position == expandableListAdapter.getGroupCount() - 1) {
                    onDetailsClick(-1);
                    return;
                }
                if (isExpanded)
                    expandableListView.collapseGroup(position);
                else
                    expandableListView.expandGroup(position);
            }
            // When add lesson is clicked start LessonActivity
            @Override
            public void onNewLessonClick(int position) {
                Intent newLesson = new Intent(getActivity(), LessonActivity.class);
                newLesson.putExtra("STUDENT_ID", ((Student) getGroup(position)).getId());
                startActivity(newLesson);

            }

            //When details is clicked start StudentActivity with ID
            @Override
            public void onDetailsClick(int studentId) {
                //get student
                int sId;
                if (studentId > -1) {
                    Student s = (Student) getGroup(studentId);
                    sId = s.getId();
                } else {
                    sId = -1;
                }

                //Put student ID in intent
                Intent intent = new Intent(getActivity(), StudentActivity.class);
                intent.putExtra("STUDENT_ID", sId);
                startActivity(intent);
            }
        };

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandable_list_view);
        expandableListView.setAdapter(expandableListAdapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu;
        inflater.inflate(R.menu.menu_main, menu);

        // Setup day spinner in menu
        MenuItem item = menu.findItem(R.id.day_spinner);
        daySpinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.sort_days, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
        daySpinner.setGravity(17); //17 == center
        daySpinner.setOnItemSelectedListener(this);
        setSpinner();

    }
 /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_student) {
            Intent intent = new Intent(getActivity(), StudentActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }
*/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Choose sorting of list based on selected daySpinner position, collapse all groups
        if (spinnerPosition != position) {
            spinnerPosition = position;
            setSpinner();
            for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                expandableListView.collapseGroup(i);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setSort(int dayPosition) {
        // Change position to SortType value and call setSort
        expandableListAdapter.setSort(ExpandableListAdapter.SortType.values()[dayPosition]);
        expandableListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("spinner_position", spinnerPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        // After Fragment is visible set sorting again to reflect changes made
        // Set last open time, determine if new sorting for students is necessary
        long currentTime = System.currentTimeMillis();

        if (daySpinner != null) {
            if (currentTime - lastOpenTime > 21600000) {
                spinnerPosition = -1;

            }
            setSpinner();
        }
        lastOpenTime = currentTime;
        super.onResume();
    }

    private void setSpinner() {
        // Set spinner to a new position, set sorting of expList to reflect this change
        //Log.d(TAG,"SpinnerPosition before calendar: " + spinnerPosition + "");
        if (spinnerPosition < 0) {
            // Set the current day as the day to show
            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case Calendar.SUNDAY:
                    spinnerPosition = 6;
                    break;
                default:
                    spinnerPosition = day - 2;
            }
        }

        daySpinner.setSelection(spinnerPosition);
        setSort(spinnerPosition);
    }
}
