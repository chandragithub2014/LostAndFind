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
import java.util.Locale;

import com.lostfind.DBManager.SiikDBHelper;
import com.lostfind.R;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

public class SearchFragment extends Fragment {
	private static final String TAG = "SearchFragment";
	private int PICK_IMAGE_REQUEST = 1;
	private View searchView;
	AutoCompleteTextView	location_spinner;
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyDixji8saFmpOFmSnKXY6-uP_2mnDYG3Js";
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
		final Spinner category = (Spinner) searchView
				.findViewById(R.id.category);
		final Spinner location = (Spinner) searchView
				.findViewById(R.id.location);
		final EditText comments= (EditText) searchView
				.findViewById(R.id.search_comments);
			location_spinner = (AutoCompleteTextView)searchView.findViewById(R.id.find_loss_location);



		Button submit = (Button) searchView.findViewById(R.id.search_btn);
		final Calendar c = Calendar
					.getInstance();	
		final Button dateBtn = (Button) searchView.findViewById(R.id.dateButton);
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
		});

		
		rootLayout = (LinearLayout) searchView.findViewById(R.id.tablelayout);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				  rootLayout.removeAllViews();
				  ScrollView sv = new ScrollView(getActivity());
				   /*  VerticalScrollView hsv = new HorizontalScrollView(getActivity());
				     hsv.addView(tableLayout);*/
				  
				 	String strCategory = category.getSelectedItem().toString();
					String strlocation = location_spinner.getText().toString();//location.getSelectedItem().toString();
					Cursor resultCursor = new SiikDBHelper().getSearchResults(strCategory,strlocation,getActivity());
					TableLayout tableLayout  = createTableLayout(resultCursor);
					
				   sv.addView(tableLayout);
				   rootLayout.addView(sv);
				
			}
		});
        setGoogleLocation();
		return searchView;
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

			// set selected date into textview
			Button dateBtn = (Button) searchView.findViewById(R.id.dateButton);
			
			selectedDate = new StringBuilder().append(month + 1)
					   .append("-").append(day).append("-").append(year)
					   .append(" ");
			dateBtn.setText(selectedDate.toString());
		}
	};

	
	
	private TableLayout createTableLayout(Cursor resultCursor) {
	     TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
	     tableLayoutParams.setMargins(5, 5, 5, 5);
	     TableLayout tableLayout = new TableLayout(getActivity());
	     tableLayout.setBackgroundColor(Color.BLACK);

	     if(resultCursor != null){
	    	 if(resultCursor.moveToFirst()){
	    		   tableLayout.addView(createTableHeader(), tableLayoutParams);
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
				         tableLayout.addView(createTableRow(category,type,imageURL), tableLayoutParams);
						
					   } while (resultCursor.moveToNext());
							   
					}
	     }else{
	    	 Toast.makeText(getActivity(), "No records Found", Toast.LENGTH_LONG).show();
	     }
	     
	       // tableLayout.addView(createTableHeader(), tableLayoutParams);
	        // tableLayout.addView(createTableRow2(), tableLayoutParams);
	    // }
	         tableLayout.setBackgroundColor(Color.TRANSPARENT);
	     return tableLayout;
	}
	

	
	
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
	
	
	
	private TableRow createTableRow(String category, String type,String imgUri) {
		TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
		tableRowParams.setMargins(1, 1, 1, 1);
		//tableRowParams.weight = 1;
		LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(200,200);
		TableRow tableRow = new TableRow(getActivity());
		LinearLayout ly = new LinearLayout(getActivity());
		ly.setOrientation(LinearLayout.HORIZONTAL);

		ImageView imageView = new ImageView(getActivity());
		//imageView.setImageURI(Uri.parse("file://mnt/sdcard/d2.jpg"));
		imageView.setImageURI(Uri.parse(imgUri));
		//imageView.setBackgroundResource(R.drawable.xhdpi_searchresults);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	//	tableRowParams.weight = 1;
		//tableRowParams.setMargins(5, 5, 5, 5);
		llParams.gravity= Gravity.LEFT ;
		//tableRowParams.width =  100*(160/240);
		//tableRowParams.height = 100*(160/240);
		ly.addView(imageView, llParams);

		//tableRowParams = new TableRow.LayoutParams();

		TextView desc = new TextView(getActivity());
		// textView.setText(String.valueOf(j));
		desc.setBackgroundColor(Color.TRANSPARENT);
		tableRowParams.gravity= Gravity.LEFT |Gravity.CENTER_VERTICAL;
		tableRowParams.setMargins(5, 5, 100, 5);
		desc.setPadding(5, 5, 160, 5);
		desc.setText(category);
		ly.addView(desc,tableRowParams);

		TextView status = new TextView(getActivity());
		// textView.setText(String.valueOf(j));
		status.setBackgroundColor(Color.TRANSPARENT);
		status.setText(type);
		tableRowParams.gravity= Gravity.LEFT |Gravity.CENTER_VERTICAL;
		tableRowParams.setMargins(150, 0, 0, 5);
		//tableRowParams.setMargins(5, 5, 0, 5);
		ly.addView(status,tableRowParams);

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
}
