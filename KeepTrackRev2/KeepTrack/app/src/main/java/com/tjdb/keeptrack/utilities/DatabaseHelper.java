package com.tjdb.keeptrack.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tjdb.keeptrack.model.Assessment;
import com.tjdb.keeptrack.model.Course;
import com.tjdb.keeptrack.model.Note;
import com.tjdb.keeptrack.model.Term;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "ToDo_Table";
    private static final String COL1 = "ID";
    private static final String COL2 = "Name";
    private static final String COL3 = "Date";
    private static final String COL4 = "Time";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTermTable = "CREATE TABLE terms ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "startdate DATE, " +
                "enddate DATE);";
        Log.d(TAG, "Creating table " + createTermTable);
        db.execSQL(createTermTable);

        String createCourseTable = "CREATE TABLE courses ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "startdate DATE, " +
                "enddate TIME, " +
                "status TEXT, " +
                "instructor_name TEXT, " +
                "instructor_phone TEXT, " +
                "instructor_email TEXT, " +
                "term_id INTEGER, " +
                "FOREIGN KEY (term_id) REFERENCES terms(id)" +
                ");";
        Log.d(TAG, "Creating table " + createCourseTable);
        db.execSQL(createCourseTable);

        String createNoteTable = "CREATE TABLE notes ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "text TEXT, " +
                "course_id INTEGER, " +
                "FOREIGN KEY (course_id) REFERENCES courses(id)" +
                ");";
        Log.d(TAG, "Creating table " + createNoteTable);
        db.execSQL(createNoteTable);

        String createAssessmentTable = "CREATE TABLE assessments ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "startdate DATE, " +
                "enddate TIME, " +
                "objective TEXT, " +
                "performance TEXT, " +
                "course_id INTEGER, " +
                "FOREIGN KEY (course_id) REFERENCES courses(id)" +
                ");";
        Log.d(TAG, "Creating table " + createAssessmentTable);
        db.execSQL(createAssessmentTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS courses;");
        db.execSQL("DROP TABLE IF EXISTS terms;");
        onCreate(db);
    }

    public boolean insertTerm(String title, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("startdate", startDate);
        contentValues.put("enddate", endDate);
        Log.d(TAG, "insertData: Inserting " + title + " to terms");
        long result = db.insert("terms", null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean updateTerm(long id, String title, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("title", title);
        contentValues.put("startdate", startDate);
        contentValues.put("enddate", endDate);
        Log.d(TAG, "insertData: Inserting " + title + " to terms");
        long result = db.update("terms", contentValues, "id="+id, null);
        db.close();
        return result != -1;
    }

    public void deleteTerm(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String query = "DELETE FROM terms WHERE " +
                    "id = " + id + ";";
            db.execSQL(query);
            Log.d(TAG, "deleteItem: " + query);
            Log.d(TAG, "deleteData: Deleted " + id + " from database");
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public ArrayList<Term> getAllTerms() {
        ArrayList<Term> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM terms";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Long id = cursor.getLong(0);
            String title = cursor.getString(1);
            String startDate = cursor.getString(2);
            String endDate = cursor.getString(3);
            Term term = new Term(id, title, startDate, endDate);
            arrayList.add(term);
        }
        db.close();
        return arrayList;
    }

    public Term getTermById(long termId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM terms where id = " + termId + ";";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToNext();

        Long id = cursor.getLong(0);
        String title = cursor.getString(1);
        String startDate = cursor.getString(2);
        String endDate = cursor.getString(3);
        Term term = new Term(id, title, startDate, endDate);

        db.close();
        return term;
    }

    public ArrayList<Course> getAllCourses(long termId) {
        ArrayList<Course> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM courses where term_id="+termId+";";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Long id = cursor.getLong(0);
            String title = cursor.getString(1);
            String startDate = cursor.getString(2);
            String endDate = cursor.getString(3);
            String status = cursor.getString(4);
            String instructorName = cursor.getString(5);
            String instructorPhone = cursor.getString(6);
            String instructorEmail = cursor.getString(7);
            //Long termId = cursor.getLong(8);
            Course course = new Course(id, title, startDate, endDate, status, instructorName, instructorPhone, instructorEmail, termId);
            arrayList.add(course);
        }
        db.close();
        return arrayList;
    }

    public long insertCourse(String title, String startDate, String endDate,String status, String instructorName,String instructorPhone,String instructorEmail, long termId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("startdate", startDate);
        contentValues.put("enddate", endDate);
        contentValues.put("status", status);
        contentValues.put("instructor_name", instructorName);
        contentValues.put("instructor_phone", instructorPhone);
        contentValues.put("instructor_email", instructorEmail);
        contentValues.put("term_id", termId);
        Log.d(TAG, "insertData: Inserting " + title + " to courses");
        long result = db.insert("courses", null, contentValues);
        db.close();
        return result;
    }

    public boolean updateCourse(long id, String title, String startDate, String endDate,String status, String instructorName,String instructorPhone,String instructorEmail, long termId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("startdate", startDate);
        contentValues.put("enddate", endDate);
        contentValues.put("status", status);
        contentValues.put("instructor_name", instructorName);
        contentValues.put("instructor_phone", instructorPhone);
        contentValues.put("instructor_email", instructorEmail);
        contentValues.put("term_id", termId);
        Log.d(TAG, "insertData: Inserting " + title + " to courses");
        long result = db.update("courses", contentValues, "id="+id, null);
        db.close();
        return result != -1;
    }

    public void deleteCourse(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String query = "DELETE FROM courses WHERE " +
                    "id = " + id + ";";
            db.execSQL(query);
            Log.d(TAG, "deleteItem: " + query);
            Log.d(TAG, "deleteData: Deleted " + id + " from database");
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public Course getCourseById(long courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM courses where id = " + courseId + ";";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToNext();

        Long id = cursor.getLong(0);
        String title = cursor.getString(1);
        String startDate = cursor.getString(2);
        String endDate = cursor.getString(3);
        String status = cursor.getString(4);
        String instructorName = cursor.getString(5);
        String instructorPhone = cursor.getString(6);
        String instructorEmail = cursor.getString(7);
        Long termId = cursor.getLong(8);
        Course course = new Course(id, title, startDate, endDate, status, instructorName, instructorPhone, instructorEmail, termId);

        db.close();
        return course;
    }


    public boolean insertNote(String text, long courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("text", text);
        contentValues.put("course_id", courseId);
        Log.d(TAG, "insertData: Inserting " + text + " to notes");
        long result = db.insert("notes", null, contentValues);
        db.close();
        return result != -1;
    }

    public ArrayList<Note> getAllNotes(long courseId) {
        ArrayList<Note> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM notes where course_id="+courseId+";";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Long id = cursor.getLong(0);
            String text = cursor.getString(1);
            Note note = new Note(id, text, courseId);
            arrayList.add(note);
        }
        db.close();
        return arrayList;
    }

    public long insertAssessment(String title, String startDate, String endDate, String objective, String performance, long courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("startdate", startDate);
        contentValues.put("enddate", endDate);
        contentValues.put("objective", objective);
        contentValues.put("performance", performance);
        contentValues.put("course_id", courseId);
        Log.d(TAG, "insertData: Inserting " + title + " to assessments");
        long result = db.insert("assessments", null, contentValues);
        db.close();
        return result;
    }

    public boolean updateAssessment(long id, String title, String startDate, String endDate, String objective, String performance, long courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("title", title);
        contentValues.put("startdate", startDate);
        contentValues.put("enddate", endDate);
        contentValues.put("objective", objective);
        contentValues.put("performance", performance);
        contentValues.put("course_id", courseId);
        Log.d(TAG, "insertData: Inserting " + title + " to assessments");
        long result = db.update("assessments", contentValues, "id="+id, null);
        db.close();
        return result != -1;
    }

    public ArrayList<Assessment> getAllAssessments(long courseId) {
        ArrayList<Assessment> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM assessments where course_id="+courseId+";";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Long id = cursor.getLong(0);
            String title = cursor.getString(1);
            String startDate = cursor.getString(2);
            String endDate = cursor.getString(3);
            String objective = cursor.getString(4);
            String performance = cursor.getString(5);
            Assessment assessment = new Assessment(id, title, startDate, endDate, objective, performance, courseId);
            arrayList.add(assessment);
        }
        db.close();
        return arrayList;
    }

    public Assessment getAssessmentById(long assessmentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM assessments where id = " + assessmentId + ";";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToNext();

        Long id = cursor.getLong(0);
        String title = cursor.getString(1);
        String startDate = cursor.getString(2);
        String endDate = cursor.getString(3);
        String objective = cursor.getString(4);
        String performance = cursor.getString(5);
        Long courseId = cursor.getLong(6);
        Assessment assessment = new Assessment(id, title, startDate, endDate, objective, performance, courseId);

        db.close();
        return assessment;
    }

    public void deleteAssessment(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String query = "DELETE FROM assessments WHERE " +
                    "id = " + id + ";";
            db.execSQL(query);
            Log.d(TAG, "deleteItem: " + query);
            Log.d(TAG, "deleteData: Deleted " + id + " from database");
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }





}
