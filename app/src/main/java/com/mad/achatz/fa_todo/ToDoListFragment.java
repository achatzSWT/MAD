package com.mad.achatz.fa_todo;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ToDoListFragment extends ListFragment {

    private ArrayList<ToDo> todoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ListFragment","onCreateView");

        return inflater.inflate(R.layout.todo_list_fragment, container, false);
    }

    public void setTodoList(ArrayList<ToDo> todoList) {
        this.todoList = todoList;
        TodoListAdapter listAdapter = new TodoListAdapter(getContext(), todoList);
        setListAdapter(listAdapter);
    }

    public void refreshList() {
        ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
    }


}
