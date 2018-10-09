package com.englishalternative.mystudents.framework;

import com.englishalternative.mystudents.Student;
import com.englishalternative.mystudents.Lesson;


import java.util.List;

/**
 * Created by Roman on 22.02.2017.
 */

public interface Database {

    public Student getStudent(int id);

    public List<Lesson> getLessonsStudent(Student S);

    public void updateStudent(Student s);

    public void updateLesson(Lesson l);

    public List<Student> getAllStudents();

    public boolean deleteStudent(Student s);

    public boolean deleteLesson(Lesson l);

    public boolean addLesson(Lesson l);

    public boolean addStudent(Student s);

    public void close();

}
