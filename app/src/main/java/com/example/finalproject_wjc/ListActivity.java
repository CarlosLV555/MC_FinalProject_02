package com.example.finalproject_wjc;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class ListActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        ListView listView = findViewById(R.id.list_view);


        // Example data (replace this with real data or data passed via Intent)
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SQLiteDatabase database = dbHelper.getDataBase();

        // Query data from the database
        Cursor dbCursor = database.rawQuery("SELECT * FROM MobCartoDB_table;", null);
        int length = dbCursor.getCount();
        System.out.println("Rows fetched: " + length);
        if (length == 0) {
            System.out.println("No data found in the table.");
        } else {
            System.out.println("Data found, processing...");
        }
        dbCursor.moveToFirst();

        String[] db_names = new String[length];

        for (int i = 0; i < length; i++) {

            db_names[i] = dbCursor.getString(0);
            dbCursor.moveToNext();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, db_names);

// Toggle visibility of the empty message and ListView
        TextView emptyMessage = findViewById(R.id.empty_message);
        if (length == 0) {
            emptyMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

// Set the adapter
        listView.setAdapter(adapter);
        // Check if data is empty

    }
}
