package com.lostfind.fragments;

import java.io.IOException;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.lostfind.R;
import com.lostfind.SharedPreferencesUtils;
import com.lostfind.slidingmenu.SlidingMenuActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileFragment extends Fragment implements  OnClickListener{
	private int PICK_IMAGE_REQUEST = 1;
	private View userProfileView;
	SharedPreferencesUtils sharedPreferencesUtils;
    EditText firstName,lastName,emailId,phoneNumber,userId;
    Button submit;
    private boolean isViewEnabled = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreferencesUtils = new SharedPreferencesUtils();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		userProfileView = inflater.inflate(R.layout.ly_user_profile, container,
				false);
		 firstName = (EditText) userProfileView
				.findViewById(R.id.firstName);
		 lastName = (EditText) userProfileView
				.findViewById(R.id.lastName);
		 emailId = (EditText) userProfileView


				.findViewById(R.id.emailId);
		 phoneNumber = (EditText) userProfileView
				.findViewById(R.id.phoneNumber);
		 userId = (EditText) userProfileView.findViewById(R.id.userId);

		Button changeImage = (Button) userProfileView
				.findViewById(R.id.change_image);
		 submit = (Button) userProfileView.findViewById(R.id.save_btn);
        submit.setOnClickListener(this);
        disableView();
        prepopulateDataIfExists();
		ImageButton	footerImage_btn = (ImageButton)userProfileView.findViewById(R.id.footer_img_btn);
		footerImage_btn.setOnClickListener(this);

		changeImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        PICK_IMAGE_REQUEST);
            }
        });

        String userProfilePhoneNum = "";
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


		/* Hard coded Data */
	//	firstName.setText("Praveen");
	//	lastName.setText("");
	//	emailId.setText("praveen.kumar@gmail.com");
	//	phoneNumber.setText("+11003333");
	//	userId.setText("");


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
		}
	}

	private void laucnchSlidingMenu(){
		Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("position",3);
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
		}
	}


    private void performButtonAction(){
        if(!isViewEnabled){
            enableView();
            isViewEnabled = true;
        }else{
            createAndSaveProfileObject();
            disableView();
        }
    }

    private void createAndSaveProfileObject(){
        String userProfileFirstName =  firstName.getText().toString();
        String userProfileLastName = lastName.getText().toString();
        String userProfileEmail = emailId.getText().toString();
        String userPhoneNum = phoneNumber.getText().toString();
        String userProfileUserId = userId.getText().toString();

        String loginType = sharedPreferencesUtils.getStringPreferences(getActivity(), "loginType");

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


        //facebookprofile
        //googleprofile

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
