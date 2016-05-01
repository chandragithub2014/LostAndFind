package com.lostfind.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lostfind.R;
import com.lostfind.SharedPreferencesUtils;
import com.lostfind.WebserviceHelpers.SiiKGetResponseHelper;
import com.lostfind.WebserviceHelpers.SiiKImageUploadHelper;
import com.lostfind.WebserviceHelpers.SiiKPUTResponseHelper;
import com.lostfind.WebserviceHelpers.SiiKPostResponseHelper;
import com.lostfind.application.MyApplication;
import com.lostfind.interfaces.SiikReceiveListener;
import com.lostfind.slidingmenu.SlidingMenuActivity;
import com.lostfind.utils.BikeConstants;
import com.lostfind.utils.RealPathUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileFragment extends Fragment implements  OnClickListener,SiikReceiveListener{
    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_CAMERA_REQUEST = 2;
    private View userProfileView;
    SharedPreferencesUtils sharedPreferencesUtils;
    EditText firstName,lastName,emailId,phoneNumber,userId;
    Button submit;
    private boolean isViewEnabled = false;
    String[] itemNames;
    Button changeImage;
    String profilePassword = "";

    AutoCompleteTextView reg_loc;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDixji8saFmpOFmSnKXY6-uP_2mnDYG3Js";

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
                    Log.d("UserProfileFragment","Profile Info::"+searchResponse);
                    parseProfileJSON(searchResponse);
                    // makeCallToJSONParser(searchResponse);
                }
            }else if(result.equalsIgnoreCase("putsuccess")){
                if(!TextUtils.isEmpty(MyApplication.getInstance().getRegistrationResponseMessage())){
                    Toast.makeText(getActivity(),MyApplication.getInstance().getRegistrationResponseMessage(),Toast.LENGTH_LONG).show();
                }
            }else if(result.equalsIgnoreCase("uploadSuccess")){
                Log.d("UserProfileFragment","ImageURL:::"+MyApplication.getInstance().getImageURL());
            }else if(!TextUtils.isEmpty(MyApplication.getInstance().getRegistrationResponseMessage())){
                Toast.makeText(getActivity(),MyApplication.getInstance().getRegistrationResponseMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void parseProfileJSON(String profileJSON){
        try {
            JSONObject profileJSONObject = new JSONObject(profileJSON);
            String profileImage = "";
            String profileEmail = profileJSONObject.getString("email");
             profilePassword = profileJSONObject.getString("password");
            String profileName = profileJSONObject.getString("name");
            String phoneNum =  profileJSONObject.getString("phone");
            if(profileJSONObject.getString("imageurl")!=null && !profileJSONObject.getString("imageurl").equalsIgnoreCase("null")){
             profileImage = profileJSONObject.getString("imageurl");
                new LoadImage().execute(profileImage);
            }
            String location_user = profileJSONObject.getString("location");
            reg_loc.setText(location_user);
            firstName.setText(profileName);
            lastName.setText(profileName);
            emailId.setText(profileEmail);
            phoneNumber.setText(phoneNum);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void makeWebserviceCall(){
        new SiiKGetResponseHelper(getActivity(), UserProfileFragment.this,"Fetching UserProfile ....").execute(
                BikeConstants.USER_PROFILE_GET_SERVICE_URL);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesUtils = new SharedPreferencesUtils();
        itemNames = getResources().getStringArray(R.array.array_gallery_camera);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        userProfileView = inflater.inflate(R.layout.ly_user_profile, container,
                false);
        Toolbar mToolBar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        TextView toolBarTitle = (TextView)mToolBar.findViewById(R.id.title);
        toolBarTitle.setText("SiiK");
        reg_loc = (AutoCompleteTextView)userProfileView.findViewById(R.id.register_loc);
        firstName = (EditText) userProfileView
                .findViewById(R.id.firstName);
        lastName = (EditText) userProfileView
                .findViewById(R.id.lastName);
        emailId = (EditText) userProfileView


                .findViewById(R.id.emailId);
        phoneNumber = (EditText) userProfileView
                .findViewById(R.id.phoneNumber);
        userId = (EditText) userProfileView.findViewById(R.id.userId);
        userId.setVisibility(View.GONE);
        changeImage = (Button) userProfileView
                .findViewById(R.id.change_image);
        submit = (Button) userProfileView.findViewById(R.id.save_btn);
        submit.setOnClickListener(this);
        disableView();
      //  prepopulateDataIfExists();
        ImageButton	footerImage_btn = (ImageButton)userProfileView.findViewById(R.id.footer_img_btn);
        footerImage_btn.setOnClickListener(this);
        changeImage.setOnClickListener(this);
      /*  changeImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image*//**//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        PICK_IMAGE_REQUEST);


                //   launchSelector();
              *//*  Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, PICK_CAMERA_REQUEST);*//*

            }
        });*/

       /* String userProfilePhoneNum = "";
        if(!TextUtils.isEmpty(sharedPreferencesUtils.getStringPreferences(getActivity(),"PhoneNumber"))){
            userProfilePhoneNum = sharedPreferencesUtils.getStringPreferences(getActivity(),"PhoneNumber");
            phoneNumber.setText(userProfilePhoneNum);
        }

        String name = "";
        if(!TextUtils.isEmpty(sharedPreferencesUtils.getStringPreferences(getActivity(),"name"))){
            name = sharedPreferencesUtils.getStringPreferences(getActivity(),"name");
            firstName.setText(name);
        }


        String loginType = sharedPreferencesUtils.getStringPreferences(getActivity(),"loginType");
        if(loginType.equalsIgnoreCase("emailProfile")){
            String emailJSON = sharedPreferencesUtils.getStringPreferences(getActivity(),"email");
            if(!TextUtils.isEmpty(emailJSON)){
                try {
                    JSONObject emailJSONObj = new JSONObject(emailJSON);
                    String email = (String) emailJSONObj.get("emailId");
                    emailId.setText(email);

                }catch (JSONException e){
                    e.printStackTrace();;
                }
            }

        }

        if(loginType.equalsIgnoreCase("facebookprofile")){
            String fbookJSON = sharedPreferencesUtils.getStringPreferences(getActivity(),"facebook");
            if(!TextUtils.isEmpty(fbookJSON)){
                try {
                    JSONObject emailJSONObj = new JSONObject(fbookJSON);
                    String email = (String) emailJSONObj.get("profileemail");
                    emailId.setText(email);
                    String profileName = (String) emailJSONObj.get("profilename");
                    firstName.setText(profileName);

                }catch (JSONException e){
                    e.printStackTrace();;
                }

            }
        }


        if(loginType.equalsIgnoreCase("googleprofile")){
            String gmailJSON = sharedPreferencesUtils.getStringPreferences(getActivity(),"gmail");
            if(!TextUtils.isEmpty(gmailJSON)){
                try {
                    JSONObject emailJSONObj = new JSONObject(gmailJSON);
                    String email = (String) emailJSONObj.get("profileemail");
                    emailId.setText(email);
                    String profileName = (String) emailJSONObj.get("profilename");
                    firstName.setText(profileName);

                }catch (JSONException e){
                    e.printStackTrace();;
                }

            }
        }

*/
		/* Hard coded Data */
        //	firstName.setText("Praveen");
        //	lastName.setText("");
        //	emailId.setText("praveen.kumar@gmail.com");
        //	phoneNumber.setText("+11003333");
        //	userId.setText("");


        setGoogleLocation();
        makeWebserviceCall();
        return userProfileView;
    }

    private void setGoogleLocation(){
        reg_loc.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
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
            Log.e("Registration", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("Registration", "Error connecting to Places API", e);
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
    private void disableView(){
        //     firstName,lastName,emailId,phoneNumber,userId
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        emailId.setEnabled(false);
        phoneNumber.setEnabled(false);
        userId.setEnabled(false);


        firstName.setClickable(false);
        lastName.setClickable(false);
        emailId.setClickable(false);
        phoneNumber.setClickable(false);
        userId.setClickable(false);

        firstName.setTextColor(Color.parseColor("#9b9b9b"));
        lastName.setTextColor(Color.parseColor("#9b9b9b"));
        emailId.setTextColor(Color.parseColor("#9b9b9b"));
        phoneNumber.setTextColor(Color.parseColor("#9b9b9b"));
        userId.setTextColor(Color.parseColor("#9b9b9b"));
        reg_loc.setTextColor(Color.parseColor("#9b9b9b"));

    }

    private void enableView(){
        //     firstName,lastName,emailId,phoneNumber,userId
        firstName.setEnabled(true);
        lastName.setEnabled(true);
       // emailId.setEnabled(true);
        phoneNumber.setEnabled(true);
        userId.setEnabled(true);


        firstName.setClickable(true);
        lastName.setClickable(true);
        emailId.setClickable(true);
        phoneNumber.setClickable(true);
        userId.setClickable(true);

        firstName.setTextColor(Color.parseColor("#000000"));
        lastName.setTextColor(Color.parseColor("#000000"));
     //   emailId.setTextColor(Color.parseColor("#9b9b9b"));
        phoneNumber.setTextColor(Color.parseColor("#000000"));
        userId.setTextColor(Color.parseColor("#000000"));
        reg_loc.setTextColor(Color.parseColor("#000000"));

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "requestCode::" + requestCode);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == Activity.RESULT_OK && data != null
                && data.getData() != null) {
        String realPath = "";
            Log.d("UserPRofile","Build.VERSION.SDK_INT"+Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT <=19) {
                Cursor cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null);
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                 realPath = cursor.getString(idx);
            }else{
                  realPath = RealPathUtil.getRealPathFromURI_API19(getActivity(), data.getData());
            }
            Log.d("UserProfileFrag","RealPath::::"+realPath);
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity()
                        .getContentResolver(), uri);
                ImageView imageView = (ImageView) userProfileView
                        .findViewById(R.id.user_image);
                imageView.setImageBitmap(bitmap);
                new SiiKImageUploadHelper(getActivity(),UserProfileFragment.this,realPath,"avatar").execute("http://52.38.114.74:8000/upload/avatar");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == PICK_CAMERA_REQUEST
                && resultCode == Activity.RESULT_OK && data != null
                && data.getData() != null) {
            Cursor cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String realPath =  cursor.getString(idx);
            Log.d("UserProfile","Real Path:::"+realPath);
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            ImageView imageView = (ImageView) userProfileView
                    .findViewById(R.id.user_image);
            imageView.setImageBitmap(bp);

            new SiiKImageUploadHelper(getActivity(),UserProfileFragment.this,realPath,"avatar").execute("http://52.38.114.74:8000/upload/avatar");

        }
    }

    private void laucnchSlidingMenu(){
        Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("position", 4);
        i.putExtras(bundle);
        startActivity(i);
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.footer_img_btn:
                laucnchSlidingMenu();
                break;
            case R.id.save_btn:
                performButtonAction();
                break;
            case R.id.change_image:
                checkForPermissions();

                break;
        }
    }

    private void checkForPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getActivity())) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                // continue with your code
                launchSelector();
                Log.d("TAG","Can write");
            }
        }else{
            Log.d("TAG","Build.VERSION.SDK_INT <= Build.VERSION_CODES.M");
            launchSelector();
        }
        // int hasWriteExternalStoragePermission  = getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //if(getActivity().checkSelfPermission(Manifest.Per))
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    launchSelector();
                    //    writeToXMLFile(resultXML);
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }

    private void launchSelector(){
       /* InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Make your selection");
        builder.setItems(R.array.array_gallery_camera, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                if(itemNames[item].equalsIgnoreCase("Gallery")){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            PICK_IMAGE_REQUEST);
                }else if(itemNames[item].equalsIgnoreCase("Camera")){
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, PICK_CAMERA_REQUEST);
                }
                //     changeImage.setText(itemNames[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void performButtonAction(){
        if(!isViewEnabled){
            enableView();
            isViewEnabled = true;
        }else{
            createAndSaveProfileObject();
            Toast.makeText(getActivity(),"Profile Updated",Toast.LENGTH_LONG).show();
            disableView();
        }
    }

    private void createAndSaveProfileObject(){
        String userProfileFirstName =  firstName.getText().toString();
        String userProfileLastName = lastName.getText().toString();
        String userProfileEmail = emailId.getText().toString();
        String userPhoneNum = phoneNumber.getText().toString();
        String userProfileUserId = userId.getText().toString();
        try {
            JSONObject profileJson = new JSONObject();
            profileJson.put("name", userProfileFirstName);
          //  profileJson.put("password", profilePassword);
          //  profileJson.put("email", userProfileEmail);
            profileJson.put("phone", userPhoneNum);
            profileJson.put("location",reg_loc.getText().toString());
          //  profileJson.put("userid", userProfileUserId);
            if(!TextUtils.isEmpty(MyApplication.getInstance().getImageURL())) {
                profileJson.put("imageurl", MyApplication.getInstance().getImageURL());
            }
            makePutWebServiceCall(profileJson);
        } catch (JSONException ej){
                ej.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
    /*    String loginType = sharedPreferencesUtils.getStringPreferences(getActivity(), "loginType");

        try {
            JSONObject profileJson = new JSONObject();
            profileJson.put("firstName",userProfileFirstName);
            profileJson.put("lastName",userProfileLastName);
            profileJson.put("email", userProfileEmail);
            profileJson.put("phone", userPhoneNum);
            profileJson.put("userid",userProfileUserId);
            JSONObject finalJSON = new JSONObject();
            Log.d("TAG", "FinalJSON:::" + finalJSON.toString());
            if(loginType.equalsIgnoreCase("facebookprofile")){
                sharedPreferencesUtils.saveStringPreferences(getActivity(),"userProfileFaceBook",profileJson.toString());
            }else if(loginType.equalsIgnoreCase("googleprofile")){
                sharedPreferencesUtils.saveStringPreferences(getActivity(),"userProfileGoogle",profileJson.toString());
            }else if(loginType.equalsIgnoreCase("emailProfile")){
                sharedPreferencesUtils.saveStringPreferences(getActivity(),"userProfileEmail",profileJson.toString());
            }

        }catch (JSONException ej){
            ej.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }

*/
        //facebookprofile
        //googleprofile

    }
 private void makePutWebServiceCall(JSONObject jsonPayload){

     new SiiKPUTResponseHelper(getActivity(),UserProfileFragment.this,jsonPayload).execute(BikeConstants.USER_PROFILE_GET_SERVICE_URL);
}

    private void prepopulateDataIfExists(){
        String profileJSONBasedOnLogin = "";
        String loginType  =  sharedPreferencesUtils.getStringPreferences(getActivity(), "loginType");
        if(loginType.equalsIgnoreCase("facebookprofile")){
            profileJSONBasedOnLogin = sharedPreferencesUtils.getStringPreferences(getActivity(),"userProfileFaceBook");
        }else if(loginType.equalsIgnoreCase("googleprofile")){
            profileJSONBasedOnLogin = sharedPreferencesUtils.getStringPreferences(getActivity(),"userProfileGoogle");
        }else if(loginType.equalsIgnoreCase("emailProfile")){
            profileJSONBasedOnLogin = sharedPreferencesUtils.getStringPreferences(getActivity(),"userProfileEmail");
        }

        if(!TextUtils.isEmpty(profileJSONBasedOnLogin)) {
            try {
                JSONObject profileJSONObject = new JSONObject(profileJSONBasedOnLogin);

                if(profileJSONObject!=null){

                    Log.d("UserProfileFragment","PrepopuateJSON:::"+profileJSONObject.toString());
                    firstName.setText((String) profileJSONObject.get("firstName"));
                    lastName.setText((String) profileJSONObject.get("lastName"));
                    emailId.setText((String) profileJSONObject.get("email"));
                    phoneNumber.setText((String) profileJSONObject.get("phone"));
                    userId.setText((String) profileJSONObject.get("userid"));


                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                ImageView imageView = (ImageView) userProfileView
                        .findViewById(R.id.user_image);
             //   imageView.setImageBitmap(bitmap);
                imageView.setImageBitmap(image);
                pDialog.dismiss();

            }else{

                pDialog.dismiss();
                Toast.makeText(getActivity(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
