package com.lostfind.fragments;

import java.io.IOException;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.lostfind.R;
import com.lostfind.slidingmenu.SlidingMenuActivity;

public class UserProfileFragment extends Fragment implements  OnClickListener{
	private int PICK_IMAGE_REQUEST = 1;
	private View userProfileView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		userProfileView = inflater.inflate(R.layout.ly_user_profile, container,
				false);
		EditText firstName = (EditText) userProfileView
				.findViewById(R.id.firstName);
		EditText lastName = (EditText) userProfileView
				.findViewById(R.id.lastName);
		EditText emailId = (EditText) userProfileView


				.findViewById(R.id.emailId);
		EditText phoneNumber = (EditText) userProfileView
				.findViewById(R.id.phoneNumber);
		EditText userId = (EditText) userProfileView.findViewById(R.id.userId);

		Button changeImage = (Button) userProfileView
				.findViewById(R.id.change_image);
		Button submit = (Button) userProfileView.findViewById(R.id.save_btn);

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

		/* Hard coded Data */
		firstName.setText("Praveen");
		lastName.setText("Kumar");
		emailId.setText("praveen.kumar@gmail.com");
		phoneNumber.setText("+11003333");
		userId.setText("praveenn");

		return userProfileView;
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
		}
	}
}
