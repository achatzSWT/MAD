package com.mad.achatz.fa_todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ToDoListFragment extends ListFragment implements TodoListAdapter.ToDoListClickListener {

    public static final int REQUEST_NEW_TODO = 0;
    public static final int REQUEST_EDIT_TODO = 1;

    private TodoDbAdapter db;

    private ArrayList<ToDo> todoList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = new TodoDbAdapter(getActivity());
        todoList = db.getAllTodos(null);

        TodoListAdapter listAdapter = new TodoListAdapter(getContext(), todoList);
        listAdapter.setToDoListClickListener(this);
        setListAdapter(listAdapter);

        return inflater.inflate(R.layout.todo_list_fragment, container, false);
    }

    @Override
    public void onStart() {
        db.open();
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        db.close();
    }

    public void startAddTodoActivity() {
        Intent intent = new Intent(getActivity(), EditToDoActivity.class);
        startActivityForResult(intent, REQUEST_NEW_TODO);
    }

    public void startAddTodoActivityForEdit(ToDo todo) {
        Intent intent = new Intent(getActivity(), EditToDoActivity.class);
        intent.putExtra(EditToDoActivity.EXTRA_TODO_PARCEL, todo);
        startActivityForResult(intent, REQUEST_EDIT_TODO);
    }

    public void refreshList() {
        todoList = db.getAllTodos(todoList);
        ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_CANCELED) return;

        if (db.isClosed()) db.open();

        ToDo todo = data.getParcelableExtra(EditToDoActivity.EXTRA_TODO_PARCEL);
        switch (requestCode) {
            case REQUEST_NEW_TODO:
                db.insertTodo(todo);
                break;
            case REQUEST_EDIT_TODO:
                switch (resultCode) {
                    case AppCompatActivity.RESULT_OK:
                        db.updateTodoInDb(todo);
                        break;
                    case EditToDoActivity.RESULT_DELETE:
                        db.deleteTodo(todo);
                        break;
                }
                break;
        }

        refreshList();
    }


    @Override
    public void onDoneClicked(int position) {
        db.updateTodoInDb(todoList.get(position));
    }

    @Override
    public void onFavClicked(int position) {
        db.updateTodoInDb(todoList.get(position));
    }

    @Override
    public void onItemClicked(int position) {
        ToDo todo = todoList.get(position);
        startAddTodoActivityForEdit(todo);
    }
}
