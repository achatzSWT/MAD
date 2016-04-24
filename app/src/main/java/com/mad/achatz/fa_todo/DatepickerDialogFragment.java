package com.mad.achatz.fa_todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatepickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    final static String YEAR = "year";
    final static String MONTH = "month";
    final static String DAY = "day";

    private OnDateSetListener onDateSetListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        int day = args.getInt(DAY);
        int month = args.getInt(MONTH);
        int year = args.getInt(YEAR);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setOnDateSetListener(OnDateSetListener onDateSetListener){
        this.onDateSetListener = onDateSetListener;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (onDateSetListener != null)
            onDateSetListener.onDateSet(view, year, monthOfYear, dayOfMonth);
    }


    public interface OnDateSetListener {
        void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth);
    }
}
