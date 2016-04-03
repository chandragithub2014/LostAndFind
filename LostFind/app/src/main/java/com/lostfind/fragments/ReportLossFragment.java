package com.lostfind.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.lostfind.DBManager.SiikDBHelper;
import com.lostfind.R;
import com.lostfind.WebserviceHelpers.SiiKPostResponseHelper;
import com.lostfind.WebserviceHelpers.SiiKPostUploadResponseHelper;
import com.lostfind.application.MyApplication;
import com.lostfind.interfaces.SiikReceiveListener;
import com.lostfind.slidingmenu.SlidingMenuActivity;
import com.lostfind.utils.BikeConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportLossFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportLossFragment extends Fragment implements View.OnClickListener,SiikReceiveListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "ReportLossFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private boolean mParam1;
    private String mParam2;

    Button spinnerType;
View view = null;
    String[] itemNames,locationNames;
    EditText description;
    AutoCompleteTextView location_spinner;
    Button date_btn;
    EditText additionalInfo,rewardOption;
    ImageButton footerImage_btn;

    private SimpleDateFormat dateFormatter;

    static final int DATE_PICKER_ID = 1111;

    private DatePickerDialog fromDatePickerDialog;
    protected AlertDialog myAlert;
  Button changeImg;
    private int PICK_IMAGE_REQUEST = 1;
private String imageUrl="";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDixji8saFmpOFmSnKXY6-uP_2mnDYG3Js";
    Button fromDate,toDate;
    boolean isFromDateSet,isToDateSet;
    RelativeLayout root_layout;
    String[] cameraGalleryitemNames;
    private int PICK_CAMERA_REQUEST = 2;

    public ReportLossFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param isReportLoss Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportLossFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportLossFragment newInstance(boolean isReportLoss, String param2) {
        ReportLossFragment fragment = new ReportLossFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isReportLoss);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getBoolean(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        cameraGalleryitemNames = getResources().getStringArray(R.array.array_gallery_camera);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       itemNames = getResources().getStringArray(R.array.array_name);
        locationNames = getResources().getStringArray(R.array.array_location);
        view =  inflater.inflate(R.layout.fragment_report_loss, container, false);

        Toolbar mToolBar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        TextView toolBarTitle = (TextView)mToolBar.findViewById(R.id.title);
        toolBarTitle.setText("SiiK");
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        footerImage_btn = (ImageButton)view.findViewById(R.id.footer_img_btn);
        footerImage_btn.setOnClickListener(this);
        root_layout = (RelativeLayout)view.findViewById(R.id.root_layout);
        root_layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(view);
                return false;
            }
        });
        initInputFieldView(view);
        return view;
    }
    /**
     * Hides virtual keyboard
     *
     * @author chandra
     */
    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void initInputFieldView(View v){

        ScrollView inputScrollView = (ScrollView)v.findViewById(R.id.find_loss_input);
         spinnerType = (Button)inputScrollView.findViewById(R.id.find_loss_selector);
        description = (EditText)inputScrollView.findViewById(R.id.find_loss_desc);
        spinnerType.setOnClickListener(this);
        location_spinner = (AutoCompleteTextView)inputScrollView.findViewById(R.id.find_loss_location);
        location_spinner.setOnClickListener(this);
        LinearLayout dateLayout = (LinearLayout)inputScrollView.findViewById(R.id.to_from_date);
        fromDate  = (Button)dateLayout.findViewById(R.id.from_date);
        toDate = (Button)dateLayout.findViewById(R.id.to_date);
      //  date_btn  = (Button)inputScrollView.findViewById(R.id.find_loss_date);
        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        additionalInfo = (EditText)inputScrollView.findViewById(R.id.additional_info);
        rewardOption = (EditText)inputScrollView.findViewById(R.id.find_loss_reward_option);
        Button loss_report_btn = (Button)inputScrollView.findViewById(R.id.find_loss_report);
        loss_report_btn.setOnClickListener(this);
        changeImg = (Button)v.findViewById(R.id.chng_btn);
        changeImg.setOnClickListener(this);
        setGoogleLocation();
//additionalInfo,rewardOption
    }

    private void setGoogleLocation(){
        location_spinner.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
          case R.id.find_loss_selector:
                hideKeyboard();
                launchSelector();
                break;
           /* case R.id.find_loss_location:
                launchLocationSelector();
                break;*/
            case R.id.from_date:
                hideKeyboard(v);
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
            case R.id.to_date:
                hideKeyboard(v);
                final Calendar toC = Calendar
                        .getInstance();

                int toyear =  toC.get(Calendar.YEAR);
                int tomonth =  toC.get(Calendar.MONTH);
                int today =   toC.get(Calendar.DAY_OF_MONTH);
                myAlert = new DatePickerDialog(
                        getActivity(),
                        toDateSetListener,
                        toyear,
                        tomonth,
                        today);
                //    setDatePicker();

                myAlert.show();
                break;
            case R.id.find_loss_report:
                Log.d(TAG,"Desc:::"+description.getText().toString());
if(isFromDateSet) {
    formJSONPayLoad();
  /*  if(BikeConstants.IS_WEB_SERVICE_ENABLED){
        formJSONPayLoad();
    }else {
        popualteDateInDB();
    }*/
}
                else  if(TextUtils.isEmpty(description.getText().toString()) || TextUtils.isEmpty(spinnerType.getText().toString())|| TextUtils.isEmpty(location_spinner.getText().toString())){
    Toast.makeText(getActivity(),"Fill All fields to Report",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.footer_img_btn:
                laucnchSlidingMenu();
                break;
            case R.id.chng_btn:
                /*Intent intent = new Intent();
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        PICK_IMAGE_REQUEST);*/
                launchCameraGallerySelector();
                break;
        }
    }

    private void launchCameraGallerySelector(){
       /* InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Make your selection");
        builder.setItems(R.array.array_gallery_camera, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                if(cameraGalleryitemNames[item].equalsIgnoreCase("Gallery")){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            PICK_IMAGE_REQUEST);
                }else if(cameraGalleryitemNames[item].equalsIgnoreCase("Camera")){
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, PICK_CAMERA_REQUEST);
                }
                //     changeImage.setText(itemNames[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == Activity.RESULT_OK && data != null
                && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity()
                        .getContentResolver(), uri);
                ImageView imageView = (ImageView) view
                        .findViewById(R.id.iv_upload);
                imageView.setImageBitmap(bitmap);
      //          Toast.makeText(getActivity(),"URI::::"+uri,Toast.LENGTH_LONG).show();
                imageUrl = ""+uri;
                new SiiKPostUploadResponseHelper(getActivity(),ReportLossFragment.this,bitmap).execute("http://52.38.114.74:8000/upload");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == PICK_CAMERA_REQUEST
                && resultCode == Activity.RESULT_OK && data != null
                && data.getData() != null) {
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            ImageView imageView = (ImageView) view
                    .findViewById(R.id.iv_upload);
            imageView.setImageBitmap(bp);

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

            fromDate
                    .setText(inputFomat_val);
            isFromDateSet =  true;



        }

    };

    private DatePickerDialog.OnDateSetListener toDateSetListener = new DatePickerDialog.OnDateSetListener() {

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

            toDate
                    .setText(inputFomat_val);
            isToDateSet =  true;



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

    private void formJSONPayLoad(){

        /*
        description, category, location, info, type, imageurl
         */
        Log.d(TAG,"In makePostWebserviceCall()");
        JSONObject reportLostFoundJson = new JSONObject();
        try {
            reportLostFoundJson.put("description", description.getText().toString());
            reportLostFoundJson.put("location", location_spinner.getText().toString());
            reportLostFoundJson.put("category", spinnerType.getText().toString());
            reportLostFoundJson.put("info", additionalInfo.getText().toString());
            if(mParam1){
                reportLostFoundJson.put("type", "lost");
            }else{
                reportLostFoundJson.put("type", "found");
            }

           // reportLostFoundJson.put("imageurl", "");
            if(!TextUtils.isEmpty(toDate.getText().toString())) {
                reportLostFoundJson.put("to_date", toDate.getText().toString());
            }else{
                reportLostFoundJson.put("to_date", "");
            }
            reportLostFoundJson.put("from_date",fromDate.getText().toString());

            makeWebServiceCall(reportLostFoundJson);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void makeWebServiceCall(JSONObject jsonPayLoad){
         new SiiKPostResponseHelper(getActivity(), ReportLossFragment.this,jsonPayLoad ,mParam1).execute(BikeConstants.REPORT_POST_SERVICE_URL);
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

    if(!TextUtils.isEmpty(toDate.getText().toString())){
        cv.put("date",toDate.getText().toString());
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
    cv.put("username","UserName"+System.currentTimeMillis());
  //  cv.put("imageurl",imageUrl);
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

    private void laucnchSlidingMenu(){
        Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("position",4);
        i.putExtras(bundle);
        startActivity(i);
        getActivity().finish();
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
    public void receiveResult(String result) {
        Log.d(TAG, "Received Result" + result);
        if(result.equalsIgnoreCase("Success")){
            if(!TextUtils.isEmpty(MyApplication.getInstance().getRegistrationResponseMessage())){
                Toast.makeText(getActivity(),MyApplication.getInstance().getRegistrationResponseMessage(),Toast.LENGTH_LONG).show();
            }
          //  callSlidingMenu();
        }else {
            if(!TextUtils.isEmpty(MyApplication.getInstance().getRegistrationResponseMessage())){
                Toast.makeText(getActivity(),MyApplication.getInstance().getRegistrationResponseMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
}
