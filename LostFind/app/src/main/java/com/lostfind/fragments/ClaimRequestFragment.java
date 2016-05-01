package com.lostfind.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lostfind.DTO.SearchDTO;
import com.lostfind.R;
import com.lostfind.WebserviceHelpers.SiiKGetJSONParser;
import com.lostfind.WebserviceHelpers.SiiKGetResponseHelper;
import com.lostfind.WebserviceHelpers.SiiKPUTResponseHelper;
import com.lostfind.WebserviceHelpers.SiiKPostResponseHelper;
import com.lostfind.application.MyApplication;
import com.lostfind.interfaces.SiikReceiveListener;
import com.lostfind.utils.BikeConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClaimRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClaimRequestFragment extends Fragment implements SiikReceiveListener ,View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText additionalInfo;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final String TAG = this.getClass().getSimpleName();
    View claimView = null;
    private boolean isPostRequest = false;
    private boolean isFromReportHistory = false;
    Spinner status_items;
    Button status_selector;
    String[] itemNames;
    public ClaimRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClaimRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClaimRequestFragment newInstance(String param1, String param2) {
        ClaimRequestFragment fragment = new ClaimRequestFragment();
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
        Log.d(TAG, "Clicked Position ItemId::::" + mParam1);
        if(!TextUtils.isEmpty(mParam2)){
             if(mParam2.equalsIgnoreCase("isFromReportHistory")){
                 isFromReportHistory =true;
             }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        claimView =  inflater.inflate(R.layout.fragment_claim_request, container, false);
        itemNames = getResources().getStringArray(R.array.spinner_report_status);

        if(!TextUtils.isEmpty(mParam1)){
            makeWebServiceCall();
        }
        return claimView;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(claimView.getWindowToken(), 0);
    }
    private void makeWebServiceCall(){

        new SiiKGetResponseHelper(getActivity(), ClaimRequestFragment.this,"Fetching item data ....").execute(
                BikeConstants.SEARCH_GET_SERVICE_URL+"/"+mParam1);
    }

    @Override
    public void receiveResult(String result) {
        if(result!=null){
            if(result.equalsIgnoreCase(BikeConstants.WEBSERVICE_NETWORK_FAIL) || result.equalsIgnoreCase("Get Failed")){
                if(!TextUtils.isEmpty(MyApplication.getInstance().getRegistrationResponseMessage())){
                    Toast.makeText(getActivity(), MyApplication.getInstance().getRegistrationResponseMessage(), Toast.LENGTH_LONG).show();
                }
            }else if(result.equalsIgnoreCase("success")){
                if(isPostRequest) {
                    if(!TextUtils.isEmpty(MyApplication.getInstance().getRegistrationResponseMessage())){
                        Toast.makeText(getActivity(), MyApplication.getInstance().getRegistrationResponseMessage(), Toast.LENGTH_LONG).show();
                        isPostRequest = false;
                    }
                }else {
                    if (!TextUtils.isEmpty(MyApplication.getInstance().getSearchResponse())) {
                        //Toast.makeText(getActivity(),MyApplication.getInstance().getRegistrationResponseMessage(),Toast.LENGTH_LONG).show();
                        String searchResponse = MyApplication.getInstance().getSearchResponse();
                        Log.d(TAG, "Response for Item Id::;" + searchResponse);
                        SearchDTO parseDTO = new SiiKGetJSONParser().getParseResponseForItem(getActivity(), searchResponse);
                        //      makeCallToJSONParser(searchResponse);
                        initAndPopulateViews(claimView, parseDTO);
                    }
                }
            }else if(result.equalsIgnoreCase("putsuccess")){
                if(!TextUtils.isEmpty(MyApplication.getInstance().getRegistrationResponseMessage())){
                    Toast.makeText(getActivity(),MyApplication.getInstance().getRegistrationResponseMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }
    }
private void initAndPopulateViews(View v,SearchDTO searchDTO){
String imageURL = searchDTO.getImageURL();
String category = searchDTO.getCategory();
String location  = searchDTO.getLocation();
String info =       searchDTO.getInfo();
    String status = searchDTO.getStatus();
    final String email = searchDTO.getEmail();
    String des = searchDTO.getItemDescription();
    ScrollView claimInputLayout = (ScrollView)v.findViewById(R.id.find_loss_input);
    EditText desc = (EditText)claimInputLayout.findViewById(R.id.find_loss_desc);
    if(!TextUtils.isEmpty(des)) {
        desc.setText(des);
    }
    AutoCompleteTextView loc = (AutoCompleteTextView)claimInputLayout.findViewById(R.id.find_loss_location);
    if(!TextUtils.isEmpty(location)){
        loc.setText(location);
    }

    Button category_btn = (Button)claimInputLayout.findViewById(R.id.find_loss_selector);
Log.d(TAG,"Catergory::::"+category);

    if(!TextUtils.isEmpty(category)){
        category_btn.setText(category);
    }

     additionalInfo = (EditText)claimInputLayout.findViewById(R.id.additional_info);
   /* if(!TextUtils.isEmpty(info)){
        additionalInfo.setText(info);
       *//* additionalInfo.setEnabled(false);
        additionalInfo.setClickable(false);*//*
    }*/

    if(!TextUtils.isEmpty(imageURL)){
        //  ImageView siikImage = (ImageView)claimInputLayout.findViewById(R.id.iv_upload);
        new LoadImage().execute(imageURL);
    }

Button claim_btn = (Button)claimInputLayout.findViewById(R.id.find_loss_report);
    if(status.equalsIgnoreCase("found")){
        claim_btn.setText("Notify");
    }
    claim_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard();
            if(isFromReportHistory) {
                makePutRequest();
            }else {
                if (!TextUtils.isEmpty(additionalInfo.getText().toString())) {
                    Log.d(TAG, "Commnets:::" + additionalInfo.getText().toString());
                    if (additionalInfo.getText().toString().length() >= 10) {
                        makeWebServiceCall(additionalInfo.getText().toString());

                    } else {
                        Toast.makeText(getActivity(), "Minimum Character Length is 10", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Enter Comments  to proceed further", Toast.LENGTH_SHORT).show();
                }
            }
       //     Toast.makeText(getActivity(),"To be Claimed to"+email,Toast.LENGTH_LONG).show();

        }
    });

    if(isFromReportHistory){
       /* status_items = (Spinner)claimInputLayout.findViewById(R.id.status_spinner);
        status_items.setVisibility(View.VISIBLE);*/
        claim_btn.setText("Claim  Status");
        additionalInfo.setVisibility(View.GONE);
       /* String [] spin_arry = getResources().getStringArray(R.array.spinner_report_status);
        SpinnerAdapter mAdapter = new CustomArrayAdapter<CharSequence>(getActivity(), spin_arry);
        status_items.setAdapter(mAdapter);*/

        status_selector  = (Button)claimInputLayout.findViewById(R.id.status_item);
        status_selector.setVisibility(View.GONE);
        status_selector.setText(status);
        status_selector.setOnClickListener(ClaimRequestFragment.this);


    }
}
private void  makePutRequest(){
    new SiiKPUTResponseHelper(getActivity(),ClaimRequestFragment.this,true).execute(BikeConstants.COMMENT_ITEM_URL+"/"+mParam1+"/"+"claim");
}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.status_item:
                launchSelector();
                break;
        }
    }

    private void launchSelector(){
       /* InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Make your selection");
        builder.setItems(R.array.spinner_report_status, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                status_selector.setText(itemNames[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
    static class CustomArrayAdapter<T> extends ArrayAdapter<T>
    {
        public CustomArrayAdapter(Context ctx, T [] objects)
        {
            super(ctx, android.R.layout.simple_spinner_item, objects);
        }

        //other constructors

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getView(position, convertView, parent);

            //we know that simple_spinner_item has android.R.id.text1 TextView:

        /* if(isDroidX) {*/
            TextView text = (TextView)view.findViewById(android.R.id.text1);
            text.setTextColor(Color.RED);//choose your color :)
            text.setTextSize(20);
        /*}*/

            return view;

        }
    }
private void makeWebServiceCall(String comment){
    try{
        JSONObject commentJson = new JSONObject();

            //emailPwdJson.put("username", username);
        commentJson.put("info", comment);
        isPostRequest = true;
    new SiiKPostResponseHelper(getActivity(),ClaimRequestFragment.this,commentJson,true,"Posting Comment....").execute(BikeConstants.COMMENT_ITEM_URL+"/"+mParam1+"/"+"comment");
    }catch (JSONException e){
        e.printStackTrace();
    }catch (Exception e){

        e.printStackTrace();
    }
}
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap;
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Image ....");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                ImageView imageView = (ImageView) claimView
                        .findViewById(R.id.iv_upload);
                //   imageView.setImageBitmap(bitmap);
                imageView.setImageBitmap(image);
                pDialog.dismiss();

            }else{

                pDialog.dismiss();
                Toast.makeText(getActivity(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }
    ///items/:id
}
