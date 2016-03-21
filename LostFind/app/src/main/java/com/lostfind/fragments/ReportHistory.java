package com.lostfind.fragments;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.lostfind.Adapter.MyRecyclerViewAdapter;
import com.lostfind.DBManager.SiikDBHelper;
import com.lostfind.DTO.DataObject;
import com.lostfind.DTO.SearchDTO;
import com.lostfind.R;
import com.lostfind.WebserviceHelpers.SiiKGetJSONParser;
import com.lostfind.WebserviceHelpers.SiiKGetResponseHelper;
import com.lostfind.interfaces.MyClickListener;
import com.lostfind.interfaces.SiikReceiveListener;
import com.lostfind.utils.BikeConstants;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportHistory extends Fragment implements MyClickListener,OnClickListener,SiikReceiveListener {
    private static final String TAG = "SearchFragment";
    private int PICK_IMAGE_REQUEST = 1;
    private View searchView;
    AutoCompleteTextView	location_spinner;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDixji8saFmpOFmSnKXY6-uP_2mnDYG3Js";
    RecyclerView searchResults;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Button category;
    String[] categoryNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        searchView = inflater.inflate(R.layout.report_history_layout, container,
                false);

        Toolbar mToolBar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        TextView toolBarTitle = (TextView)mToolBar.findViewById(R.id.title);
        toolBarTitle.setText("Report History");
        categoryNames = getResources().getStringArray(R.array.array_name);
        /*category = (Button) searchView
                .findViewById(R.id.category);
        category.setOnClickListener(this);*/
	/*	final Spinner location = (Spinner) searchView
				.findViewById(R.id.location);*/
      /*  final EditText comments= (EditText) searchView
                .findViewById(R.id.search_comments);*/
     /*   location_spinner = (AutoCompleteTextView)searchView.findViewById(R.id.find_loss_location);*/



     //   ImageView submit = (ImageView) searchView.findViewById(R.id.search_btn);
       /* final Calendar c = Calendar
                .getInstance();
        final Button dateBtn = (Button) searchView.findViewById(R.id.dateButton);
        final Button toDateBtn = (Button) searchView.findViewById(R.id.todateButton);

        toDateBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy",Locale.getDefault());
                Date date = null;
                date = new Date();
                c.setTime(date);
                year =  c.get(Calendar.YEAR);
                month =  c.get(Calendar.MONTH);
                day =   c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(getActivity(), toDatePickerListener,
                        year, month,day).show();
            }
        });

        dateBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy",Locale.getDefault());
                Date date = null;
                date = new Date();
                c.setTime(date);
                year =  c.get(Calendar.YEAR);
                month =  c.get(Calendar.MONTH);
                day =   c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(getActivity(), datePickerListener,
                        year, month,day).show();
            }
        });*/

        initRecyclerView();
        launchResultsList();

        return searchView;
    }


    private void initRecyclerView(){
        // ScrollView resultsView = (ScrollView)searchView.findViewById(R.id.results_table);
        LinearLayout searchResultsllayout = (LinearLayout)searchView.findViewById(R.id.results_table);
        searchResults = (RecyclerView)searchResultsllayout.findViewById(R.id.search_results_list);
    }

    private void launchResultsList(){
        searchResults.setHasFixedSize(true);
        final LinearLayoutManager   mLayoutManager = new LinearLayoutManager(getActivity());
        searchResults.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet(),ReportHistory.this);
        searchResults.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        searchResults.addItemDecoration(itemDecoration);
    }

    private ArrayList<DataObject> getDataSet() {
        String[] stringArray = getActivity().getResources().getStringArray(R.array.array_name);
        ArrayList results = new ArrayList<DataObject>();
        for(int i=0;i<stringArray.length;i++) {
            DataObject obj = new DataObject("",stringArray[i],"found");
            results.add(i, obj);
        }
        return results;
    }

    private LinearLayout rootLayout;
    private int year;
    private int month;
    private int day;
    private StringBuilder selectedDate;
    static final int DATE_DIALOG_ID = 999;
   /* private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            Button dateBtn = (Button) searchView.findViewById(R.id.dateButton);

            selectedDate = new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" ");
            dateBtn.setText(selectedDate.toString());
        }
    };


    private DatePickerDialog.OnDateSetListener toDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            Button toDateBtn = (Button) searchView.findViewById(R.id.todateButton);

            selectedDate = new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" ");
            toDateBtn.setText(selectedDate.toString());
        }
    };*/
	
	/*private TableLayout createTableLayout(Cursor resultCursor) {
		ScrollView resultsView = (ScrollView)searchView.findViewById(R.id.results_table);
        //table_results_llayout
        LinearLayout searchResultsllayout = (LinearLayout)resultsView.findViewById(R.id.table_results_llayout);
		TableLayout searchResults = (TableLayout)searchResultsllayout.findViewById(R.id.table_results);
	   *//*
	     TableLayout tableLayout = new TableLayout(getActivity());
	     tableLayout.setBackgroundColor(Color.BLACK);*//*
		TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
		tableLayoutParams.setMargins(5, 5, 5, 5);


		   if(resultCursor != null){
	    	 if(resultCursor.moveToFirst()){
	    		 //  tableLayout.addView(createTableHeader(), tableLayoutParams);
					do{
						String category = resultCursor
								.getString(resultCursor
										.getColumnIndex("category"));
						String type = resultCursor
								.getString(resultCursor
										.getColumnIndex("type"));
						String imageURL = resultCursor
								.getString(resultCursor
										.getColumnIndex("imageurl"));
						//searchResults.addView(createTableRow(category, type, imageURL), tableLayoutParams);
                        searchResults.addView(createResultsTableRow(category, type, imageURL), tableLayoutParams);
					   } while (resultCursor.moveToNext());
							   
					}
	     }else{
	    	 Toast.makeText(getActivity(), "No records Found", Toast.LENGTH_LONG).show();
	     }
	     
	       // tableLayout.addView(createTableHeader(), tableLayoutParams);
	        // tableLayout.addView(createTableRow2(), tableLayoutParams);
	    // }
	      //   tableLayout.setBackgroundColor(Color.TRANSPARENT);
	     return searchResults;
	}*/


















    @Override
    public void onSpecificViewOnItemClick(int position, View v) {
        Fragment  infoFragment = null;
        Log.d(TAG,"Clicked Position:::"+position);
      /*    if(position==0){
            infoFragment =          InfoFragment.newInstance(position,"");
        }else if(position==1){
            infoFragment =          InfoFragment.newInstance(position,"");
        }else if(position==2){
            infoFragment =          InfoFragment.newInstance(position,"");
        }
      getActivity().getSupportFragmentManager().beginTransaction()
                .replace(mContainerId, infoFragment).addToBackStack(null)
                .commit();*/
    }

    @Override
    public void onItemClick(int position, View v) {
        Log.d(TAG,"Clicked Position:::"+position);
    }

    @Override
    public void receiveResult(String result) {
        if(result!=null){
            if(result.equalsIgnoreCase(BikeConstants.WEBSERVICE_NETWORK_FAIL)){
                Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();
            }else{

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(category.getWindowToken(), 0);
    }




    private void makeWebServiceCall(){
        new SiiKGetResponseHelper(getActivity(), ReportHistory.this).execute(
                BikeConstants.SEARCH_GET_SERVICE_URL);
    }

    private void makeCallToJSONParser(String serviceResponse){
        List<SearchDTO> jsonResponseList = new SiiKGetJSONParser().getParseResponse(getActivity(),serviceResponse);
        if(jsonResponseList!=null && jsonResponseList.size()>0){
            //populate list;
        }
    }

}
