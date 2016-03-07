package com.lostfind.fragments;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.lostfind.application.MyApplication;

/**
 * Created by CHANDRASAIMOHAN on 3/3/2016.
 */

    public class GoogleSignOut implements GoogleApiClient.OnConnectionFailedListener ,GoogleApiClient.ConnectionCallbacks{

    private static GoogleSignOut instance;

    private GoogleApiClient mGoogleApiClient;


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private GoogleSignOut(){

    }

    public static GoogleSignOut getInstance(){
        if(instance == null){
            instance = new GoogleSignOut();
        }
        return instance;
    }

    public void signOut(){

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(MyApplication.getInstance().getActivity())
                .enableAutoManage(MyApplication.getInstance().getActivity() *//* FragmentActivity *//*, this *//* OnConnectionFailedListener *//*)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

        Auth.GoogleSignInApi.signOut(MyApplication.getInstance().getmGoogleAPIClient()).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(MyApplication.getInstance().getActivity(),"Signout:::",Toast.LENGTH_LONG).show();
                  //     status.getStatusMessage()
                        // [START_EXCLUDE]
                  //      updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
}
