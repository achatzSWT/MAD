package com.mad.achatz.fa_todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TodoListAdapter extends ArrayAdapter {

    private ToDoListClickListener toDoListClickListener;

    public TodoListAdapter(Context context, List<ToDo> objects) {
        super(context, R.layout.todo_list_item, objects);
    }

    public void setToDoListClickListener(ToDoListClickListener toDoListClickListener) {
        this.toDoListClickListener = toDoListClickListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ToDo todo = (ToDo) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_list_item, null);
        }

        convertView.setActivated(todo.isOverdue() && !todo.isDone());

        ((TextView)convertView.findViewById(R.id.list_item_name_textview)).setText(todo.getName());

        Date date = todo.getDueDate().getTime();
        String dateString = DateFormat.getDateInstance().format(date);
        dateString += ", " + (SimpleDateFormat.getTimeInstance(DateFormat.SHORT)).format(date);
        ((TextView)convertView.findViewById(R.id.list_item_date_textview)).setText(dateString);

        final CheckBox doneCheckbox = (CheckBox)convertView.findViewById(R.id.list_item_done_checkbox);
        final CheckBox favCheckbox = (CheckBox)convertView.findViewById(R.id.list_item_favorite_checkbox);

        doneCheckbox.setChecked(todo.isDone());
        favCheckbox.setChecked(todo.isFavourite());

        doneCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDoListClickListener != null) {
                    ToDo clickedTodo = (ToDo) getItem(position);
                    clickedTodo.setDone(doneCheckbox.isChecked());
                    toDoListClickListener.onDoneClicked(position);
                }
            }
        });

        favCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDoListClickListener != null) {
                    ToDo clickedTodo = (ToDo) getItem(position);
                    clickedTodo.setFavourite(favCheckbox.isChecked());
                    toDoListClickListener.onFavClicked(position);
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDoListClickListener != null) {
                    toDoListClickListener.onItemClicked(position);
                }
            }
        });

        return convertView;
    }


    public interface ToDoListClickListener {
        void onDoneClicked(int position);
        void onFavClicked(int position);
        void onItemClicked(int position);
    }

}
