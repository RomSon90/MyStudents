package com.englishalternative.mystudents;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.englishalternative.mystudents.dialogs.DatePickerDialog;
import com.englishalternative.mystudents.dialogs.DurationDialog;
import com.englishalternative.mystudents.dialogs.TextInputDialog;

import java.util.Calendar;

public class LessonActivity extends Activity
        implements
        DatePickerDialog.DatePickerListener,
        DurationDialog.DurationSetListener,
        TextInputDialog.TextChangedListener {


    boolean isNewLesson;
    Lesson lesson;

    int id;
    Student student;
    String date;
    String topic;
    String hWork;
    int price;
    boolean paid;
    int duration;
    long dateUnix;

    boolean isHwDone;

    StudentData sData;

    Calendar c;

    TextView[] textViews;

    TextView dateTextView;
    TextView durationTextView;
    TextView topicTextView;
    TextView priceTextView;
    CheckBox paidCheckbox;
    TextView homeworkTextView;

    TextView studentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        // If student ID < 0 it doesn't exist in database, something went wrong.
        int sId = intent.getIntExtra("STUDENT_ID", -1);
        if (sId < 0) {
            Toast.makeText(this, R.string.message_student_unavailable, Toast.LENGTH_SHORT);
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
            finish();
            return;
        }
        setContentView(R.layout.activity_lesson);

        // Get student data
        sData = StudentData.getInstance(this);
        student = sData.getStudent(sId);



        // Check if its a new lesson (id < 0) or an existing lesson (id > 0)
        id = intent.getIntExtra("LESSON_ID", -1);
        isNewLesson = id < 0;



        // Create reference to topic edit text, priceTextView etc.
        dateTextView = (TextView) findViewById(R.id.date_lesson);
        topicTextView = (TextView) findViewById(R.id.topic_lesson_new);
        priceTextView = (TextView) findViewById(R.id.price_lesson_new);
        paidCheckbox = (CheckBox) findViewById(R.id.paid_checkbox);
        homeworkTextView = (TextView) findViewById(R.id.homework_lesson_new);
        studentTextView = (TextView) findViewById(R.id.student_name);

        // Show student nameView
        studentTextView.setText(student.getName());

        // Make DurationTextView display duration picker
        durationTextView = (TextView) findViewById(R.id.lesson_duration);
        durationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = DurationDialog.getDurationDialog(duration);
                dialog.show(getFragmentManager(), "DURATION_DIALOG");
            }
        });

        // Load calendar with current date.
        c = Calendar.getInstance();

        if (isNewLesson) {
            //Initialize empty variables
            topic = "";
            hWork = "";
            price = student.getPrice();
            duration = student.getDuration();
        } else {
            // Lesson already exists, show its details and set save button to update
            lesson = sData.getLesson(id, student.getId());
            date = lesson.getDate();
            topic = lesson.getTopic();
            hWork = lesson.getHomework();
            price = lesson.getPrice();
            duration = lesson.getDuration();
            paid = lesson.isPaid();
            price = lesson.getPrice();
            c.setTime(Lesson.stringToDate(date));

        }

        // Set all texts
        dateSet(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        durationSet(duration);
        topicTextView.setText(topic);
        priceTextView.setText(String.valueOf(price));
        paidCheckbox.setChecked(paid);
        homeworkTextView.setText(hWork);

        // Make onClickListener to start dialog
        View.OnClickListener startInputDialog = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextInputDialog.TextType textType = TextInputDialog.TextType.UPPERCASE;
                if (v == priceTextView)
                    textType = TextInputDialog.TextType.NUMBERS;

                TextInputDialog dialog = TextInputDialog.getInstance(
                        ((TextView) v).getText().toString(), v, textType );
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, "textInput");
            }
        };

        // Create array of views to use them in textChangedListener

        textViews = new TextView[] {topicTextView, priceTextView, homeworkTextView};

        for (TextView textView : textViews) {
            textView.setOnClickListener(startInputDialog);
        }

        // Set title to show on the menu
        if (isNewLesson)
            setTitle(R.string.new_lesson);
        else
            setTitle(R.string.lesson_details);

    }

    @Override
    public void onTextChanged(String text, int viewId) {
        for (TextView textView : textViews) {
            if (viewId == textView.getId())
                textView.setText(text);
            topic = topicTextView.getText().toString();
            price = Integer.parseInt(priceTextView.getText().toString());
            hWork = homeworkTextView.getText().toString();
        }



    }

    public void durationSet(int duration) {

        this.duration = duration;
        int hours = duration / 60;
        int minutes = duration - hours*60;
        durationTextView.setText(hours + " h : " + minutes + " m");
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lesson, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int itemId = item.getItemId();

        switch (itemId) {
            case R.id.add_lesson:
                // Get data from EditTexts etc.
                topic = topicTextView.getText().toString().trim();
                price = Integer.parseInt(priceTextView.getText().toString().trim());
                paid = paidCheckbox.isChecked();
                hWork = homeworkTextView.getText().toString().trim();
                dateUnix = c.getTimeInMillis();

                // Add lesson to database
                if (isNewLesson) {
                    Lesson l = new Lesson(
                            student, hWork, false, topic, price, paid, duration, dateUnix);
                    if (sData.addLesson(l))
                        Toast.makeText(this,
                                getString(R.string.message_lesson_added), Toast.LENGTH_SHORT).show();
                }
                // Or update existing lesson
                else {
                    Lesson l = new Lesson(
                            id, student, hWork, false, topic, price, paid, duration, dateUnix);
                    if (sData.updateLesson(l))
                        Toast.makeText(this,
                                getString(R.string.message_lesson_updated), Toast.LENGTH_SHORT).show();
                }
                break;

        // When user clicks delete check if the lesson exists
            case R.id.delete_lesson:
                if (isNewLesson) {
                    Toast.makeText(this, R.string.lesson_missing_error, Toast.LENGTH_SHORT).show();
                    return true;
                }
                else {
                    Lesson l = new Lesson(
                            id, student, hWork, false, topic, price, paid, duration, dateUnix);
                    if (sData.deleteLesson(l))
                        Toast.makeText(this,
                                getString(R.string.message_lesson_deleted), Toast.LENGTH_SHORT).show();
                }
                finish();
                break;

            case android.R.id.home:
                finish();
                return true;
            }

        finish();

        return super.onOptionsItemSelected(item);
    }

    public void setLessonDate(View v) {
        DialogFragment dateFragment = DatePickerDialog.getDateInstance(c);
        dateFragment.show(getFragmentManager(), "datePicker");
    }

    public void dateSet(int year, int month, int day) {
        // Get date from date picker
        c.set(year, month, day);
        // Format date and show it
        date = Lesson.dateToString(c.getTime());
        dateTextView.setText(date);
    }



}
