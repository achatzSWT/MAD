package com.mad.achatz.fa_todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_NEW_TODO = 0;
    private final int REQUEST_EDIT_TODO = 1;

    private TodoDbAdapter db;

    private ArrayList<ToDo> todoList;

    private ToDoListFragment toDoListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity","vor setCOntentView");
        setContentView(R.layout.activity_main);
        Log.d("MainActivity","nach setCOntentView");
        db = new TodoDbAdapter(this);
        todoList = db.getAllTodos(null);

        Log.d("MainActivity","vor Fragment zeug");

        toDoListFragment = (ToDoListFragment) getSupportFragmentManager().findFragmentById(R.id.todo_list_fragment);
        toDoListFragment.setTodoList(todoList);
    }

    @Override
    protected void onStart() {
        db.open();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
    }

    public void addToDoFabClicked(View view) {
        Intent intent = new Intent(this, EditToDoActivity.class);
        startActivityForResult(intent, REQUEST_NEW_TODO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) return;

        if (db.isClosed()) db.open();

        ToDo todo;
        switch (requestCode) {
            case REQUEST_NEW_TODO:
                todo = data.getParcelableExtra(EditToDoActivity.EXTRA_TODO_PARCEL);
                db.insertTodo(todo);
                todoList.add(todo);
                break;
            case REQUEST_EDIT_TODO:
                break;
        }

        toDoListFragment.refreshList();
    }
}
