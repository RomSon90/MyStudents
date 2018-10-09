package com.englishalternative.mystudents.fragments;


import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.englishalternative.mystudents.Lesson;
import com.englishalternative.mystudents.R;
import com.englishalternative.mystudents.Student;
import com.englishalternative.mystudents.StudentData;

import com.englishalternative.mystudents.dialogs.LessonListDialog;
import com.englishalternative.mystudents.dialogs.StudentPickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment
        implements StudentPickerDialog.StudentSelectedListener, LessonListDialog.LessonListDialogListener {

    private List<Student> allStudents;
    String selectedStudentsString;
    private List<Student> selectedStudents;
    TextView selectedStudentsView;
    private List<StudentInfo> studentInfos;
    private List<Lesson> unpaidLessons;

    TextView startDate;
    TextView endDate;

    TextView timeTextView;
    TextView moneyTextView;
    View unpaidView;
    int time;
    int money;
    int moneyUnpaid;

    View timeView;
    View moneyView;

    Calendar startDateCalendar;
    Calendar endDateCalendar;

    LessonListDialog lessonListDialog;
    StudentData sDataInstance;

    StudentPickerDialog.ShowList studentListState;

    @Override
    public void onLessonListDismiss() {
        refreshViews(selectedStudents);
    }

    @Override
    public void getStudents(List<Student> selectedStudents,
                            StudentPickerDialog.ShowList studentListState) {
        // get students selected from StudentPickerDialog
        this.selectedStudents = selectedStudents;
        refreshViews(selectedStudents);
        // save selected list state
        this.studentListState = studentListState;
    }

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check for changes in students' data and post them
        allStudents = sDataInstance.getAllStudents();
        refreshViews(selectedStudents);
        if (lessonListDialog != null) {
            lessonListDialog.lessonList = unpaidLessons;
            lessonListDialog.adapter.notifyDataSetChanged();
            lessonListDialog.dialog.setTitle(String.format(Locale.US,"%s - %d" ,
                    getString(R.string.unpaid_lessons),
                    moneyUnpaid));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get all student data and setup the views
        sDataInstance = StudentData.getInstance(getActivity());
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                sDataInstance.updateAllStudentLessons();

            }
        });
        t.start();


        moneyTextView = (TextView) view.findViewById(R.id.stats_money_text);
        timeTextView = (TextView) view.findViewById(R.id.stats_time_text);
        unpaidView = view.findViewById(R.id.stats_unpaid);
        timeView = view.findViewById(R.id.stats_time);
        moneyView = view.findViewById(R.id.stats_money);

        selectedStudentsView = (TextView) view.findViewById(R.id.stats_selected_students);
        selectedStudentsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studentListState == null)
                    studentListState = StudentPickerDialog.ShowList.CLASSES;
                DialogFragment dialogFragment = StudentPickerDialog.getInstance(selectedStudents,
                                                                                studentListState);
                dialogFragment.show(getChildFragmentManager(), "STATS");
            }
        });



        selectedStudents = new ArrayList<>();

        startDate = (TextView) view.findViewById(R.id.stats_start_date);
        endDate = (TextView) view.findViewById(R.id.stats_end_date);
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();
        startDateCalendar.set(Calendar.MONTH, endDateCalendar.get(Calendar.MONTH) - 1);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDateCalendar.set(year, month, dayOfMonth);
                        startDate.setText(SimpleDateFormat.getDateInstance()
                                .format(startDateCalendar.getTime()));
                        refreshViews(selectedStudents);
                    }
                }, startDateCalendar.get(Calendar.YEAR),
                        startDateCalendar.get(Calendar.MONTH),
                        startDateCalendar.get(Calendar.DAY_OF_MONTH));

                datePicker.show();
            }});

        endDate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    endDateCalendar.set(year, month, dayOfMonth);
                    endDate.setText(SimpleDateFormat.getDateInstance()
                            .format(endDateCalendar.getTime()));
                    refreshViews(selectedStudents);
                }
            }, endDateCalendar.get(Calendar.YEAR),
                    endDateCalendar.get(Calendar.MONTH),
                    endDateCalendar.get(Calendar.DAY_OF_MONTH));

            datePicker.show();
        }});

        /* Unnecessary graphs
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Collections.sort(studentInfos, timeComparator);
                for (StudentInfo si : studentInfos)
                    Log.d("StatsFrag", String.format("%s - %d", si.name, si.time));
                ChartDialog dialog = ChartDialog.getInstance(
                        getString(R.string.time), getNames(studentInfos), getTimes(studentInfos) );
                dialog.show(getChildFragmentManager(), "timeChart");
            }
        });


        moneyView.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Collections.sort(studentInfos, moneyComparator);
                for (StudentInfo si : studentInfos)
                    Log.d("StatsFrag", String.format("%s - %d", si.name, si.money));
                ChartDialog dialog = ChartDialog.getInstance(
                        getString(R.string.money), getNames(studentInfos), getMoney(studentInfos) );
                dialog.show(getChildFragmentManager(), "moneyChart");
            }
        }));
        */

        unpaidView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = String.format(Locale.US,"%s - %d" ,
                        getString(R.string.unpaid_lessons),
                        moneyUnpaid);
                lessonListDialog =
                        LessonListDialog.getInstance(unpaidLessons, title, 1, true);


                lessonListDialog.show(getChildFragmentManager(), "lessonList");

            }
        });

        startDate.setText(SimpleDateFormat.getDateInstance().format(startDateCalendar.getTime()));
        endDate.setText(SimpleDateFormat.getDateInstance().format(endDateCalendar.getTime()));

        try {
            t.join();
        } catch (InterruptedException ex) {

        }
    }

    private void refreshViews(List<Student> students) {

        // Calculate the statistics and show them in the views
        selectedStudentsString = "";

        if (students.isEmpty()) {
            selectedStudentsString = String.format(Locale.getDefault(),
                    "%s (%d)",getString(R.string.all), allStudents.size());
            students = this.allStudents;
        }

        else {
            for (Student s : students) {
                selectedStudentsString = selectedStudentsString + s.getName() + " : ";
            }
        }

        Date startDate = startDateCalendar.getTime();
        Date endDate = endDateCalendar.getTime();

        time = 0;
        money = 0;
        moneyUnpaid = 0;

        studentInfos = new ArrayList<>();
        unpaidLessons = new ArrayList<>();

        for (Student s : students) {
            StudentInfo tempInfo = new StudentInfo(s.getName());
            if (s.getLessons() != null) {
                for (Lesson l : s.getLessons()) {
                    try {
                        Date lessonDate = SimpleDateFormat.getDateInstance().parse(l.getDate());
                        if (!lessonDate.before(startDate) && !lessonDate.after(endDate)) {
                            int lessonTime = l.getDuration();
                            int lessonMoney = l.getPrice();
                            time += lessonTime;
                            money += lessonMoney;
                            tempInfo.time += lessonTime;
                            tempInfo.money += lessonMoney;
                            if (!l.isPaid()) {
                                unpaidLessons.add(l);
                                moneyUnpaid += lessonMoney;
                            }
                        }
                    } catch (ParseException ex) {

                    }
                }
            }
            studentInfos.add(tempInfo);
        }

        selectedStudentsView.setText(selectedStudentsString);
        int hours = time / 60;
        int minutes = time - hours*60;
        timeTextView.setText(String.format(Locale.US, "%02d h : %02d m", hours, minutes));

        moneyTextView.setText(String.format(Locale.US, "%d (-%d)", money, moneyUnpaid));
    }

    private int[] getTimes (List<StudentInfo> studentInfos) {
        int[] times = new int[studentInfos.size()];

        for (int i  = 0 ; i < studentInfos.size() ; i ++) {
            times[i] = studentInfos.get(i).time;
        }
        return times;
    }

    private int[] getMoney (List<StudentInfo> studentInfos) {

        int[] money = new int[studentInfos.size()];

        for (int i  = 0 ; i < studentInfos.size() ; i ++) {
            money[i] = studentInfos.get(i).money;
        }
        return money;
    }

    private String[] getNames (List<StudentInfo> students) {
        String[] names = new String[students.size()];
        for (int i  = 0 ; i < students.size() ; i ++) {
            names[i] = students.get(i).name;
        }
        return names;

    }

    private static Comparator<StudentInfo> timeComparator = new Comparator<StudentInfo>() {
        @Override
        public int compare(StudentInfo o1, StudentInfo o2) {
            return o2.time - o1.time;
        }
    };

    private static Comparator<StudentInfo> moneyComparator = new Comparator<StudentInfo>() {
        @Override
        public int compare(StudentInfo o1, StudentInfo o2) {
            return o2.money - o1.money;
        }
    };

    private class StudentInfo {
        String name;
        int money = 0;
        int time = 0;

        StudentInfo (String name) {
            this.name = name;
        }
    }
}
