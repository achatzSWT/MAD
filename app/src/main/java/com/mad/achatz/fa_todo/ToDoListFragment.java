package com.mad.achatz.fa_todo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
    public static final int REQUEST_LOGIN = 2;

    public static final int SORT_DATE_FAV = 0;
    public static final int SORT_FAV_DATE = 1;

    private TodoDbAdapter db;

    private TodoWebAccess webAccess;

    private ArrayList<ToDo> todoList;

    private ProgressBar progressBar;

    private int sortMethod = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = new TodoDbAdapter(getActivity());
        todoList = new ArrayList<>();

        webAccess = new TodoWebAccess(this);

        TodoListAdapter listAdapter = new TodoListAdapter(getContext(), todoList);
        listAdapter.setToDoListClickListener(this);
        setListAdapter(listAdapter);

        View view = inflater.inflate(R.layout.todo_list_fragment, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getText(R.string.connecting));

        webAccess.checkConnection(progressDialog);

        return view;
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

    private void initList(boolean connectionAvailable) {
        refreshList();
        if (connectionAvailable)
            synchronizeWithWeb();
    }

    public void startLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
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
        refreshList();
    }

    private void deleteTodo(ToDo toDo) {
        db.deleteTodo(toDo);
        webAccess.deleteTodo(toDo);
        refreshList();
    }

    private void updateTodo(ToDo toDo) {
        db.updateTodoInDb(toDo);
        webAccess.updateTodo(toDo);
        refreshList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_CANCELED) return;

        if (db.isClosed()) db.open();

        ToDo todo = null;
        try {
            todo = data.getParcelableExtra(EditToDoActivity.EXTRA_TODO_PARCEL);
        } catch (RuntimeException e) {
            // Nicht alle activities geben ein todo zurück
        }
        switch (requestCode) {
            case REQUEST_NEW_TODO:
                addTodo(todo);
                break;
            case REQUEST_EDIT_TODO:
                switch (resultCode) {
                    case EditToDoActivity.RESULT_OK:
                        updateTodo(todo);
                        break;
                    case EditToDoActivity.RESULT_DELETE:
                        deleteTodo(todo);
                        break;
                }
                break;
            case REQUEST_LOGIN:
                switch (resultCode) {
                    case LoginActivity.RESULT_OK:
                        // In diesem Fall wurde der Login erfolgreich ausgeführt
                        initList(true);
                        break;
                    case LoginActivity.RESULT_NO_CONNECTION:
                        toastNoConnection();
                        initList(false);
                        break;
                }
                break;
        }
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
    }

    @Override
    public void OnConnectionAvailableResult(boolean connectionAvailable) {
        if (connectionAvailable) {
            startLoginActivity();
        } else {
            initList(false);
            toastNoConnection();
        }
    }

    @Override
    public void OnConnectionLost() {
        toastNoConnection();
    }

    /** Zeigt eine einfache Nachricht, dass keine Verbing zum Server besteht */
    private void toastNoConnection() {
        Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
    }
}
