package com.lostfind.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lostfind.MainActivity;
import com.lostfind.R;
import com.lostfind.WebserviceHelpers.SiiKGetResponseHelper;
import com.lostfind.WebserviceHelpers.SiiKPostResponseHelper;
import com.lostfind.application.MyApplication;
import com.lostfind.interfaces.SiikReceiveListener;
import com.lostfind.utils.BikeConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordResetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordResetFragment extends Fragment implements View.OnClickListener,SiikReceiveListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
View view = null;
    TextView email;

    public PasswordResetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasswordResetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordResetFragment newInstance(String param1, String param2) {
        PasswordResetFragment fragment = new PasswordResetFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_password_reset, container, false);
        email = (TextView)view.findViewById(R.id.EditText3);
        Button resetPwd = (Button)view.findViewById(R.id.resetbtn);
        resetPwd.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.resetbtn:

                if(!TextUtils.isEmpty(email.getText().toString())){
                  //  makeWebserviceCall(email.getText().toString());
                    makePostWebServiceCall(email.getText().toString());
                }else{
                    Toast.makeText(getActivity(),"Enter Email To proceed ",Toast.LENGTH_LONG).show();
                }
                /*Toast.makeText(getActivity(),"Password Reset Done",Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity(), MainActivity.class); // Your list's Intent
                //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                getActivity().overridePendingTransition(0, 0);
                getActivity().finish();

                getActivity().overridePendingTransition(0, 0);
                startActivity(i);*/
                break;
        }
    }

    private void makeWebserviceCall(String email){
        String queryString = "email="+email;
        new SiiKGetResponseHelper(getActivity(), PasswordResetFragment.this,"Resetting password....").execute(
                BikeConstants.RESET_PASSWORD+"?"+queryString);
    }

    private void makePostWebServiceCall(String email){
        Log.d("PassWordResetFragment", "In makePostWebserviceCall()");
        /*
        {"mode":"forgot_password", "email": "a@c.com"}
         */
        JSONObject emailPwdJson = new JSONObject();
        try {
            //emailPwdJson.put("username", username);
            emailPwdJson.put("mode", "forgot_password");
            emailPwdJson.put("email", email);
            }
        catch (JSONException e){
            e.printStackTrace();
        }
        new SiiKPostResponseHelper(getActivity(),PasswordResetFragment.this,emailPwdJson,"Posting For Reset...").execute(BikeConstants.RESET_PASSWORD);
    }

    @Override
    public void receiveResult(String result) {
        if(result.equalsIgnoreCase("success")){
             Toast.makeText(getActivity(), MyApplication.getInstance().getRegistrationResponseMessage(),Toast.LENGTH_LONG).show();
            launchLogin();
        }else{
            Toast.makeText(getActivity(), MyApplication.getInstance().getRegistrationResponseMessage(),Toast.LENGTH_LONG).show();
        }
    }


    private void launchLogin(){
        Intent i = new Intent(getActivity(), MainActivity.class); // Your list's Intent
        //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
        getActivity().overridePendingTransition(0, 0);
        getActivity().finish();

        getActivity().overridePendingTransition(0, 0);
        startActivity(i);
    }
}
