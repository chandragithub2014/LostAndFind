package com.lostfind.fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.lostfind.MainActivity;
import com.lostfind.R;
import com.lostfind.SharedPreferencesUtils;
import com.lostfind.WebserviceHelpers.SiiKPostResponseHelper;
import com.lostfind.application.MyApplication;
import com.lostfind.interfaces.SiikReceiveListener;
import com.lostfind.slidingmenu.SlidingMenuActivity;
import com.lostfind.utils.BikeConstants;
import com.lostfind.utils.EmailValidator;
import com.lostfind.utils.PasswordValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener,SiikReceiveListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String TAG = this.getClass().getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    int mContainerId = -1;
    LinearLayout loginLayout;
    EditText userName,passWord,retypePassword,mobileNum,name;
    Button signUp;
    AutoCompleteTextView reg_loc;
    private EmailValidator emailValidator;
    private PasswordValidator passwordValidator;
    SharedPreferencesUtils prefs;



    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDixji8saFmpOFmSnKXY6-uP_2mnDYG3Js";

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
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
        emailValidator = new EmailValidator();
        passwordValidator = new PasswordValidator();
        prefs = new SharedPreferencesUtils();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContainerId = container.getId();
        View view =  inflater.inflate(R.layout.siikregistrationlayout, container, false);
        initViews(view);
        return  view;
    }


    private void initViews(View view){
       // loginLayout = (LinearLayout)view.findViewById(R.id.signup_pooler);
        name  = (EditText)view.findViewById(R.id.reg_name);
        userName  = (EditText)view.findViewById(R.id.register_email);
        passWord = (EditText)view.findViewById(R.id.register_pwd);
        retypePassword = (EditText)view.findViewById(R.id.retype_pwd);
        mobileNum = (EditText)view.findViewById(R.id.user_mobile);
        signUp = (Button)view.findViewById(R.id.signup);
        signUp.setOnClickListener(this);
        reg_loc = (AutoCompleteTextView)view.findViewById(R.id.register_loc);
        setGoogleLocation();
    }


    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.signup:
              if(!TextUtils.isEmpty(name.getText().toString())) {
                  if (!TextUtils.isEmpty(userName.getText().toString())) {
                      boolean isEmailValid = validateEmail(userName.getText().toString());

                      if (isEmailValid) {

                          if (!TextUtils.isEmpty(passWord.getText().toString())) {
                              //Validate Password
                              String passWordToValidate = passWord.getText().toString();
                              if (passWordToValidate.length() < 6 || passWordToValidate.length() > 20) {
                                  Toast.makeText(getActivity(), "Password must be  between 6 to 20 characters", Toast.LENGTH_LONG).show();
                              } else {
                                  boolean isPassValid = validatePassword(passWord.getText().toString());
                                  if (isPassValid) {
                                      if (!TextUtils.isEmpty(retypePassword.getText().toString())) {
                                          String password = passWord.getText().toString();
                                          String reTypePassword = retypePassword.getText().toString();
                                          if (password.equalsIgnoreCase(reTypePassword)) {
                                              //   saveInSharedPreferences(userName.getText().toString(),password);
                                              if (!TextUtils.isEmpty(mobileNum.getText().toString())) {
                                                  String phoneNum = mobileNum.getText().toString();
                                               //   savePhoneNumberInPreferences("PhoneNumber", phoneNum);
                                                //  saveNameInPreferences("name", name.getText().toString());
                                                  saveInSharedPreferences(userName.getText().toString(), password);
                                                  //makePostWebserviceCall(user);
                                                  String userID = "user"+System.currentTimeMillis();
                                                  MyApplication.getInstance().setUserIDForEmail(userID);
                                                 makePostWebserviceCall(name.getText().toString(),userName.getText().toString(),password,userID,phoneNum,reg_loc.getText().toString());
                                              } else {
                                                  Toast.makeText(getActivity(), "Mobile Number Can't be Empty", Toast.LENGTH_LONG).show();
                                              }
                                          } else {
                                              Toast.makeText(getActivity(), "Password not matched", Toast.LENGTH_LONG).show();
                                          }
                                      } else {
                                          Toast.makeText(getActivity(), "Re Type Password cannot be empty", Toast.LENGTH_LONG).show();
                                      }
                                  } else {
                                      Toast.makeText(getActivity(), "Password must have atleast a lowercase,uppercase,digit and special symbol @#$% ", Toast.LENGTH_LONG).show();
                                  }

                              }
                          } else {
                              Toast.makeText(getActivity(), "Password cannot be empty", Toast.LENGTH_LONG).show();
                          }
                      } else {
                          Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_LONG).show();
                      }
                  } else {
                      Toast.makeText(getActivity(), "Email cannot be empty", Toast.LENGTH_LONG).show();
                  }
              }else{
                  Toast.makeText(getActivity(), "Name cannot be empty", Toast.LENGTH_LONG).show();
              }



             /* if(!TextUtils.isEmpty(retypePassword.getText().toString())){

              }else{
                  Toast.makeText(getActivity(),"Re Type Password cannot be empty",Toast.LENGTH_LONG).show();
              }*/
              break;
      }
    }

    private boolean validateEmail(String email) {
        boolean isEmailValid  = emailValidator.validate(email);
        return isEmailValid;
    }


    private boolean validatePassword(String password){
        boolean isValidPwd = passwordValidator.validate(password);
        return isValidPwd;
    }

    private void saveInSharedPreferences(String email,String password){

        JSONObject emailPwdJson = new JSONObject();
        try {
            emailPwdJson.put("emailId", email);
            emailPwdJson.put("password", password);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
        prefs.saveStringPreferences(getActivity(), "email", emailPwdJson.toString());
        Toast.makeText(getActivity(), "Data Saved", Toast.LENGTH_LONG).show();
        prefs.saveStringPreferences(getActivity(), "loginType", "emailProfile");
       /* if(!TextUtils.isEmpty(prefs.getStringPreferences(getActivity(),"emailProfile"))){

            Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
            startActivity(i);
            getActivity().finish();
        }else{
            getFragmentManager().beginTransaction()
                    .replace(mContainerId,  ProfileFragment.newInstance("email", ""))
                    .commit();
        }
*/

    /*    Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
        startActivity(i);
        getActivity().finish();
*/
     //   callSlidingMenu();

    //    getFragmentManager().beginTransaction().replace(mContainerId, new BikePoolerMapFragment()).commit();
    }

    private void makePostWebserviceCall(String name,String email,String password,String username,String phonnumber,String location){
      /*
      {
    "username": "zzzzzzz",
    "name": "Test User 2",
    "password": "test223",
    "phone": "9848012411",
    "email": "tesyyt1@gmail.com",
    "location": "hyyyderabad"
}
       */
        Log.d(TAG,"In makePostWebserviceCall()");
        JSONObject emailPwdJson = new JSONObject();
        try {
            //emailPwdJson.put("username", username);
            emailPwdJson.put("location", location);
            emailPwdJson.put("phone", phonnumber);
            emailPwdJson.put("email", email);
            emailPwdJson.put("password", password);
            emailPwdJson.put("name", name);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
        new SiiKPostResponseHelper(getActivity(),RegistrationFragment.this,emailPwdJson).execute(BikeConstants.REGISTRATION_POST_SERVICE_URL);
    }
    private void callSlidingMenu(){
        Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
        startActivity(i);
        getActivity().finish();
    }
    private void savePhoneNumberInPreferences(String key,String phoneVal){
        prefs.saveStringPreferences(getActivity(), key, phoneVal);
    }

    private void saveNameInPreferences(String key,String name){
        prefs.saveStringPreferences(getActivity(), key, name);
    }

    @Override
    public void receiveResult(String result) {
         Log.d(TAG, "Received Result" + result);
        if(result.equalsIgnoreCase("Success")){
            if(!TextUtils.isEmpty(MyApplication.getInstance().getRegistrationResponseMessage())){
                Toast.makeText(getActivity(),MyApplication.getInstance().getRegistrationResponseMessage()+"Login With Credentials",Toast.LENGTH_LONG).show();

            }
            launchLogin();
         //   callSlidingMenu();
        }else {
            if(!TextUtils.isEmpty(MyApplication.getInstance().getRegistrationResponseMessage())){
                Toast.makeText(getActivity(),MyApplication.getInstance().getRegistrationResponseMessage(),Toast.LENGTH_LONG).show();
            }
        }

    }


    public void launchLogin(){
        Intent i = new Intent(getActivity(), MainActivity.class); // Your list's Intent
        //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
        getActivity().overridePendingTransition(0, 0);
        getActivity().finish();

        getActivity().overridePendingTransition(0, 0);
        startActivity(i);
       // break;
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

}
