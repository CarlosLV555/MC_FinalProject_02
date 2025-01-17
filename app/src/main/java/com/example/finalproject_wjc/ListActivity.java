package com.example.finalproject_wjc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class ListActivity extends AppCompatActivity {
    static DatabaseHelper dbHelper;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        ListView listView = findViewById(R.id.list_view);
        TextView emptyMessage = findViewById(R.id.empty_message);

        // Initialize database
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
        if (length == 0) {
            emptyMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        } else {
            emptyMessage.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        String[] db_names = new String[length];
        final Cursor finalCursor = dbCursor;

        dbCursor.moveToFirst();
        for (int i = 0; i < length; i++) {
            db_names[i] = dbCursor.getString(dbCursor.getColumnIndex("name")); // Adjust column name
            dbCursor.moveToNext();
        }
        dbCursor.moveToFirst(); // Reset cursor

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, db_names);
        listView.setAdapter(adapter);

        // Set item click listener
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            finalCursor.moveToPosition(position);

            // Get the data for the selected item
            String name = finalCursor.getString(finalCursor.getColumnIndexOrThrow("name"));
            String address = finalCursor.getString(finalCursor.getColumnIndexOrThrow("address"));
            String category = finalCursor.getString(finalCursor.getColumnIndexOrThrow("category"));
            String subCategory = finalCursor.getString(finalCursor.getColumnIndexOrThrow("sub_cat"));
            String url = finalCursor.getString(finalCursor.getColumnIndexOrThrow("url"));
            String notes = finalCursor.getString(finalCursor.getColumnIndexOrThrow("notes"));
            double lat = finalCursor.getDouble(finalCursor.getColumnIndexOrThrow("lat"));
            double lng = finalCursor.getDouble(finalCursor.getColumnIndexOrThrow("lng"));

            // Create an intent to start PointDescriptionActivity
            Intent intent = new Intent(ListActivity.this, PointDescriptionActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("address", address);
            intent.putExtra("category", category);
            intent.putExtra("sub_cat", subCategory); // Must match the key in PointDescriptionActivity
            intent.putExtra("url", url);
            intent.putExtra("notes", notes);
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);

            // Start the PointDescriptionActivity
            startActivity(intent);
        });
    }
}
