package com.mad.achatz.fa_todo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditToDoActivity extends AppCompatActivity
                              implements DatepickerDialogFragment.OnDateSetListener,
                                         TimepickerDialogFragment.OnTimeSetListener {

    private ToDo todo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_to_do);

        todo = new ToDo();

        ((EditText)findViewById(R.id.todo_name_edittext)).setText(todo.getName());
        ((EditText)findViewById(R.id.todo_description_edittext)).setText(todo.getDescription());
        setDateTimeTextViews(todo.getDueDate().getTime());
        ((CheckBox)findViewById(R.id.done_checkbox)).setChecked(todo.isDone());
        ((CheckBox)findViewById(R.id.favorite_checkbox)).setChecked(todo.isFavourite());
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
        String timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
        TextView dateTextView = (TextView) findViewById(R.id.date_textview);
        TextView timeTextView = (TextView) findViewById(R.id.time_textview);
        dateTextView.setText(dateString);
        timeTextView.setText(timeString);
    }

}
