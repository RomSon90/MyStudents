package com.englishalternative.mystudents;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.englishalternative.mystudents.dialogs.DayDialog;
import com.englishalternative.mystudents.dialogs.DurationDialog;
import com.englishalternative.mystudents.dialogs.LessonListDialog;
import com.englishalternative.mystudents.dialogs.TextInputDialog;

public class StudentActivity extends Activity implements
        DayDialog.DayDialogListener,
        TextInputDialog.TextChangedListener,
        DurationDialog.DurationSetListener {

    TextView nameView;
    TextView placeView;
    TextView clssView;
    TextView dayButton;
    TextView showLessonsButton;
    StudentData sData;

    TextView priceView;
    TextView durationView;

    String name;
    String place;
    String clss;

    int price;
    int duration;

    boolean[] selectedDays;
    TextView[] textViews;

    Student student;
    int studentId;

    boolean isNewStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Get student ID
        Intent intent = getIntent();
        studentId = intent.getIntExtra("STUDENT_ID", -1);
        // If ID < 0, student doesn't exist
        isNewStudent = studentId < 0;

        sData = StudentData.getInstance(this);

        // Get references to views and initialize with empty strings
        // to avoid NullPointerException, set OnClickListener
        // to show input dialog


        nameView = (TextView) findViewById(R.id.name_student);
        placeView = (TextView) findViewById(R.id.place_student);
        clssView = (TextView) findViewById(R.id.class_student);
        dayButton = (TextView) findViewById(R.id.date_button);
        priceView = (TextView) findViewById(R.id.price_student);
        durationView = (TextView) findViewById(R.id.duration_student);
        showLessonsButton = (TextView) findViewById(R.id.show_lessons_button);

        name = "";
        place = "";
        clss = "";
        price = 0;
        duration = 0;
        selectedDays = new boolean[7];

        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = DayDialog.newInstance(selectedDays);
                dialog.show(getFragmentManager(), "DateDialogFragment");
            }
        });

        if (!isNewStudent) {
            student = sData.getStudent(studentId);
            // Present student data in layout
            name = student.getName();
            place = student.getPlace();
            clss = student.getClss();
            selectedDays = student.getDate();
            price = student.getPrice();
            duration = student.getDuration();

    }

        // Make OnClickListener to start input dialog
        View.OnClickListener startTextInput = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputDialog.TextType textType = TextInputDialog.TextType.UPPERCASE;
                if (v == priceView)
                    textType = TextInputDialog.TextType.NUMBERS;

                TextInputDialog dialog = TextInputDialog.getInstance(
                        ((TextView) v).getText().toString(), v, textType );
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, "textInput");
            }
        };

        nameView.setOnClickListener(startTextInput);
        placeView.setOnClickListener(startTextInput);
        clssView.setOnClickListener(startTextInput);
        priceView.setOnClickListener(startTextInput);

        durationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the duration dialog
                DialogFragment dialog = DurationDialog.getDurationDialog(duration);
                dialog.show(getFragmentManager(), "DURATION_DIALOG");
            }
        });

        showLessonsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show new dialog with student lessons
                if (!isNewStudent) {
                    sData.updateStudentLessons(student);
                    DialogFragment lessonsDialog = LessonListDialog.getInstance
                            (student.getLessons(), getString(R.string.lessons), 0);
                    lessonsDialog.show(getFragmentManager(), "LESSONS_DIALOG");
                }
            }
        });

        nameView.setText(name);
        placeView.setText(place);
        clssView.setText(clss);
        priceView.setText(String.valueOf(price));
        durationSet(duration);

        // Add TextViews to array to use them in onTextChanged interface
        textViews = new TextView[]{nameView, placeView, clssView, priceView};

        // Set activity title
        if (isNewStudent)
            setTitle(R.string.new_student);
        else
            setTitle(R.string.student_details);

    }

    @Override
    public void onTextChanged(String text, int viewId) {

        // When user finishes typing text store it

        for (TextView v : textViews) {
            if (v.getId() == viewId)
                v.setText(text);
        }
        name = nameView.getText().toString();
        place = placeView.getText().toString();
        clss = clssView.getText().toString();
        price = Integer.parseInt(priceView.getText().toString());
    }

    public void durationSet(int duration) {

        this.duration = duration;
        int hours = duration / 60;
        int minutes = duration - hours*60;
        durationView.setText(hours + " h : " + minutes + " m");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();

        switch (id) {
            case R.id.save_student:
                // Determine if it's new student or existing one
                if (isNewStudent){
                    Student s = new Student(
                            name.trim(),
                            place.trim(),
                            clss.trim(),
                            selectedDays,
                            price,
                            duration);

                    if (sData.addStudent(s))
                        Toast.makeText(this, getString(R.string.message_student_added),
                                Toast.LENGTH_SHORT).show();
                } else {

                    Student s = new Student(
                            studentId,
                            name.trim(),
                            place.trim(),
                            clss.trim(),
                            selectedDays,
                            price,
                            duration);
                    if (sData.updateStudent(s))
                        Toast.makeText(this, getString(R.string.message_student_updated),
                                Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.delete_student:
                //Delete student from database
                if (!isNewStudent) {
                    if (sData.deleteStudent(student))
                        Toast.makeText(this, getString(R.string.message_student_deleted),
                                Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,
                            getString(R.string.message_student_unavailable),
                            Toast.LENGTH_SHORT).show();
                }
                break;


            case android.R.id.home:
                finish();
                return true;
            }
        finish();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //sData.close();
    }

    @Override
    public void getDialogData(boolean[] selectedDays) {
        this.selectedDays = selectedDays;
    }
}


