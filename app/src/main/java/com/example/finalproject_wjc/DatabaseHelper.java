package com.example.finalproject_wjc;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH;
    private static final String DB_PATH_PREFIX = "/data/user/0/";
    private static final String DB_PATH_SUFFIX = "/databases/";
    private static final String DB_NAME = "MobCarto_SQLite.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public void createDataBase() throws IOException {
        // Set the database path
        DB_PATH = DB_PATH_PREFIX + myContext.getPackageName() + DB_PATH_SUFFIX + DB_NAME;
        Log.d("DatabaseHelper", "DB_PATH set to: " + DB_PATH);

        // Check if the database already exists
        boolean dbExist = checkDataBase();
        Log.d("DatabaseHelper", "Database exists: " + dbExist);

        SQLiteDatabase db_Read = null;
        if (dbExist) {
            Log.d("DatabaseHelper", "Database already exists. No need to copy.");
        } else {
            Log.d("DatabaseHelper", "Database does not exist. Preparing to copy.");
            try {
                // Create an empty database file to prepare for copying
                db_Read = this.getReadableDatabase();
                Log.d("DatabaseHelper", "Readable database created successfully.");
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Error while creating readable database: " + e.getMessage(), e);
            } finally {
                if (db_Read != null) {
                    db_Read.close();
                    Log.d("DatabaseHelper", "Readable database closed.");
                }
            }

            try {
                // Copy the database from assets
                Log.d("DatabaseHelper", "Attempting to copy the database.");
                copyDataBase();
                Log.d("DatabaseHelper", "Database copied successfully.");
            } catch (IOException e) {
                Log.e("DatabaseHelper", "Error while copying the database: " + e.getMessage(), e);
                throw e; // Re-throw the exception to propagate the error
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_PATH, null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            Log.d("DatabaseHelper", "Database opened successfully for checking existence.");
            checkDB.close();
            return true;
        } catch (SQLiteException e) {
            Log.d("DatabaseHelper", "Database does not exist.");
            return false;
        }
    }

    private void copyDataBase() throws IOException {
        Log.d("DatabaseHelper", "Starting database copy process.");

        InputStream assetsDB = null;
        OutputStream dbOut = null;

        try {
            // Open the database from assets
            assetsDB = myContext.getAssets().open(DB_NAME);
            Log.d("DatabaseHelper", "Database file opened from assets. Size: " + assetsDB.available() + " bytes");

            // Ensure the database directory exists
            File directory = new File(DB_PATH_PREFIX + myContext.getPackageName() + DB_PATH_SUFFIX);
            if (!directory.exists()) {
                boolean dirCreated = directory.mkdirs();
                if (dirCreated) {
                    Log.d("DatabaseHelper", "Database directory created successfully.");
                } else {
                    Log.e("DatabaseHelper", "Failed to create database directory.");
                    throw new IOException("Failed to create directory: " + directory.getPath());
                }
            }

            // Open the output file
            File outFile = new File(DB_PATH);
            dbOut = new FileOutputStream(outFile);
            Log.d("DatabaseHelper", "Output stream to database file created.");

            // Use a smaller buffer size
            byte[] buffer = new byte[1024];
            int length;
            int totalBytes = 0;
            while ((length = assetsDB.read(buffer)) > 0) {
                dbOut.write(buffer, 0, length);
                totalBytes += length;
            }
            dbOut.flush();
            Log.d("DatabaseHelper", "Database copy completed successfully. Total bytes copied: " + totalBytes);

            // Verify file size after copy
            Log.d("DatabaseHelper", "Final database file size: " + outFile.length() + " bytes");
        } catch (IOException e) {
            Log.e("DatabaseHelper", "Error while copying the database: " + e.getMessage(), e);
            throw e;
        } finally {
            if (assetsDB != null) {
                try {
                    assetsDB.close();
                    Log.d("DatabaseHelper", "Input stream closed.");
                } catch (IOException e) {
                    Log.e("DatabaseHelper", "Error closing input stream: " + e.getMessage(), e);
                }
            }
            if (dbOut != null) {
                try {
                    dbOut.close();
                    Log.d("DatabaseHelper", "Output stream closed.");
                } catch (IOException e) {
                    Log.e("DatabaseHelper", "Error closing output stream: " + e.getMessage(), e);
                }
            }
        }
    }

    public SQLiteDatabase getDataBase() throws SQLException {
        Log.d("DatabaseHelper", "Opening the database.");
        myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null,
                SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        Log.d("DatabaseHelper", "Database opened successfully.");
        return myDataBase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Placeholder: no default database creation steps
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Placeholder: no upgrade logic for now
    }
}
