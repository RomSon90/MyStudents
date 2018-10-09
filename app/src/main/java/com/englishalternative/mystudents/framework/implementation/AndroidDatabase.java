package com.englishalternative.mystudents.framework.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.englishalternative.mystudents.framework.Database;
import com.englishalternative.mystudents.Student;
import com.englishalternative.mystudents.Lesson;

import java.util.ArrayList;
import java.util.List;


/**
 * SQlite database holding local information about students and lessons
 */

public class AndroidDatabase extends SQLiteOpenHelper implements Database {

    private static AndroidDatabase sInstance;

    private static final String DB_NAME = "my_students_database";
    private static int DB_VERSION = 1;

    private SQLiteDatabase db;
    private Cursor cursor;


    public static synchronized AndroidDatabase getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new AndroidDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    private AndroidDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = this.getWritableDatabase();

    }

    /* Cursor cursor = sqLiteDatabase.query(
    tableName, tableColumns, whereClause, whereArgs, groupBy, having, orderBy);
    */

    @Override
    public Student getStudent(int id) {

        // Get single student out of db, if no such student return null
        Student s = null;
        cursor = db.query("STUDENTS", null, "_id = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor.moveToFirst()) {

            s = new Student(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), intToBoolean(cursor.getInt(4)),
                    cursor.getInt(5), cursor.getInt(6));
            s.setLessons(getLessonsStudent(s));
            cursor.close();
        }

        return s;
    }


    @Override
    public List<Lesson> getLessonsStudent(Student s) {
        String[] where = {String.valueOf(s.getId())};

        cursor = db.query("LESSONS", null, "STUDENT_ID = ?", where,
                null, null, "DATE_UNIX DESC");

        List<Lesson> lessonsStudent = new ArrayList<>();
        while (cursor.moveToNext()) {
            Lesson l = new Lesson(
                    cursor.getInt(0), //id
                    s, //student
                    cursor.getString(1), //hWork
                    (cursor.getInt(2) == 1), //hwDone
                    cursor.getString(3), //topic
                    cursor.getInt(4), //price
                    (cursor.getInt(5) == 1), //paid
                    cursor.getInt(6), //duration
                    cursor.getLong(8) // unix date
                    );
            lessonsStudent.add(l);
        }

        cursor.close();
        return lessonsStudent;

    }


    @Override
    public boolean deleteStudent(Student s) {
        //Get all student lessons and delete them
        List<Lesson> lessons = getLessonsStudent(s);
        for (Lesson l : lessons)
            deleteLesson(l);
        //Delete the student
        db.delete("STUDENTS", "_id = ? AND NAME = ?", new String[]{
                Integer.toString(s.getId()), s.getName()});

        return true;
    }

    @Override
    public boolean deleteLesson(Lesson l) {
        db.delete("LESSONS", "_id = ?", new String[]{
                String.valueOf(l.getId())});
        return true;
    }

    @Override
    public boolean addLesson(Lesson l) {
        ContentValues c = new ContentValues();
        c.put("HOMEWORK", l.getHomework());
        c.put("HOMEWORK_DONE", l.isHwDone());
        c.put("STUDENT_ID", l.getStudentID());
        c.put("TOPIC", l.getTopic());
        c.put("PRICE", l.getPrice());
        c.put("PAID", l.isPaid()?1:0);
        c.put("DURATION", l.getDuration());
        c.put("DATE_UNIX", l.getDateUnix());
        db.insert("LESSONS", null, c);
        return true;

    }

    @Override
    public boolean addStudent(Student s) {
        ContentValues c = new ContentValues();
        c.put("NAME", s.getName());
        c.put("PLACE", s.getPlace());
        c.put("CLASS", s.getClss());
        c.put("DATE", booleansToInt(s.getDate()));
        c.put("PRICE", s.getPrice());
        c.put("DURATION", s.getDuration());
        db.insert("STUDENTS", null, c);
        return true;
    }

    @Override
    public List<Student> getAllStudents() {

        cursor = db.query("STUDENTS", null, null, null, null, null, "NAME");
        List<Student> studentsList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Student s = new Student(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    intToBoolean(cursor.getInt(4)),
                    cursor.getInt(5),
                    cursor.getInt(6));
            studentsList.add(s);
        }
        cursor.close();
        return studentsList;

    }

    @Override
    public void updateStudent(Student s) {

        //create values to replace old student
        ContentValues c = new ContentValues();
        c.put("NAME", s.getName());
        c.put("PLACE", s.getPlace());
        c.put("CLASS", s.getClss());
        c.put("DATE", booleansToInt(s.getDate()));
        c.put("PRICE", s.getPrice());
        c.put("DURATION", s.getDuration());


        //replace student with matching id
        db.update("STUDENTS", c, "_id = ?", new String[]{Integer.toString(s.getId())});
    }

    @Override
    public void updateLesson(Lesson l) {
        // Create content values
        ContentValues c = new ContentValues();
        c.put("HOMEWORK", l.getHomework());
        c.put("HOMEWORK_DONE", l.isHwDone() ? 1:0);
        c.put("TOPIC", l.getTopic());
        c.put("PRICE", l.getPrice());
        c.put("PAID", l.isPaid() ? 1:0);
        c.put("DURATION", l.getDuration());
        c.put("STUDENT_ID", l.getStudentID());
        c.put("DATE_UNIX", l.getDateUnix());

        // Replace lesson with matching id
        db.update("LESSONS", c, "_id = ?", new String[]{Integer.toString(l.getId())});
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create new database
        db.execSQL("CREATE TABLE LESSONS (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "HOMEWORK TEXT, " +
                "HOMEWORK_DONE INTEGER, " +
                "TOPIC TEXT, " +
                "PRICE INTEGER, " +
                "PAID INTEGER, " +
                "DURATION INTEGER, " +
                "STUDENT_ID INTEGER, " +
                "DATE_UNIX INTEGER);");

        db.execSQL("CREATE TABLE STUDENTS (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT, " +
                "PLACE TEXT, " +
                "CLASS TEXT, " +
                "DATE INTEGER, " +
                "PRICE INTEGER, " +
                "DURATION INTEGER);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    /* Everything is created correctly in onCreate()

        if (oldVersion < 2) {
            // Add column UNIX_TIME and convert all dates to unix format
            db.execSQL("ALTER TABLE LESSONS ADD COLUMN DATE_UNIX INTEGER;");
            Log.d("DATABASE", "Unix date added");
            cursor = db.query("LESSONS", null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String d = cursor.getString(1);
                Long unixD = convertTime(d);
                ContentValues values = new ContentValues();
                values.put("DATE_UNIX", unixD);
                db.update("LESSONS", values, "_id = ?", new String[] {Integer.toString(cursor.getInt(0))});
                Log.d("UPDATE", "lesson updated id: " + cursor.getInt(0));
            }
        }

        if (oldVersion < 3) {
            Log.d("Database", "removing DATE column from LESSONS");
            db.execSQL("CREATE TABLE LESSONS_TMP (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "HOMEWORK TEXT, " +
                    "HOMEWORK_DONE INTEGER, " +
                    "TOPIC STRING, " +
                    "PRICE INTEGER, " +
                    "PAID INTEGER, " +
                    "DURATION INTEGER, " +
                    "STUDENT_ID INTEGER, " +
                    "DATE_UNIX INTEGER);");
            db.execSQL("INSERT INTO LESSONS_TMP SELECT " +
                    "_id, " +
                    "HOMEWORK, " +
                    "HOMEWORK_DONE, " +
                    "TOPIC, " +
                    "PRICE, " +
                    "PAID, " +
                    "DURATION, " +
                    "STUDENT_ID, " +
                    "DATE_UNIX FROM LESSONS");
            db.execSQL("DROP TABLE IF EXISTS LESSONS");
            db.execSQL("ALTER TABLE LESSONS_TMP RENAME TO LESSONS");
            Log.d("DATABASE", "RMOVAL COMPLETE");
        }

        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE STUDENTS ADD COLUMN PRICE INTEGER;");
            db.execSQL("ALTER TABLE STUDENTS ADD COLUMN DURATION INTEGER;");

        }
      */
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void close() {
        cursor.close();
        db.close();
        sInstance = null;
    }

    private int booleansToInt(boolean[] arr) {
        int n = 0;

        //move bits one left and copy the value of boolean[current]
        for (boolean b : arr)
            n = (n << 1) | (b ? 1 : 0);
        return n;
    }

    private boolean[] intToBoolean(int rawBool) {
        //copy the value of rightmost bit and store it as boolean on last position,
        //then move the bits one right. Repeat seven times.
        boolean[] daysOfWeek = new boolean[7];
        int temp;
        for (int i = 0; i < 7; i++) {
            temp = rawBool & 1;
            daysOfWeek[6 - i] = temp == 1;
            rawBool = rawBool >>> 1;
        }
        return daysOfWeek;
    }
}