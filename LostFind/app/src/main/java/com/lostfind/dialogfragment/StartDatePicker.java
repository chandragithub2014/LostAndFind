package com.lostfind.dialogfragment;

/**
 * Created by CHANDRASAIMOHAN on 3/10/2016.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;


public class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    Calendar c = Calendar.getInstance();
    int startYear = c.get(Calendar.YEAR);
    int startMonth = c.get(Calendar.MONTH);
    int startDay = c.get(Calendar.DAY_OF_MONTH);
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // Use the current date as the default date in the picker
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, startYear, startMonth, startDay);
        return dialog;

    }
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        // TODO Auto-generated method stub
        // Do something with the date chosen by the user
        startYear = year;
        startMonth = monthOfYear;
        startDay = dayOfMonth;
        //  updateStartDateDisplay();

        /*date_btn.setText(new StringBuilder().append(startMonth + 1)
                .append("-").append(startDay).append("-").append(year)
                .append(" "));*/


    }
}
