package com.lostfind.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.Toast;

import com.lostfind.DBManager.SiikDBHelper;
import com.lostfind.R;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportLossFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportLossFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button spinnerType;
View view = null;
    String[] itemNames,locationNames;
    EditText description;
    Button location_spinner;
    Button date_btn;
    EditText additionalInfo,rewardOption;

    private SimpleDateFormat dateFormatter;

    static final int DATE_PICKER_ID = 1111;

    private DatePickerDialog fromDatePickerDialog;
    protected AlertDialog myAlert;

    public ReportLossFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportLossFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportLossFragment newInstance(String param1, String param2) {
        ReportLossFragment fragment = new ReportLossFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       itemNames = getResources().getStringArray(R.array.array_name);
        locationNames = getResources().getStringArray(R.array.array_location);
        view =  inflater.inflate(R.layout.fragment_report_loss, container, false);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        initInputFieldView(view);
        return view;
    }


    private void initInputFieldView(View v){
        ScrollView inputScrollView = (ScrollView)v.findViewById(R.id.find_loss_input);
         spinnerType = (Button)inputScrollView.findViewById(R.id.find_loss_selector);
        description = (EditText)inputScrollView.findViewById(R.id.find_loss_desc);
        spinnerType.setOnClickListener(this);
        location_spinner = (Button)inputScrollView.findViewById(R.id.find_loss_location);
        location_spinner.setOnClickListener(this);
        date_btn  = (Button)inputScrollView.findViewById(R.id.find_loss_date);
        date_btn.setOnClickListener(this);
        additionalInfo = (EditText)inputScrollView.findViewById(R.id.additional_info);
        rewardOption = (EditText)inputScrollView.findViewById(R.id.find_loss_reward_option);
        Button loss_report_btn = (Button)inputScrollView.findViewById(R.id.find_loss_report);
        loss_report_btn.setOnClickListener(this);
//additionalInfo,rewardOption
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.find_loss_selector:
                hideKeyboard();
                launchSelector();
                break;
            case R.id.find_loss_location:
                launchLocationSelector();
                break;
            case R.id.find_loss_date:
           //     getActivity().showDialog(DATE_PICKER_ID);
                final Calendar c = Calendar
                        .getInstance();

                int year =  c.get(Calendar.YEAR);
                int month =  c.get(Calendar.MONTH);
                int day =   c.get(Calendar.DAY_OF_MONTH);
                myAlert = new DatePickerDialog(
                        getActivity(),
                        dateSetListener,
                        year,
                        month,
                        day);
            //    setDatePicker();

                myAlert.show();

                break;
            case R.id.find_loss_report:
                popualteDateInDB();
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(
                DatePicker arg0,
                int year,
                int monthofyear,
                int dayOfMonth) {
            String monthString;
            String dayString;
            int mYear = year;
            if (monthofyear + 1 < 10) {
                monthString = "0"
                        + (monthofyear+1);
            } else {
                monthString = ""
                        + (monthofyear + 1);
            }
            if (dayOfMonth < 10) {
                dayString = "0"
                        + dayOfMonth;
            } else {
                dayString = ""
                        + dayOfMonth;
            }




            String inputFomat_val = new StringBuilder()
                    // Month
                    // is
                    // 0
                    // based
                    // so
                    // addj
                    // 1
                    .append(monthString)
                    .append("-")
                    .append(dayString)
                    .append("-")
                    .append(mYear)
                    .append(" ").toString();

            date_btn
                    .setText(inputFomat_val);



        }

    };
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(spinnerType.getWindowToken(), 0);
    }
    private void launchSelector(){
       /* InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Make your selection");
        builder.setItems(R.array.array_name, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                spinnerType.setText(itemNames[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void  launchLocationSelector(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Make your selection");
        builder.setItems(R.array.array_location, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                location_spinner.setText(locationNames[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void setDatePicker(){
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_btn.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


private void popualteDateInDB(){
    ContentValues cv = new ContentValues();
    if(!TextUtils.isEmpty(description.getText().toString())){
    cv.put("description",description.getText().toString());
    }else{
        cv.put("description","");
    }

    if(!TextUtils.isEmpty(spinnerType.getText().toString())){
        cv.put("category",spinnerType.getText().toString());
    }else{
        cv.put("category","");
    }

    if(!TextUtils.isEmpty(location_spinner.getText().toString())){
        cv.put("location",location_spinner.getText().toString());
    }else{
        cv.put("location","");
    }

    if(!TextUtils.isEmpty(date_btn.getText().toString())){
        cv.put("date",date_btn.getText().toString());
    }else{
        cv.put("date","");
    }


    if(!TextUtils.isEmpty(additionalInfo.getText().toString())){
        cv.put("info",additionalInfo.getText().toString());
    }else{
        cv.put("info","");
    }


    if(!TextUtils.isEmpty(rewardOption.getText().toString())){

    }else{

    }
    cv.put("username","b.chandrasaimohan@gmail.com");
    try {
        int rowId = (Integer) new SiikDBHelper().insertSiiKData("lostorfound", cv);
        if(rowId != -1){
            Toast.makeText(getActivity(),"Data Saved",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(),"Data Save Failed",Toast.LENGTH_SHORT).show();
        }
    }
    catch (SQLException e){
        e.printStackTrace();
    }catch (Exception e){
        e.printStackTrace();
    }
}
}
