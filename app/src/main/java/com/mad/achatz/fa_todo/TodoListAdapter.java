package com.mad.achatz.fa_todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import java.text.DateFormat;
import java.util.Date;
import java.util.List;


public class TodoListAdapter extends ArrayAdapter {

    public TodoListAdapter(Context context, List<ToDo> objects) {
        super(context, R.layout.todo_list_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ToDo todo = (ToDo) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_list_item, null);
        }

        ((TextView)convertView.findViewById(R.id.list_item_name_textview)).setText(todo.getName());

        Date date = todo.getDueDate().getTime();
        String dateString = DateFormat.getDateInstance().format(date);
        dateString += ", " + DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
        ((TextView)convertView.findViewById(R.id.list_item_date_textview)).setText(dateString);

        ((CheckBox)convertView.findViewById(R.id.list_item_done_checkbox)).setChecked(todo.isDone());
        ((CheckBox)convertView.findViewById(R.id.list_item_favorite_checkbox)).setChecked(todo.isFavourite());

        return convertView;
    }
}
