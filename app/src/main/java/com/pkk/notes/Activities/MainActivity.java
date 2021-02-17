package com.pkk.notes.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pkk.notes.Adapters.NotesDisplayAdapter;
import com.pkk.notes.DBHelpers.DBHelper;
import com.pkk.notes.Models.NotesModel;
import com.pkk.notes.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<NotesModel> modelList;
    NotesDisplayAdapter adapter;
    DBHelper dbHelper;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Notes");
        setSupportActionBar(toolbar);

    }

    void initialize(){
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, NotesActivity.class));
            finish();
        });

        dbHelper = new DBHelper(this);
        modelList = dbHelper.getAllNotes();

        recyclerView = findViewById(R.id.notes_display_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new NotesDisplayAdapter(this, modelList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void loadRecyclerView(){
        if(adapter!=null){
            modelList = dbHelper.getAllNotes();
            adapter = new NotesDisplayAdapter(this, modelList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.refresh) {
            loadRecyclerView();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecyclerView();
    }
}