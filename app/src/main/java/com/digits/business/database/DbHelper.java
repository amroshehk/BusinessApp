package com.digits.business.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.digits.business.database.CallerContract.CallerEntry;
import com.digits.business.entities.Caller;
import com.digits.business.database.LogContract.LogEntry;
import com.digits.business.entities.Log;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {


    public static final String LOG_TAG = DbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "digits.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;


    private static final String ENCODING_SETTING = "PRAGMA encoding ='windows-1256'";

    /**
     * Constructs a new instance of {@link DbHelper}.
     *
     * @param context of the app
     */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the Caller History table
        /*String SQL_CREATE_CALLER_HISTORY_TABLE =
                "CREATE TABLE " + CallerEntry.TABLE_NAME + " ("
                        + CallerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + CallerEntry.COLUMN_CALLER_NAME + " TEXT , "
                        + CallerEntry.COLUMN_CALLER_NUMBER + " TEXT  NOT NULL, "
                        + CallerEntry.COLUMN_CALLER_TIMESTAMP + " TEXT  NOT NULL, "
                        + CallerEntry.COLUMN_CALLER_TYPE + " TEXT NOT NULL); ";*/


        // Create a String that contains the SQL statement to create the Caller History table
        String SQL_CREATE_LOG_HISTORY_TABLE =
                "CREATE TABLE " + LogEntry.TABLE_NAME + " ("
                        + LogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + LogEntry.COLUMN_CALLER_NAME + " TEXT , "
                        + LogEntry.COLUMN_CALLER_NUMBER + " TEXT  NOT NULL, "
                        + LogEntry.COLUMN_CALL_TIMESTAMP + " TEXT  NOT NULL, "
                        + LogEntry.COLUMN_EMAIL + " TEXT  NOT NULL, "
                        + LogEntry.COLUMN_CALL_DURATION + " TEXT  NOT NULL, "
                        + LogEntry.COLUMN_CALLER_TYPE + " INTEGER NOT NULL); ";


        // Execute the SQL statements
        //db.execSQL(SQL_CREATE_CALLER_HISTORY_TABLE);

        db.execSQL(SQL_CREATE_LOG_HISTORY_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older tables if existed
        //db.execSQL("DROP TABLE IF EXISTS " + CallerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LogEntry.TABLE_NAME);

        // Create tables again
        onCreate(db);

    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        if (!db.isReadOnly()) {
            db.execSQL(ENCODING_SETTING);
        }
    }


   /* *//**
     * Adding new Caller
     *
     * @param caller
     *//*
    public void saveCaller(Caller caller) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CallerEntry.COLUMN_CALLER_NAME , caller.getName());
        values.put(CallerEntry.COLUMN_CALLER_NUMBER, caller.getNumber());
        values.put(CallerEntry.COLUMN_CALLER_TIMESTAMP, caller.getTimestamp());
        values.put(CallerEntry.COLUMN_CALLER_TYPE, caller.getType());

        db.insert(CallerEntry.TABLE_NAME, null, values); // Inserting Row
        db.close(); // Closing database connection
    }*/



    /**
     * Adding new Log Entry
     *
     * @param log
     */
    public void saveLogEntry(Log log) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(LogEntry.COLUMN_CALLER_NAME , log.getName());
        values.put(LogEntry.COLUMN_EMAIL , log.getEmail());
        values.put(LogEntry.COLUMN_CALLER_TYPE , log.getType());
        values.put(LogEntry.COLUMN_CALL_DURATION , log.getDuration());
        values.put(LogEntry.COLUMN_CALLER_NUMBER , log.getPhone_no());
        values.put(LogEntry.COLUMN_CALL_TIMESTAMP , log.getTimestamp());

        db.insert(LogEntry.TABLE_NAME, null, values); // Inserting Row
        db.close(); // Closing database connection
    }


    /**
     * Getting single Caller
     *
     * @param id
     * @return caller
     *//*
    public Caller getCaller(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                CallerEntry._ID,
                CallerEntry.COLUMN_CALLER_NAME,
                CallerEntry.COLUMN_CALLER_NUMBER,
                CallerEntry.COLUMN_CALLER_TIMESTAMP,
                CallerEntry.COLUMN_CALLER_TYPE
        };

        // Filter results WHERE "_ID" = 'id'
        String selection = CallerEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        // Perform a query on the callers table
        Cursor cursor = db.query(
                CallerEntry.TABLE_NAME,   // The table to query
                projection,                // The columns to return
                selection,                 // The columns for the WHERE clause
                selectionArgs,             // The values for the WHERE clause
                null,              // Don't group the rows
                null,               // Don't filter by row groups
                null);            // The sort order


        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(CallerEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_NAME);
        int numberColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_NUMBER);
        int timestampColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_TIMESTAMP);
        int typeColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_TYPE);

        Caller caller = new Caller();
        caller.setId(Integer.parseInt(cursor.getString(idColumnIndex)));
        caller.setName(cursor.getString(nameColumnIndex));
        caller.setNumber(cursor.getString(numberColumnIndex));
        caller.setTimestamp(Long.parseLong(cursor.getString(timestampColumnIndex)));
        caller.setType(cursor.getString(typeColumnIndex));

        cursor.close();

        return caller;
    }*/



    /**
     * Getting single Log Entry
     *
     * @param id
     * @return logEntry
     */
    public Log getLogEntry(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LogEntry._ID,
                LogEntry.COLUMN_CALLER_NAME,
                LogEntry.COLUMN_CALLER_NUMBER,
                LogEntry.COLUMN_CALL_TIMESTAMP,
                LogEntry.COLUMN_CALLER_TYPE,
                LogEntry.COLUMN_EMAIL,
                LogEntry.COLUMN_CALL_DURATION
        };

        // Filter results WHERE "_ID" = 'id'
        String selection = LogEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        // Perform a query on the log table
        Cursor cursor = db.query(
                LogEntry.TABLE_NAME,   // The table to query
                projection,                // The columns to return
                selection,                 // The columns for the WHERE clause
                selectionArgs,             // The values for the WHERE clause
                null,              // Don't group the rows
                null,               // Don't filter by row groups
                null);            // The sort order


        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(LogEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_CALLER_NAME);
        int phoneNoColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_CALLER_NUMBER);
        int timestampColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_CALL_TIMESTAMP);
        int typeColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_CALLER_TYPE);
        int durationColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_CALL_DURATION);
        int emailColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_EMAIL);

        Log logEntry = new Log();
        logEntry.setId(Integer.parseInt(cursor.getString(idColumnIndex)));
        logEntry.setName(cursor.getString(nameColumnIndex));
        logEntry.setPhone_no(cursor.getString(phoneNoColumnIndex));
        logEntry.setTimestamp(Long.parseLong(cursor.getString(timestampColumnIndex)));
        logEntry.setType(Integer.parseInt(cursor.getString(typeColumnIndex)));
        logEntry.setDuration(Long.parseLong(cursor.getString(durationColumnIndex)));
        logEntry.setEmail(cursor.getString(emailColumnIndex));

        cursor.close();

        return logEntry;
    }





    /**
     * Getting single Caller by name
     *
     * @param name
     * @return caller
     *//*
    public Caller getCallerByName(String name) {

        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                CallerEntry._ID,
                CallerEntry.COLUMN_CALLER_NAME,
                CallerEntry.COLUMN_CALLER_NUMBER,
                CallerEntry.COLUMN_CALLER_TIMESTAMP,
                CallerEntry.COLUMN_CALLER_TYPE
        };

        // Filter results WHERE "_ID" = 'id'
        String selection = CallerEntry.COLUMN_CALLER_NAME + " = ?";
        String[] selectionArgs = {name};

        // Perform a query on the products table
        Cursor cursor = db.query(
                CallerEntry.TABLE_NAME,   // The table to query
                projection,                // The columns to return
                selection,                 // The columns for the WHERE clause
                selectionArgs,             // The values for the WHERE clause
                null,              // Don't group the rows
                null,               // Don't filter by row groups
                null);            // The sort order


        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(CallerEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_NAME);
        int numberColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_NUMBER);
        int timestampColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_TIMESTAMP);
        int typeColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_TYPE);

        Caller caller = new Caller();
        caller.setId(Integer.parseInt(cursor.getString(idColumnIndex)));
        caller.setName(cursor.getString(nameColumnIndex));
        caller.setNumber(cursor.getString(numberColumnIndex));
        caller.setTimestamp(Long.parseLong(cursor.getString(timestampColumnIndex)));
        caller.setType(cursor.getString(typeColumnIndex));

        cursor.close();

        return caller;
    }
*/



   /* *//**
     * Getting single Caller by number
     *
     * @param number
     * @return product
     *//*
    public Caller getCallerByNumber(String number) {

        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                CallerEntry._ID,
                CallerEntry.COLUMN_CALLER_NAME,
                CallerEntry.COLUMN_CALLER_NUMBER,
                CallerEntry.COLUMN_CALLER_TIMESTAMP,
                CallerEntry.COLUMN_CALLER_TYPE
        };

        // Filter results WHERE "_ID" = 'id'
        String selection = CallerEntry.COLUMN_CALLER_NUMBER + " = ?";
        String[] selectionArgs = {number};

        // Perform a query on the products table
        Cursor cursor = db.query(
                CallerEntry.TABLE_NAME,   // The table to query
                projection,                // The columns to return
                selection,                 // The columns for the WHERE clause
                selectionArgs,             // The values for the WHERE clause
                null,              // Don't group the rows
                null,               // Don't filter by row groups
                null);            // The sort order


        if (cursor != null && cursor.moveToFirst()) {

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(CallerEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_NAME);
            int numberColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_NUMBER);
            int timestampColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_TIMESTAMP);
            int typeColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_TYPE);

            Caller caller = new Caller();
            caller.setId(Integer.parseInt(cursor.getString(idColumnIndex)));
            caller.setName(cursor.getString(nameColumnIndex));
            caller.setNumber(cursor.getString(numberColumnIndex));
            caller.setTimestamp(Long.parseLong(cursor.getString(timestampColumnIndex)));
            caller.setType(cursor.getString(typeColumnIndex));

            cursor.close();

            return caller;
        }
        return null;

    }*/





    /**
     * Retrieve all  callers from the database
     *
     * @return callers
     *//*
    public ArrayList<Caller> getAllCallers() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Caller> callers = new ArrayList<>();
        Caller caller;


        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                CallerEntry._ID,
                CallerEntry.COLUMN_CALLER_NAME,
                CallerEntry.COLUMN_CALLER_NUMBER,
                CallerEntry.COLUMN_CALLER_TIMESTAMP,
                CallerEntry.COLUMN_CALLER_TYPE
        };

        // Perform a query on the callers table
        Cursor cursor = db.query(
                CallerEntry.TABLE_NAME,   // The table to query
                projection,                // The columns to return
                null,              // The columns for the WHERE clause
                null,           // The values for the WHERE clause
                null,              // Don't group the rows
                null,               // Don't filter by row groups
                null);            // The sort order

        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(CallerEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_NAME);
        int numberColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_NUMBER);
        int timestampColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_TIMESTAMP);
        int typeColumnIndex = cursor.getColumnIndex(CallerEntry.COLUMN_CALLER_TYPE);


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                caller = new Caller();
                caller.setId(Integer.parseInt(cursor.getString(idColumnIndex)));
                caller.setName(cursor.getString(nameColumnIndex));
                caller.setNumber(cursor.getString(numberColumnIndex));
                caller.setTimestamp(Long.parseLong(cursor.getString(timestampColumnIndex)));
                caller.setType(cursor.getString(typeColumnIndex));

                callers.add(caller);
            } while (cursor.moveToNext());
        }

        return callers;

    }*/



    /**
     * Retrieve all entries from the database
     *
     * @return logs
     */
    public ArrayList<Log> getAllLogEntries() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Log> logs = new ArrayList<>();
        Log logEntry;


        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LogEntry._ID,
                LogEntry.COLUMN_CALLER_NAME,
                LogEntry.COLUMN_CALLER_NUMBER,
                LogEntry.COLUMN_CALL_TIMESTAMP,
                LogEntry.COLUMN_CALLER_TYPE,
                LogEntry.COLUMN_EMAIL,
                LogEntry.COLUMN_CALL_DURATION
        };

        // Perform a query on the callers table
        Cursor cursor = db.query(
                LogEntry.TABLE_NAME,   // The table to query
                projection,                // The columns to return
                null,                 // The columns for the WHERE clause
                null,             // The values for the WHERE clause
                null,              // Don't group the rows
                null,               // Don't filter by row groups
                null);            // The sort order

        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(LogEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_CALLER_NAME);
        int phoneNoColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_CALLER_NUMBER);
        int timestampColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_CALL_TIMESTAMP);
        int typeColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_CALLER_TYPE);
        int durationColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_CALL_DURATION);
        int emailColumnIndex = cursor.getColumnIndex(LogEntry.COLUMN_EMAIL);





        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                logEntry = new Log();
                logEntry.setId(Integer.parseInt(cursor.getString(idColumnIndex)));
                logEntry.setName(cursor.getString(nameColumnIndex));
                logEntry.setPhone_no(cursor.getString(phoneNoColumnIndex));
                logEntry.setTimestamp(Long.parseLong(cursor.getString(timestampColumnIndex)));
                logEntry.setType(Integer.parseInt(cursor.getString(typeColumnIndex)));
                logEntry.setDuration(Long.parseLong(cursor.getString(durationColumnIndex)));
                logEntry.setEmail(cursor.getString(emailColumnIndex));

                logs.add(logEntry);
            } while (cursor.moveToNext());
        }

        return logs;

    }




    /**
     * Delete a logEntry from the database
     *
     * @param logEntry
     */
    public void deleteLogEntry(Log logEntry) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LogEntry.TABLE_NAME,
                LogEntry._ID + " = ?",
                new String[]{String.valueOf(logEntry.getId())}
        );

    }


    /**
     * Delete all Log Entries from the database
     */
    public void deleteAllLogEntries() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LogEntry.TABLE_NAME, null, null);

    }


//    /**
//     * Update a single product in the database.
//     *
//     * @param product
//     */
//    public int updateProduct(Product product) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(ProductEntry.COLUMN_PRODUCT_NAME, product.getName());
//        values.put(ProductEntry.COLUMN_PRODUCT_CODE, product.getCode());
//        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, product.getPrice());
//
//        // updating row
//        return db.update(ProductEntry.TABLE_NAME,
//                values,
//                ProductEntry.COLUMN_PRODUCT_CODE + " = ?",
//                new String[]{product.getCode()});
//    }

}
