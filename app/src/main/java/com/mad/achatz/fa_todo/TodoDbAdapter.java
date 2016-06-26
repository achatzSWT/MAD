package com.mad.achatz.fa_todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TodoDbAdapter {

    private static final String DATABASE_NAME = "todo.db";
    private static final String TABLE_TODOS = "todos";
    private static final int DATABASE_VERSION = 1;

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DONE = "isdone";
    private static final String COLUMN_FAVORITE = "isfavorite";
    private static final String COLUMN_DUEDATE = "duedate";
    private static final String COLUMN_CONTACTS = "contacts";

    private DatabaseHelper dbHelper;
    private Context context;

    private SQLiteDatabase db;

    private class DatabaseHelper extends SQLiteOpenHelper {

        private final String createCommand =
                "CREATE TABLE " + TABLE_TODOS + " " + "(" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_DESCRIPTION + " TEXT, " +
                        COLUMN_DONE + " BOOLEAN NOT NULL, " +
                        COLUMN_FAVORITE + " BOOLEAN NOT NULL, " +
                        COLUMN_DUEDATE + " DATETIME NOT NULL, " +
                        COLUMN_CONTACTS + " TEXT);";

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(createCommand);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

    public TodoDbAdapter(Context context) {
        this.context = context;
        open();
    }

    public void open() {
        if (db != null && db.isOpen()) return;
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
        dbHelper.close();
    }

    public boolean isClosed() {
        return !db.isOpen();
    }

    /**
     * Aktualisiere ein ToDo in der Datenbank
     *
     * @return True wenn erfolgreich, ansonsten false
     */
    public boolean updateTodoInDb(ToDo todo) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(COLUMN_NAME, todo.getName());
        updateValues.put(COLUMN_DESCRIPTION, todo.getDescription());
        updateValues.put(COLUMN_DONE, todo.isDone());
        updateValues.put(COLUMN_FAVORITE, todo.isFavourite());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetimeString = dateFormat.format(todo.getDueDate().getTime());
        updateValues.put(COLUMN_DUEDATE, datetimeString);
        updateValues.put(COLUMN_CONTACTS, integerListToString(todo.getContactIds()));
        int result = db.update(TABLE_TODOS, updateValues, COLUMN_ID + "=" + todo.getDbId(), null);
        return (result > 0);
    }

    /**
     * Füge Todo in Datenbank ein und schreibe die von der Datenbank vergebene Id in das Todo.
     *
     * @param todo  Das Todo
     * @return Datenbank ID des eingefügten Todo oder -1 bei Fehler.
     */
    public long insertTodo(ToDo todo) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_NAME, todo.getName());
        initialValues.put(COLUMN_DESCRIPTION, todo.getDescription());
        initialValues.put(COLUMN_DONE, todo.isDone());
        initialValues.put(COLUMN_FAVORITE, todo.isFavourite());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetimeString = dateFormat.format(todo.getDueDate().getTime());
        initialValues.put(COLUMN_DUEDATE, datetimeString);
        initialValues.put(COLUMN_CONTACTS, integerListToString(todo.getContactIds()));
        long dbId = db.insert(TABLE_TODOS, null, initialValues);
        todo.setDbId(dbId);
        return dbId;
    }

    /**
     * Hole alle Todos aus der Datenbank.
     *
     * @param todos ArrayList in welche die Todos geschrieben werden sollen. Wird vorher geleert.
     * @return Die übergebene Arraylist.
     */
    public ArrayList<ToDo> getAllTodos(ArrayList<ToDo> todos) {
        Cursor c = db.query(TABLE_TODOS, null, null, null, null, null, COLUMN_NAME);

        if (todos == null) todos = new ArrayList<ToDo>();
        todos.clear();

        if (c.moveToFirst()) {
            int colId = c.getColumnIndex(COLUMN_ID);
            int colName = c.getColumnIndex(COLUMN_NAME);
            int colDesc = c.getColumnIndex(COLUMN_DESCRIPTION);
            int colDone = c.getColumnIndex(COLUMN_DONE);
            int colFav = c.getColumnIndex(COLUMN_FAVORITE);
            int colDue = c.getColumnIndex(COLUMN_DUEDATE);
            int colContacts = c.getColumnIndex(COLUMN_CONTACTS);

            do {
                ToDo todo = new ToDo();

                todo.setDbId(c.getLong(colId));
                todo.setName(c.getString(colName));
                todo.setDescription(c.getString(colDesc));
                todo.setDone(c.getInt(colDone) > 0);
                todo.setFavourite(c.getInt(colFav) > 0);

                String dateTime = c.getString(colDue);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = dateFormat.parse(dateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                todo.setDueDate(cal);

                todo.setContactIds(stringToIntegerList(c.getString(colContacts)));

                todos.add(todo);
            } while (c.moveToNext());
        }

        c.close();

        return todos;
    }

    /**
     * Lösche Todo aus Datenbank
     */
    public void deleteTodo(ToDo todo) {
        db.delete(TABLE_TODOS, COLUMN_ID + "=" + todo.getDbId(), null);
    }

    private String integerListToString(ArrayList<Integer> integerList) {
        if (integerList == null)
            return null;
        ArrayList<String> strings = new ArrayList<>();
        for (int i : integerList) {
            strings.add(Integer.toString(i));
        }

        return TextUtils.join(",", strings);
    }

    private ArrayList<Integer> stringToIntegerList(String string) {
        if (string == null)
            return null;
        String[] strings = string.split(",");
        ArrayList<Integer> integers = new ArrayList<>();
        for (String s : strings) {
            try {
                integers.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
            }
        }
        return integers;
    }
}
