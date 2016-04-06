package com.lostfind.fragments;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

public class SearchFragment extends Fragment implements SiikReceiveListener ,MyClickListener,OnClickListener {
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
	Button toDateBtn,fromDateBtn;
	String queryString = "";
    EditText description;
    String from_date="";
    String to_Date = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		searchView = inflater.inflate(R.layout.ly_search, container,
				false);

		Toolbar mToolBar = (Toolbar)getActivity().findViewById(R.id.toolbar);
		TextView toolBarTitle = (TextView)mToolBar.findViewById(R.id.title);
		toolBarTitle.setText("Search");
        categoryNames = getResources().getStringArray(R.array.array_name);
		 category = (Button) searchView
				.findViewById(R.id.category);
        category.setOnClickListener(this);
	/*	final Spinner location = (Spinner) searchView
				.findViewById(R.id.location);*/
		final EditText comments= (EditText) searchView
				.findViewById(R.id.search_comments);
			location_spinner = (AutoCompleteTextView)searchView.findViewById(R.id.find_loss_location);


        description = (EditText)searchView.findViewById(R.id.search_comments);

		final Calendar c = Calendar
					.getInstance();
		LinearLayout dateLayout = (LinearLayout)searchView.findViewById(R.id.to_from_date);
		fromDateBtn = (Button) dateLayout.findViewById(R.id.from_date);
		 toDateBtn = (Button) dateLayout.findViewById(R.id.to_date);
        ImageView submit = (ImageView) dateLayout.findViewById(R.id.search_btn);

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

		fromDateBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                Date date = null;
                date = new Date();
                c.setTime(date);
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(getActivity(), datePickerListener,
                        year, month, day).show();
            }
        });

        initRecyclerView();
     //   rootLayout	 = (LinearLayout) searchView.findViewById(R.id.tablelayout);
		submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //	  rootLayout.removeAllViews();
                //	  ScrollView sv = new ScrollView(getActivity());
				   /*  VerticalScrollView hsv = new HorizontalScrollView(getActivity());
				     hsv.addView(tableLayout);*/
				  
				/* 	String strCategory = category.getSelectedItem().toString();
					String strlocation = location_spinner.getText().toString();//location.getSelectedItem().toString();
					Cursor resultCursor = new SiikDBHelper().getSearchResults(strCategory,strlocation,getActivity());
					TableLayout tableLayout  = createTableLayout(resultCursor);*/
                String description = comments.getText().toString();
                String item_category = category.getText().toString();
                String location = location_spinner.getText().toString();
                String from_Date = "";
                String to_date = "";
                if(!TextUtils.isEmpty(from_date)){
                    from_Date = from_date;
                }
                if(!TextUtils.isEmpty(to_Date)){
                    to_date = to_Date;
                }


                if (!TextUtils.isEmpty(description)) {
                    if (!TextUtils.isEmpty(queryString)) {

                        queryString += "&" + "description=" + description;
                    } else {
                        queryString = "description=" + description;
                    }
                }

                if (!TextUtils.isEmpty(item_category)) {
                    if (!TextUtils.isEmpty(queryString)) {
                        queryString += "&" + "category=" + item_category;
                    } else {
                        queryString = "category=" + item_category;
                    }
                }


                if (!TextUtils.isEmpty(location)) {
                    String formattedLocation = location.replaceAll("[, ;]", "");
                    if (!TextUtils.isEmpty(queryString)) {
                        queryString += "&" + "location=" + formattedLocation;
                    } else {
                        queryString = "location=" + formattedLocation;
                    }
                }

                if(!TextUtils.isEmpty(to_date)){
                    String formattedDate = "";
                    formattedDate = "to_date="+to_date+"00:00:00.000";
                    if (!TextUtils.isEmpty(queryString)) {

                        queryString += "&" + formattedDate.replaceAll("\\s+","");
                    }else{
                        queryString = formattedDate.replaceAll("\\s+","");
                    }

                }


                if(!TextUtils.isEmpty(from_Date)){
                    String formattedDate = "";
                    formattedDate = "from_date="+from_date+"00:00:00.000";
                    if (!TextUtils.isEmpty(queryString)) {

                        queryString += "&" +formattedDate.replaceAll("\\s+","");
                    }else{
                        queryString = formattedDate.replaceAll("\\s+","");
                    }

                }

               /* if (!to_date.equalsIgnoreCase("to date")) {
                    if (!TextUtils.isEmpty(queryString)) {
                        queryString += "&" + "to_date=" + to_date + "00:00:00.000";
                    } else {
                        queryString = "to_date=" + to_date + "00:00:00.000";
                    }
                }

                if (!from_date.equalsIgnoreCase("from")) {
                    if (!TextUtils.isEmpty(queryString)) {
                        String formattedDate = "from_date="+from_date+"00:00:00.000";
                        queryString += "&" + formattedDate.replaceAll("\\s+","");
                    } else {
                        String formattedDate = "from_date="+from_date+"00:00:00.000";
                        queryString =formattedDate.replaceAll("\\s+","");
                    }
                }*/
           //    String s =  from_date+"00:00:00.00";
               // Log.d(TAG, new Date(from_date).getTime());
           //     getCalendarFromISO(from_date);
                makeWebServiceCall();

/*if(BikeConstants.IS_WEB_SERVICE_ENABLED){
         makeWebServiceCall();
}else{
	launchResultsList();
}*/


            }
		});
        setGoogleLocation();
     return searchView;
	}

    private  Calendar getCalendarFromISO(String datestring) {
        String ISO8601DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSZ";

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()) ;
        SimpleDateFormat dateformat = new SimpleDateFormat(ISO8601DATEFORMAT, Locale.getDefault());
        try {
            Date date = dateformat.parse(datestring);
            date.setHours(date.getHours()-1);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

Log.d("TAG","Calendar::"+calendar);
        return calendar;
    }
    private String fetchFormattedString(String location){
        String str = location;
        String splitted[] = str.split(",");
        StringBuffer sb = new StringBuffer();
        String retrieveData = "";
        for(int i =0; i<splitted.length; i++){
            retrieveData = splitted[i];
            if((retrieveData.trim()).length()>0){

                if(i!=0){
                    sb.append(",");
                }
                sb.append(retrieveData);

            }
        }

        str = sb.toString();
        System.out.println(str);
        return  str;
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
        setSearchListAdapter();
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        searchResults.addItemDecoration(itemDecoration);

    }

    private void setSearchListAdapter(){
        mAdapter = new MyRecyclerViewAdapter(getDataSet(),SearchFragment.this);
        searchResults.setAdapter(mAdapter);
    }


    private void lauchWebserviceResultsList(List<SearchDTO> resultList){
        searchResults.setHasFixedSize(true);
        final LinearLayoutManager   mLayoutManager = new LinearLayoutManager(getActivity());
        searchResults.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(resultList,SearchFragment.this);
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
	private DatePickerDialog.OnDateSetListener datePickerListener 
                = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			int finalMonth = month+1;
			String formattedMonth;
			if(finalMonth>=1 && finalMonth<=9){
				formattedMonth = "0"+finalMonth;
			}else{
				formattedMonth = ""+finalMonth;
			}
            String dayString ="";
            if (day < 10) {
                dayString = "0"
                        + day;
            } else {
                dayString = ""
                        + day;
            }

            // set selected date into textview
		//	Button dateBtn = (Button) searchView.findViewById(R.id.dateButton);
			
			selectedDate = new StringBuilder().append(year)
                    .append("-").append(formattedMonth).append("-").append(dayString)
                    .append(" ");
             from_date=selectedDate.toString();

            StringBuilder unFormattedDate = new StringBuilder().append(year)
                    .append("-").append(month+1).append("-").append(day)
                    .append(" ");
			fromDateBtn.setText(unFormattedDate.toString());
		}
	};


	private DatePickerDialog.OnDateSetListener toDatePickerListener
			= new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear,
							  int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			int finalMonth = month+1;
			String formattedMonth;
			if(finalMonth>=1 && finalMonth<=9){
				formattedMonth = "0"+finalMonth;
			}else{
				formattedMonth = ""+finalMonth;
			}
			// set selected date into textview

            String dayString ="";
            if (day < 10) {
                dayString = "0"
                        + day;
            } else {
                dayString = ""
                        + day;
            }
			selectedDate = new StringBuilder().append(year)
					.append("-").append(formattedMonth).append("-").append(dayString)
					.append(" ");
            to_Date=selectedDate.toString();
            StringBuilder unFormattedDate = new StringBuilder().append(year)
                    .append("-").append(month+1).append("-").append(day)
                    .append(" ");
			toDateBtn.setText(unFormattedDate.toString());
		}
	};
	
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
	

	
	
	private TableRow createTableHeader() {
		TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
		tableRowParams.setMargins(1, 1, 1, 1);
		//tableRowParams.weight = 1;

		TableRow tableRow = new TableRow(getActivity());
		LinearLayout ly = new LinearLayout(getActivity());
		ly.setOrientation(LinearLayout.HORIZONTAL);
		ly.setWeightSum(2);


		TextView desc = new TextView(getActivity());
		// textView.setText(String.valueOf(j));
		desc.setBackgroundColor(Color.TRANSPARENT);
		tableRowParams.weight = 2;
		tableRowParams.gravity= Gravity.CENTER_HORIZONTAL;
		tableRowParams.setMargins(16, 5, 160, 5);
		Typeface ty = Typeface.DEFAULT_BOLD ;
		desc.setTypeface(ty);
		desc.setText("Item");
		desc.setTextSize(18);
		ly.addView(desc,tableRowParams);

		TextView status = new TextView(getActivity());
		// textView.setText(String.valueOf(j));
		status.setBackgroundColor(Color.TRANSPARENT);
		tableRowParams.weight = 1;
		status.setText("Status");
		status.setTypeface(ty);
		tableRowParams.gravity= Gravity.CENTER_HORIZONTAL;
		status.setPadding(50, 0, 0, 0);
	//	tableRowParams.setMargins(50, 5, 10, 5);
		status.setTextSize(18);
		ly.addView(status,tableRowParams);

		tableRow.setBackgroundColor(Color.TRANSPARENT);
		tableRowParams.setMargins(0, 0, 0, 0);
		tableRow.addView(ly, tableRowParams);
		return tableRow;
	}
	
	private TableRow createResultsTableRow(String category, String type,String imgUri ){
        TableRow tr_row = new TableRow(getActivity());
      /*  tr_row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));*/
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.bikepooler_img);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        tr_row.addView(imageView);

        TextView desc = new TextView(getActivity());
        desc.setBackgroundColor(Color.TRANSPARENT);
        desc.setText(category);
        tr_row.addView(desc);


        TextView status = new TextView(getActivity());
        status.setBackgroundColor(Color.TRANSPARENT);
        status.setText(type);
        tr_row.addView(status);
        return  tr_row;
    }


	private TableRow createTableRow(String category, String type,String imgUri) {
		TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
		tableRowParams.setMargins(1, 1, 1, 1);
		//tableRowParams.weight = 1;
		LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		TableRow tableRow = new TableRow(getActivity());
		LinearLayout ly = new LinearLayout(getActivity());
		ly.setOrientation(LinearLayout.HORIZONTAL);
        ly.setWeightSum(100);
		ImageView imageView = new ImageView(getActivity());
		//imageView.setImageURI(Uri.parse("file://mnt/sdcard/d2.jpg"));
		//imageView.setImageURI(Uri.parse(imgUri));
		imageView.setImageResource(R.drawable.bikepooler_img);
		//imageView.setBackgroundResource(R.drawable.xhdpi_searchresults);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	//	tableRowParams.weight = 1;
		//tableRowParams.setMargins(5, 5, 5, 5);
        llParams.weight = 40;
		//llParams.gravity= Gravity.LEFT ;
		//tableRowParams.width =  100*(160/240);
		//tableRowParams.height = 100*(160/240);
		ly.addView(imageView, llParams);

		//tableRowParams = new TableRow.LayoutParams();

		TextView desc = new TextView(getActivity());
		// textView.setText(String.valueOf(j));
		desc.setBackgroundColor(Color.TRANSPARENT);
        llParams.weight = 30;
		//tableRowParams.gravity= Gravity.CENTER |Gravity.CENTER_VERTICAL;
		//tableRowParams.setMargins(5, 5, 0, 5);
		desc.setPadding(5, 5, 5, 5);
		desc.setText(category);
		ly.addView(desc,llParams);

		TextView status = new TextView(getActivity());
		// textView.setText(String.valueOf(j));
		status.setBackgroundColor(Color.TRANSPARENT);
        status.setText(type);
        llParams.weight = 30;
		//tableRowParams.gravity= Gravity.RIGHT |Gravity.CENTER_VERTICAL;
		//tableRowParams.setMargins(150, 0, 0, 5);
		//tableRowParams.setMargins(5, 5, 0, 5);
		ly.addView(status,llParams);

		tableRow.setBackgroundColor(Color.TRANSPARENT);
		tableRowParams.setMargins(0, 0, 0, 0);
		tableRow.addView(ly, tableRowParams);
		return tableRow;
	}


	private void setGoogleLocation(){
		location_spinner.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
	}


	public static ArrayList autocomplete(String input) {
		ArrayList resultList = null;
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			sb.append("&components=country:us");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));
			Log.d("BikePlaceFragment", "URL For Offer Ride:::" + sb.toString());
			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			Log.d("BikePlaceFragment","Search Results:::"+jsonResults.toString());
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
				System.out.println("============================================================");
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}
		} catch (JSONException e) {
			Log.e("ReportLossFragment", "Cannot process JSON results", e);
		}
		return resultList;
	}


	class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
		private ArrayList resultList;

		public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public Object getItem(int position) {
			return resultList.get(position);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}


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
					 makeCallToJSONParser(searchResponse);
				 }
             }
         }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.category:
                hideKeyboard();
                launchSelector();
                break;

        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(category.getWindowToken(), 0);
    }


    private void launchSelector(){
       /* InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Make your selection");
        builder.setItems(R.array.array_name, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                category.setText(categoryNames[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void makeWebServiceCall(){
        if(!TextUtils.isEmpty(queryString)){
            new SiiKGetResponseHelper(getActivity(), SearchFragment.this).execute(
                    BikeConstants.SEARCH_GET_SERVICE_URL+"?"+queryString);
            queryString  = "";
        }else {
            new SiiKGetResponseHelper(getActivity(), SearchFragment.this).execute(
                    BikeConstants.SEARCH_GET_SERVICE_URL);
        }
    }

    private void makeCallToJSONParser(String serviceResponse){
       List<SearchDTO> jsonResponseList = new SiiKGetJSONParser().getParseResponse(getActivity(),serviceResponse);
        if(jsonResponseList!=null && jsonResponseList.size()>0){
            //populate list;
            lauchWebserviceResultsList(jsonResponseList);
        }
    }

}
