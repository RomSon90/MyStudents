package com.englishalternative.mystudents;

import java.util.List;

/**
 * Created by Roman on 23.02.2017.
 */

public class Student {


    private String name;
    private String place;
    private String clss;
    private boolean[] date;
    private int id;

    private int price;
    private int duration;

    private List<Lesson> lessons;

    public Student (int id, String name, String place, String clss, boolean[] date, int price, int duration) {
        this.clss = clss;
        this.date=date;
        this.name=name;
        this.place=place;
        this.id=id;
        this.price=price;
        this.duration=duration;
    }

    public Student (String name, String place, String clss, boolean[] date, int price, int duration) {
        this.id = -1;
        this.clss = clss;
        this.date=date;
        this.name=name;
        this.place=place;
        this.price=price;
        this.duration=duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getClss() {
        return clss;
    }

    public void setClss(String clss) {
        this.clss = clss;
    }

    public boolean[] getDate() {
        return date;
    }

    public void setDate(boolean[] date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public int getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        return id == student.id;

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + id;
        return result;
    }
}
