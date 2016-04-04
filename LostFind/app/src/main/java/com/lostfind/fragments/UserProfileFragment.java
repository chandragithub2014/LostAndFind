package com.lostfind.fragments;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lostfind.R;
import com.lostfind.SharedPreferencesUtils;
import com.lostfind.WebserviceHelpers.SiiKGetResponseHelper;
import com.lostfind.WebserviceHelpers.SiiKPUTResponseHelper;
import com.lostfind.WebserviceHelpers.SiiKPostResponseHelper;
import com.lostfind.application.MyApplication;
import com.lostfind.interfaces.SiikReceiveListener;
import com.lostfind.slidingmenu.SlidingMenuActivity;
import com.lostfind.utils.BikeConstants;

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
            if(profileJSONObject.getString("imageurl")!=null){
             profileImage = profileJSONObject.getString("imageurl");
            }
            firstName.setText(profileName);
            lastName.setText(profileName);
            emailId.setText(profileEmail);
            phoneNumber.setText(phoneNum);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void makeWebserviceCall(){
        new SiiKGetResponseHelper(getActivity(), UserProfileFragment.this).execute(
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

        makeWebserviceCall();
        return userProfileView;
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

    }

    private void enableView(){
        //     firstName,lastName,emailId,phoneNumber,userId
        firstName.setEnabled(true);
        lastName.setEnabled(true);
        emailId.setEnabled(true);
        phoneNumber.setEnabled(true);
        userId.setEnabled(true);


        firstName.setClickable(true);
        lastName.setClickable(true);
        emailId.setClickable(true);
        phoneNumber.setClickable(true);
        userId.setClickable(true);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "requestCode::" + requestCode);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == Activity.RESULT_OK && data != null
                && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity()
                        .getContentResolver(), uri);
                ImageView imageView = (ImageView) userProfileView
                        .findViewById(R.id.user_image);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == PICK_CAMERA_REQUEST
                && resultCode == Activity.RESULT_OK && data != null
                && data.getData() != null) {
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            ImageView imageView = (ImageView) userProfileView
                    .findViewById(R.id.user_image);
            imageView.setImageBitmap(bp);

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
                launchSelector();
                break;
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
            profileJson.put("password", profilePassword);
            profileJson.put("email", userProfileEmail);
            profileJson.put("phone", userPhoneNum);
          //  profileJson.put("userid", userProfileUserId);
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
}
