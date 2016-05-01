package com.mad.achatz.fa_todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditToDoActivity extends AppCompatActivity
                              implements DatepickerDialogFragment.OnDateSetListener,
                                         TimepickerDialogFragment.OnTimeSetListener,
                                         DeleteToDoDialogFragment.OnDeleteComfirmedListener {

    public static final String EXTRA_TODO_PARCEL = "EXTRA_TODO_PARCEL";
    public static final int RESULT_DELETE = 123;

    private ToDo todo;

    private boolean inEditMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_to_do);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.label_edit_todo);

        todo = getIntent().getParcelableExtra(EXTRA_TODO_PARCEL);

        if (todo == null) {
            todo = new ToDo();
            setTitle(R.string.label_add_todo);
            inEditMode = false;
        }

        ((EditText)findViewById(R.id.todo_name_edittext)).setText(todo.getName());
        ((EditText)findViewById(R.id.todo_description_edittext)).setText(todo.getDescription());
        setDateTimeTextViews(todo.getDueDate().getTime());
        ((CheckBox)findViewById(R.id.done_checkbox)).setChecked(todo.isDone());
        ((CheckBox)findViewById(R.id.favorite_checkbox)).setChecked(todo.isFavourite());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_todo_menu, menu);
        menu.findItem(R.id.menu_delete).setVisible(inEditMode);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                returnTodo();
                return true;
            case R.id.menu_delete:
                showDeleteConfirmDialog();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
        }

        return false;
    }

    public void dateTimeClicked(View view) {
        switch (view.getId()) {
            case R.id.date_textview:
                showDatePickerDialog();
                break;
            case R.id.time_textview:
                showTimePickerDialog();
                break;
        }
    }

    public void checkboxClicked(View view) {
        CheckBox cb = (CheckBox) view;
        switch (view.getId()) {
            case R.id.done_checkbox:
                todo.setDone(cb.isChecked());
                break;
            case R.id.favorite_checkbox:
                todo.setFavourite(cb.isChecked());
                break;
        }
    }

    private void showDatePickerDialog(){
        Calendar c;
        if (todo == null) {
            c = Calendar.getInstance();
        } else {
            c = todo.getDueDate();
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatepickerDialogFragment datepickerDialogFragment = new DatepickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt(DatepickerDialogFragment.YEAR, year);
        args.putInt(DatepickerDialogFragment.MONTH, month);
        args.putInt(DatepickerDialogFragment.DAY, day);
        datepickerDialogFragment.setArguments(args);

        datepickerDialogFragment.setOnDateSetListener(this);
        datepickerDialogFragment.show(getFragmentManager(), null);
    }

    private void showTimePickerDialog(){
        Calendar c;
        if (todo == null) {
            c = Calendar.getInstance();
        } else {
            c = todo.getDueDate();
        }

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimepickerDialogFragment timepickerDialogFragment = new TimepickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt(TimepickerDialogFragment.HOUR, hour);
        args.putInt(TimepickerDialogFragment.MINUTE, minute);
        timepickerDialogFragment.setArguments(args);

        timepickerDialogFragment.setOnTimeSetListener(this);
        timepickerDialogFragment.show(getFragmentManager(), null);
    }

    private void showDeleteConfirmDialog() {
        DeleteToDoDialogFragment deleteToDoDialogFragment = new DeleteToDoDialogFragment();
        deleteToDoDialogFragment.show(getFragmentManager(), null);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = todo.getDueDate();
        cal.set(year, monthOfYear, dayOfMonth);
        setDateTimeTextViews(cal.getTime());
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        Calendar cal = todo.getDueDate();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        setDateTimeTextViews(cal.getTime());
    }

    private void setDateTimeTextViews(Date date) {
        String dateString = DateFormat.getDateInstance(DateFormat.FULL).format(date);
        String timeString = (SimpleDateFormat.getTimeInstance(DateFormat.SHORT)).format(date);
        TextView dateTextView = (TextView) findViewById(R.id.date_textview);
        TextView timeTextView = (TextView) findViewById(R.id.time_textview);
        dateTextView.setText(dateString);
        timeTextView.setText(timeString);
    }


    private void returnTodo() {
        // Andere Werte werden alle in "Echtzeit" gesetzt, aber Name und Beschreibung fehlen noch
        EditText nameEditText = (EditText) findViewById(R.id.todo_name_edittext);
        EditText descEditText = (EditText) findViewById(R.id.todo_description_edittext);
        todo.setName(nameEditText.getText().toString());
        todo.setDescription(descEditText.getText().toString());

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TODO_PARCEL, todo);
        this.setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDeleteConfirmed() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TODO_PARCEL, todo);
        this.setResult(RESULT_DELETE, intent);
        finish();
    }

}
