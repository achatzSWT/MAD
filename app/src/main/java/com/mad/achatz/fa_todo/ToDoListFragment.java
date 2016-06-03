package com.mad.achatz.fa_todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToDoListFragment extends ListFragment implements TodoListAdapter.ToDoListClickListener,
        TodoWebAccess.TodoWebAccessListener {

    public static final int REQUEST_NEW_TODO = 0;
    public static final int REQUEST_EDIT_TODO = 1;

    public static final int SORT_DATE_FAV = 0;
    public static final int SORT_FAV_DATE = 1;

    private TodoDbAdapter db;

    private TodoWebAccess webAccess;

    private ArrayList<ToDo> todoList;

    private ProgressBar progressBar;
    private FloatingActionButton fabAddTodo;

    private int sortMethod = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = new TodoDbAdapter(getActivity());
        todoList = new ArrayList<>();

        webAccess = new TodoWebAccess(this);

        TodoListAdapter listAdapter = new TodoListAdapter(getContext(), todoList);
        listAdapter.setToDoListClickListener(this);
        setListAdapter(listAdapter);

        refreshList();

        View view = inflater.inflate(R.layout.todo_list_fragment, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        fabAddTodo = (FloatingActionButton) view.findViewById(R.id.add_todo_fab);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        synchronizeWithWeb();
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

    private void startAddTodoActivityForEdit(ToDo todo) {
        Intent intent = new Intent(getActivity(), EditToDoActivity.class);
        intent.putExtra(EditToDoActivity.EXTRA_TODO_PARCEL, todo);
        startActivityForResult(intent, REQUEST_EDIT_TODO);
    }

    public void setSortMethod(int sortMethod) {
        this.sortMethod = sortMethod;
        refreshList();
    }

    private void sortTodoList() {
        if (sortMethod == SORT_FAV_DATE)
            Collections.sort(todoList, ToDo.FavDateComparator);
        else
            Collections.sort(todoList, ToDo.DateFavComparator);
    }

    public void refreshList() {
        todoList = db.getAllTodos(todoList);
        sortTodoList();
        ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private void synchronizeWithWeb() {
        if (todoList.isEmpty()) {
            webAccess.getAllItems(progressBar);
            fabAddTodo.setEnabled(false);
        } else {
            webAccess.clearWebDatabase();
            for (ToDo todo : todoList) {
                webAccess.createTodo(todo);
            }
        }
    }

    private void addTodo(ToDo toDo) {
        db.insertTodo(toDo, false);
        webAccess.createTodo(toDo);
    }

    private void deleteTodo(ToDo toDo) {
        db.deleteTodo(toDo);
        webAccess.deleteTodo(toDo);
    }

    private void updateTodo(ToDo toDo) {
        db.updateTodoInDb(toDo);
        webAccess.updateTodo(toDo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_CANCELED) return;

        if (db.isClosed()) db.open();

        ToDo todo = data.getParcelableExtra(EditToDoActivity.EXTRA_TODO_PARCEL);
        switch (requestCode) {
            case REQUEST_NEW_TODO:
                addTodo(todo);
                break;
            case REQUEST_EDIT_TODO:
                switch (resultCode) {
                    case AppCompatActivity.RESULT_OK:
                        updateTodo(todo);
                        break;
                    case EditToDoActivity.RESULT_DELETE:
                        deleteTodo(todo);
                        break;
                }
                break;
        }

        refreshList();
    }


    @Override
    public void onDoneClicked(int position) {
        updateTodo(todoList.get(position));
        refreshList();
    }

    @Override
    public void onFavClicked(int position) {
        updateTodo(todoList.get(position));
        refreshList();
    }

    @Override
    public void onItemClicked(int position) {
        ToDo todo = todoList.get(position);
        startAddTodoActivityForEdit(todo);
    }


    @Override
    public void OnTodosRetrieved(List<ToDo> toDoList) {
        for (ToDo todo : toDoList) {
            db.insertTodo(todo, true);
        }
        refreshList();
        fabAddTodo.setEnabled(true);
    }

    @Override
    public void OnNoConnectionAvailable() {
        Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
        fabAddTodo.setEnabled(true);
    }
}
