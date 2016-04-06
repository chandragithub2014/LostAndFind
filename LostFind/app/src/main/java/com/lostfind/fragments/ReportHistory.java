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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.lostfind.Adapter.MyRecyclerViewAdapter;
import com.lostfind.DBManager.SiikDBHelper;
import com.lostfind.DTO.DataObject;
import com.lostfind.DTO.SearchDTO;
import com.lostfind.R;
import com.lostfind.WebserviceHelpers.SiiKGetJSONParser;
import com.lostfind.WebserviceHelpers.SiiKGetResponseHelper;
import com.lostfind.application.MyApplication;
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
import android.text.TextUtils;
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
import android.widget.CheckBox;
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
    CheckBox lost_check,found_check,claim_check;

    String lostQueryString="";
    String foundQueryString="";
    String claimQueryString = "";
    private boolean isLostChecked=false;
    private boolean isFoundChecked = false;
    private boolean isClaimChecked = false;
    HashMap<String,String> queryHash ;
    String queryString = "";

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
        queryHash = new HashMap<String,String>();
        Toolbar mToolBar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        TextView toolBarTitle = (TextView)mToolBar.findViewById(R.id.title);
        toolBarTitle.setText("Your History");
        categoryNames = getResources().getStringArray(R.array.array_name);

        LinearLayout check_search_Layout = (LinearLayout)searchView.findViewById(R.id.checkbox_layout);
        lost_check  = (CheckBox)check_search_Layout.findViewById(R.id.lost_check);
        found_check = (CheckBox)check_search_Layout.findViewById(R.id.found_check);
        claim_check = (CheckBox)check_search_Layout.findViewById(R.id.found_claim);
        ImageView submit = (ImageView) check_search_Layout.findViewById(R.id.search_btn);
        initRecyclerView();
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (isLostChecked && isFoundChecked && isClaimChecked) {
                    queryString = "status=lost&status=found&status=claim";
                } else if (isLostChecked && !isFoundChecked) {
                    queryString = "status=lost";
                } else if (!isLostChecked && isFoundChecked) {
                    queryString = "status=found";
                }*/

                makeWebServiceCall();
            }
        });

        lost_check.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    isLostChecked = true;
                    queryHash.put("lost","true");
                }else{
                    isLostChecked = false;
                    queryHash.put("lost","false");
                }
            }
        });

        found_check.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    queryHash.put("found","true");
                }else{
                    isFoundChecked = false;
                    queryHash.put("found","false");
                }
            }
        });

        claim_check.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    queryHash.put("claim","true");
                }else{
                    isClaimChecked = false;
                    queryHash.put("claim","false");
                }
            }
        });
        /*category = (Button) searchView
                .findViewById(R.id.category);
        category.setOnClickListener(this);*/
	/*	final Spinner location = (Spinner) searchView
				.findViewById(R.id.location);*/
      /*  final EditText comments= (EditText) searchView
                .findViewById(R.id.search_comments);*/
     /*   location_spinner = (AutoCompleteTextView)searchView.findViewById(R.id.find_loss_location);*/



     //
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

    private ArrayList<SearchDTO> getDataSet() {
        String[] stringArray = getActivity().getResources().getStringArray(R.array.array_name);
        ArrayList results = new ArrayList<SearchDTO>();

        for(int i=0;i<stringArray.length;i++) {
            //   DataObject obj = new DataObject("",stringArray[i],"found");
            //       results.add(i, obj);

            SearchDTO obj = new 	SearchDTO("", "desc","found", ""+(i+1), stringArray[i]);
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
            if(result.equalsIgnoreCase(BikeConstants.WEBSERVICE_NETWORK_FAIL) || result.equalsIgnoreCase("Get Failed")){
                if(!TextUtils.isEmpty(MyApplication.getInstance().getRegistrationResponseMessage())){
                    Toast.makeText(getActivity(),MyApplication.getInstance().getRegistrationResponseMessage(),Toast.LENGTH_LONG).show();
                }
            }else if(result.equalsIgnoreCase("success")){
                if(!TextUtils.isEmpty(MyApplication.getInstance().getSearchResponse())){
                    //Toast.makeText(getActivity(),MyApplication.getInstance().getRegistrationResponseMessage(),Toast.LENGTH_LONG).show();
                    String searchResponse = MyApplication.getInstance().getSearchResponse();
                    if(searchResponse.equalsIgnoreCase("no items found")){
                        Toast.makeText(getActivity(),searchResponse,Toast.LENGTH_LONG).show();
                    }else {
                        makeCallToJSONParser(searchResponse);
                    }
                }
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

        String lostString = "";
        String findString = "";
        String claimString = "";

        if(queryHash!=null && queryHash.size()>0){
            if(queryHash.get("lost")!=null && queryHash.get("lost").equalsIgnoreCase("true")){
                lostString = "status=lost";
                if(!TextUtils.isEmpty(queryString)){
                    queryString += "&"+lostString;
                }else{
                    queryString = lostString;
                }
            }
            if(queryHash.get("found")!=null && queryHash.get("found").equalsIgnoreCase("true")){
                findString = "status=found";
                if(!TextUtils.isEmpty(queryString)){
                    queryString += "&"+findString;
                }else{
                    queryString = findString;
                }
            }
            if(queryHash.get("claim")!=null && queryHash.get("claim").equalsIgnoreCase("true")){
                claimString = "status=claim";
                if(!TextUtils.isEmpty(queryString)){
                    queryString += "&"+claimString;
                }else{
                    queryString = claimString;
                }
            }
        }
        if(!TextUtils.isEmpty(queryString)){
            new SiiKGetResponseHelper(getActivity(), ReportHistory.this).execute(
                    BikeConstants.REPORT_HISTORY_GET_SERVICE_URL+"?"+queryString);
            queryHash = new HashMap<String,String>();
            queryString = "";
            lost_check.setChecked(false);
            found_check.setChecked(false);
            claim_check.setChecked(false);

        }else {
            new SiiKGetResponseHelper(getActivity(), ReportHistory.this).execute(
                    BikeConstants.REPORT_HISTORY_GET_SERVICE_URL);
        }
    }

    private void makeCallToJSONParser(String serviceResponse){
        List<SearchDTO> jsonResponseList = new SiiKGetJSONParser().getParseResponse(getActivity(),serviceResponse);
        if(jsonResponseList!=null && jsonResponseList.size()>0){
            //populate list;
            lauchWebserviceResultsList(jsonResponseList);
        }
    }

    private void lauchWebserviceResultsList(List<SearchDTO> resultList){
        searchResults.setHasFixedSize(true);
        final LinearLayoutManager   mLayoutManager = new LinearLayoutManager(getActivity());
        searchResults.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(resultList,ReportHistory.this);
        searchResults.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        searchResults.addItemDecoration(itemDecoration);
    }

}
