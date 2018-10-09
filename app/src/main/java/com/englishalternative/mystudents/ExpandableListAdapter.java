package com.englishalternative.mystudents;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Roman on 28.02.2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {


    public enum SortType {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, ALL
    }

    Context mContext;
    final String TAG = "ExpandableListAdapter";

    private StudentData sData;

    private int red;
    private int black;
    private int green;
    private int yellow;
    private int blue;
    private int mediumGray;
    private int lessonImageColor;


    private List<Student> studentList;

     public ExpandableListAdapter(Context c) {

         mContext = c;
         sData = StudentData.getInstance(mContext);
         red = ContextCompat.getColor(c, R.color.red_warning);
         black = ContextCompat.getColor(c, R.color.black);
         green = ContextCompat.getColor(c, R.color.green);
         blue = ContextCompat.getColor(c, R.color.blue);
         yellow = ContextCompat.getColor(c, R.color.yellow);
         mediumGray = ContextCompat.getColor(c, R.color.medium_gray);
         lessonImageColor = ContextCompat.getColor(c, R.color.lesson_image_color);
         refreshList();
    }

    public void setSort (SortType sortType) {

        refreshList();
        if (sortType != SortType.ALL) {
            // Sort by selected day
            int weekDay = sortType.ordinal();

            // Remove students who don't have lessons on this day
            for (Iterator<Student> iterator = studentList.iterator(); iterator.hasNext();) {
                Student student = iterator.next();
                boolean[] dates = student.getDate();
                if (!dates[weekDay])
                    iterator.remove();
            }
        }

        // Get students' lessons
        for (Student student : studentList) {
            sData.updateStudentLessons(student);
        }

        // Add fake student at the end to serve as "Add student" button
        Student startButton = new Student(-1, "add", "fake", "fake",
                new boolean[7], 0, 0);
        studentList.add(startButton);
    }

    public void refreshList() {
        studentList = sData.getAllStudents();
    }

    @Override
    public int getGroupCount() {
        return studentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // get student's lessons before expanding the group
        //sData.updateStudentLessons((Student) getGroup(groupPosition));
        //now student lessons load in setSort()
        if (studentList.get(groupPosition).getLessons() != null)
            return studentList.get(groupPosition).getLessons().size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return studentList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return studentList.get(groupPosition).getLessons().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public void onNewLessonClick(int position) {}

    public void onGroupClick(boolean isExpanded, int position) {}

    public void onDetailsClick(int studentId){}

    public void onLessonDetailClick(int studentId, int lessonId) {}

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {

        final Student s = (Student) getGroup(groupPosition);

        StudentViewHolder studentViewHolder;

        String studentName = s.getName();
        if (studentName.equals(""))
            studentName = "Name";
        String place = s.getPlace();
        if (place.equals(""))
            place = "Place";

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);



            convertView = inflater.inflate(R.layout.expandable_student, null);

            studentViewHolder = new StudentViewHolder();

            studentViewHolder.studentNameView = (TextView) convertView.findViewById(
                    R.id.expandable_student_name);
            studentViewHolder.studentPlaceView = (TextView) convertView.findViewById(
                    R.id.expandable_student_place);
            studentViewHolder.newLessonImage = (ImageView) convertView.findViewById(
                    R.id.expandable_add_lesson);
            studentViewHolder.indicator = (ImageView)convertView.findViewById(
                    R.id.expandable_indicator);
            studentViewHolder.addStudentText = (TextView) convertView.findViewById(
                    R.id.expandable_add_student);

            convertView.setTag(studentViewHolder);
        } else {
            studentViewHolder = (StudentViewHolder) convertView.getTag();
        }

        //Add separate listeners to student data layout and to buttons

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGroupClick(isExpanded, groupPosition);
            }
        });
        studentViewHolder.newLessonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNewLessonClick(groupPosition);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onDetailsClick(groupPosition);
                return true;
            }
        });

        // Set all fields to default
        studentViewHolder.studentNameView.setText(studentName);
        studentViewHolder.studentNameView.setTextColor(black);
        studentViewHolder.studentPlaceView.setText(place);

        int indicatorResource;
        indicatorResource = (isExpanded) ? R.drawable.ic_indicator_expanded : R.drawable.ic_indicator;
        studentViewHolder.indicator.setImageResource(indicatorResource);


        studentViewHolder.indicator.setColorFilter(R.color.light_gray);
        studentViewHolder.studentPlaceView.setVisibility(View.VISIBLE);
        studentViewHolder.newLessonImage.setVisibility(View.VISIBLE);
        studentViewHolder.indicator.setVisibility(View.VISIBLE);
        studentViewHolder.addStudentText.setVisibility(View.INVISIBLE);
        studentViewHolder.studentNameView.setVisibility(View.VISIBLE);


        List<Lesson> studentLessons = s.getLessons();
        if (studentLessons != null) {
            boolean isRed = false;
            boolean isGreen = false;
            // Check if has children, draw indicator
            if (studentLessons.isEmpty()) {
                //studentViewHolder.indicator.setVisibility(View.INVISIBLE); ukrywa wszystko
            }
            // Check if lesson was paid, if not change student indicator to red
            for (Lesson l : studentLessons) {
                if (!l.isPaid()) {
                    studentViewHolder.indicator.setColorFilter(red);
                    isRed = true;
                }

            }
            // Set duration icon to green if lesson has already been added on the day

            Calendar today = Calendar.getInstance();
            Calendar lastLessonCalendar = Calendar.getInstance();
            if (!studentLessons.isEmpty()) {
                for (Lesson lastLesson : studentLessons) {
                    lastLessonCalendar.setTimeInMillis(lastLesson.getDateUnix());
                    if (today.get(Calendar.DAY_OF_YEAR) == lastLessonCalendar.get(Calendar.DAY_OF_YEAR)
                            && ((today.getTimeInMillis() - lastLessonCalendar.getTimeInMillis()) < 86400000 )) {
                        studentViewHolder.indicator.setColorFilter(green);
                        isGreen = true;
                    }
                }
            }
            if (isGreen && isRed) {
                studentViewHolder.indicator.setColorFilter(blue);
            }

        }



        if (s.getId() < 0) {
            // Create another layout for "Add student" button

            studentViewHolder.studentPlaceView.setVisibility(View.INVISIBLE);
            studentViewHolder.newLessonImage.setVisibility(View.INVISIBLE);
            studentViewHolder.studentNameView.setVisibility(View.INVISIBLE);
            studentViewHolder.addStudentText.setVisibility(View.VISIBLE);
            studentViewHolder.indicator.setVisibility(View.INVISIBLE);

        }

        return convertView;
    }

    static class StudentViewHolder {
        TextView studentNameView;
        TextView studentPlaceView;
        TextView addStudentText;
        ImageView newLessonImage;
        ImageView indicator;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        // Get lesson from sData, inflate layout and populate with data


        Lesson l = (Lesson) getChild(groupPosition, childPosition);
        String lessonDate = l.getDate();
        String topic = l.getTopic();
        String homeWork = l.getHomework();
        String duration = l.getDurationConverted();

        LessonViewHolder lessonViewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.expandable_lesson, null);

            lessonViewHolder = new LessonViewHolder();

            lessonViewHolder.lessonDateView = (TextView) convertView.findViewById
                    (R.id.expandable_lesson_date);
            lessonViewHolder.lessonDurationView = (TextView) convertView.findViewById
                    (R.id.expandable_lesson_duration);
            lessonViewHolder.topicView = (TextView) convertView.findViewById(
                    R.id.expandable_lesson_topic);
            lessonViewHolder.lessonHomeworkView = (TextView) convertView.findViewById
                    (R.id.expandable_lesson_homework);
            lessonViewHolder.durationIcon = (ImageView) convertView.findViewById(
                    R.id.expandable_lesson_duration_image);

            convertView.setTag(lessonViewHolder);



        } else {
            lessonViewHolder = (LessonViewHolder) convertView.getTag();
        }

        lessonViewHolder.lessonDateView.setText(lessonDate);
        lessonViewHolder.lessonDurationView.setText(duration);
        if (topic.equals("")) {
            topic = mContext.getString(R.string.topic_lesson);
            lessonViewHolder.topicView.setTextColor(mediumGray);
        } else {
            lessonViewHolder.topicView.setTextColor(Color.BLACK);
        }
        lessonViewHolder.topicView.setText(topic);
        if (homeWork.equals("")) {
            homeWork = mContext.getString(R.string.homework_lesson);
            lessonViewHolder.lessonHomeworkView.setTextColor(mediumGray);
        } else {
            lessonViewHolder.lessonHomeworkView.setTextColor(Color.BLACK);
        }
        lessonViewHolder.lessonHomeworkView.setText(homeWork);

        // Set duration icon to red if lesson not paid for
        if (l.isPaid())
            lessonViewHolder.durationIcon.setColorFilter(lessonImageColor);
        else
            lessonViewHolder.durationIcon.setColorFilter(red);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send studentID and lessonID to activity
                onLessonDetailClick(
                        ((Student) getGroup(groupPosition)).getId(),
                        ((Lesson) getChild(groupPosition, childPosition)).getId());
            }
        });

        return convertView;
    }

    static class LessonViewHolder {
        TextView lessonDateView;
        TextView lessonDurationView;
        TextView topicView;
        TextView lessonHomeworkView;
        ImageView durationIcon;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


}


