package com.mad.achatz.fa_todo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimepickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    final static String HOUR = "hour";
    final static String MINUTE = "minute";

    private OnTimeSetListener onTimeSetListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        int hour = args.getInt(HOUR);
        int minute = args.getInt(MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    public void setOnTimeSetListener(OnTimeSetListener onTimeSetListener){
        this.onTimeSetListener = onTimeSetListener;
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        if (onTimeSetListener != null)
            onTimeSetListener.onTimeSet(view, hour, minute);
    }

    public interface OnTimeSetListener {
        void onTimeSet(TimePicker view, int hour, int minute);
    }
}
