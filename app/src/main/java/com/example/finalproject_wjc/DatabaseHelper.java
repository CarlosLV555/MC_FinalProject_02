package com.example.finalproject_wjc;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH;
    private static String DB_PATH_PREFIX = "/data/user/0/";
    private static String DB_PATH_SUFFIX = "/databases/";
    private static String DB_NAME = "MobCarto_SQLite.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public void createDataBase() throws IOException {
        DB_PATH = DB_PATH_PREFIX + myContext.getPackageName()
                + DB_PATH_SUFFIX + DB_NAME;
        boolean dbExist = checkDataBase();
        SQLiteDatabase db_Read = null;
        if (dbExist) {
        } else {
            db_Read = this.getReadableDatabase();
            db_Read.close();
            try {
                copyDataBase();
            } catch (IOException e) {
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_PATH, null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            checkDB.close();
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    private void copyDataBase() throws IOException {
        InputStream assetsDB = myContext.getAssets().open(DB_NAME);
        File directory = new File(DB_PATH);
        if (directory.exists() == false) {
            directory.mkdir();
        }
        OutputStream dbOut = new FileOutputStream(DB_PATH);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = assetsDB.read(buffer)) > 0) {
            dbOut.write(buffer, 0, length);
        }
        dbOut.flush();
        dbOut.close();
    }

    public SQLiteDatabase getDataBase() throws SQLException {
        myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null,
                SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return myDataBase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}