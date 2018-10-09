package com.englishalternative.mystudents;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Roman on 26.02.2017.
 */

public class Lesson {


    private String date;
    private String student;
    private String place;
    private String clss;
    private String homework;
    private String topic;
    private long dateUnix;

    private int price;
    private boolean paid;
    private int duration;

    private int id;
    private int studentID;

    private boolean hwDone;

    // NEW CONSTRUCTOR WITH UNIX DATE
    public Lesson (int id,
                   Student student,
                   String hWork,
                   boolean hwDone,
                   String topic,
                   int price,
                   boolean paid,
                   int duration,
                   long dateUnix){
        this.id=id;
        this.student=student.getName();
        this.place=student.getPlace();
        this.clss=student.getClss();
        this.homework=hWork;
        this.hwDone=hwDone;
        this.studentID=student.getId();
        this.topic=topic;
        this.price=price;
        this.paid=paid;
        this.duration=duration;
        this.dateUnix = dateUnix;
        this.date = dateToString(new Date(dateUnix));
    }

    public Lesson (Student student,
                   String hWork,
                   boolean hwDone,
                   String topic,
                   int price,
                   boolean paid,
                   int duration,
                   long dateUnix) {
        this.id=-1;
        this.student=student.getName();
        this.place=student.getPlace();
        this.clss=student.getClss();
        this.homework=hWork;
        this.hwDone=hwDone;
        this.studentID=student.getId();
        this.topic=topic;
        this.price=price;
        this.paid=paid;
        this.duration=duration;
        this.dateUnix = dateUnix;
        this.date = dateToString(new Date(dateUnix));
    }

    public String getDate() {
        return date;
    }

    public String getStudent() {
        return student;
    }

    public String getPlace() {
        return place;
    }

    public String getClss() {
        return clss;
    }

    public String getHomework() {
        return homework;
    }

    public int getId() {
        return id;
    }

    public boolean isHwDone() {
        return hwDone;
    }

    public int getStudentID() {
        return studentID;
    }

    public String getTopic() {
        return topic;
    }

    public int getPrice() {
        return price;
    }

    public boolean isPaid() {
        return paid;
    }

    public int getDuration() {
        return duration;
    }

    public long getDateUnix() {
        return dateUnix;
    }

    public static Date stringToDate(String date) {
        // Convert String from db/whatever to Date object for reasons
        Log.d("Lesson", date);
        try {
            Date dateFromString = SimpleDateFormat.getDateInstance().parse(date);
            return dateFromString;
        } catch (ParseException ex) {
            Log.e("Lesson Format time", ex.getMessage());
            return new Date();
        }
    }

    public static String dateToString(Date date) {
        // Convert date object to String for database storage etc.
        return SimpleDateFormat.getDateInstance().format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lesson lesson = (Lesson) o;

        return id == lesson.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getDurationConverted() {

        int hours = duration / 60;
        int minutes = duration - hours*60;
        return String.format(Locale.US, "%02d h : %02d m", hours, minutes);
    }
}
