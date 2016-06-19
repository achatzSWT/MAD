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

    // Baut einzelne Listenelemente für die jeweiligen Todos
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Hole Todo von gegebener position aus der Liste
        final ToDo todo = (ToDo) getItem(position);

        // existiert schon ein alter view, den wir verwenden können? WEnn nein, erstellen wir einen neuen.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_list_item, null);
        }

        // Benutze den Activated Status um zu kennzeichnen, ob ein ToDo überfallig ist.
        // Ein Todo wird nur "aktiviert" (= rote Färbung), wenn es überfällig UND noch nicht erledigt ist.
        convertView.setActivated(todo.isOverdue() && !todo.isDone());

        // Setze Namen des Todos in entsprechenden TextView
        ((TextView)convertView.findViewById(R.id.list_item_name_textview)).setText(todo.getName());

        // Konvertiere Datum des Todo zu String und setze zugehörigen TextView
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
                    // Wenn Done-Checkbox geklickt wurde, ändere den Wert für das zugehörige Todo auf den neuen Status
                    todo.setDone(doneCheckbox.isChecked());
                    // Jetzt informiere den listener, dass ein Click stattgefunden hat.
                    toDoListClickListener.onDoneClicked(position);
                }
            }
        });

        favCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDoListClickListener != null) {
                    todo.setFavourite(favCheckbox.isChecked());
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
        /** Wird gerufen, wenn bei einem Listenelement der "Erledigt" Button geklickt wurde */
        void onDoneClicked(int position);
        /** Wird gerufen, wenn bei einem Listenelement der "Wichtig" Button geklickt wurde */
        void onFavClicked(int position);
        /** Wird gerufen, wenn auf ein Listenelement geklickt wurde */
        void onItemClicked(int position);
    }

}
