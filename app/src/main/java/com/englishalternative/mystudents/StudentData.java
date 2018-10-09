package com.englishalternative.mystudents;

import android.content.Context;
import com.englishalternative.mystudents.framework.Database;
import com.englishalternative.mystudents.framework.implementation.AndroidDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class should act as an intermediate between Activities
 * and database / internet database / human brain interface
 */

public class StudentData {

    private Database db;
    private static List<Student> students;
    private static StudentData studentDataInstance;


    public static synchronized StudentData getInstance(Context context) {
        // If instance of sData doesn't exist,
        // create a new one using application context
        // not to leak a context
        if (studentDataInstance == null)
            studentDataInstance = new StudentData(context.getApplicationContext());

        return studentDataInstance;

    }


    private StudentData(Context context) {

        // Get instance of database and load students.
        // MAYBE LATER: get students from internet database, synchronize etc.
        db = AndroidDatabase.getInstance(context);
        updateStudentList();
        if (students.isEmpty()) {
            students = new ArrayList<>();
        }
    }


    public List<Student> getAllStudents() {
        return new ArrayList<Student>(students);
    }

    public Student getStudent(int id) {
        //Find student by ID and return, else null
        for (Student s : students) {
            if (s.getId() == id)
                return s;
        }
        return null;
    }

    public Lesson getLesson (int id) {
        // Slower search for lessons
        for (Student s : students) {
            if (s.getLessons() == null)
                updateStudentLessons(s);
            for (Lesson l : s.getLessons()) {
                if (l.getId() == id) {
                    return l;
                }
            }
        }
        return null;
    }

    public Lesson getLesson (int id, int studentID) {
        // Search students for lesson with matching id

        for (Student s : students) {
            if (studentID == s.getId()) {
                if (s.getLessons() == null)
                    updateStudentLessons(s);
                for (Lesson l : s.getLessons()) {
                    if (l.getId() == id) {
                        return l;
                    }
                }
            }
        }
        return null;
    }

    public boolean updateStudent(final Student student) {
        // Update student in database

        db.updateStudent(student);
        updateStudentList();

        return true;
    }

    public boolean deleteLesson(final Lesson lesson) {
        // Delete student in database

        db.deleteLesson(lesson);
        updateStudentList();

        return true;
    }

    public boolean updateLesson(final Lesson lesson) {
        // Update lesson in database

        db.updateLesson(lesson);
        updateStudentLessons(getStudent(lesson.getStudentID()));

        return true;
    }

    public boolean addStudent(final Student student) {
        // Add student to database

        db.addStudent(student);
        updateStudentList();

        return true;
    }

    public boolean addLesson(final Lesson lesson) {
        // Add lesson to database

        db.addLesson(lesson);
        updateStudentLessons(getStudent(lesson.getStudentID()));

        return true;
    }

    public boolean deleteStudent(final Student s) {
        // Delete student from database

        db.deleteStudent(s);
        updateStudentList();

        return true;
    }

    private void updateStudentList() {
        // Get updated student list

        students = db.getAllStudents();

    }

    public void updateStudentLessons(final Student student) {
        // Update student's lesson list
        List<Lesson> lessonList = db.getLessonsStudent(student);
        student.setLessons(lessonList);
    }

    public void updateAllStudentLessons() {
        // Update all students' lessons
        for (Student student : students) {
            List<Lesson> lessonList = db.getLessonsStudent(student);
            student.setLessons(lessonList);
        }
    }

    public void close() {
        db.close();
    }

}
