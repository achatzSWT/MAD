package com.mad.achatz.fa_todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addToDoFabClicked(View view) {
        ((ToDoListFragment) getSupportFragmentManager().findFragmentById(R.id.todo_list_fragment)).startAddTodoActivity();
    }

}
