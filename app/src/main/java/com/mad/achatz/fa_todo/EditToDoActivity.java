package com.mad.achatz.fa_todo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.text.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class EditToDoActivity extends AppCompatActivity implements DatepickerDialogFragment.OnDateSetListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_to_do);
    }

    public void dateTimeClicked(View view) {

        switch (view.getId()){
            case R.id.date_textview:
                changeDate();
                break;
            case R.id.time_textview: break;
        }
    }

    public void changeDate (){
        Calendar c = Calendar.getInstance();
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String dateString = DateFormat.getDateInstance().format(new Date(year, monthOfYear, dayOfMonth));
        TextView dateTextView = (TextView) findViewById(R.id.date_textview);
        dateTextView.setText(dateString);
    }
}
