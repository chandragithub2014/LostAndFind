package com.lostfind.googleintegration;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.Plus;
import com.lostfind.R;
import com.lostfind.interfaces.SocialIntgration;

import java.lang.reflect.AccessibleObject;

/**
 * Created by CHANDRASAIMOHAN on 2/29/2016.
 */
public class GoogleSignIn implements GoogleApiClient.OnConnectionFailedListener ,GoogleApiClient.ConnectionCallbacks{
 AppCompatActivity activity;
    GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "GoogleSignIn";
    private ProgressDialog mProgressDialog;
    SocialIntgration socialIntgration;
    Context ctx;

      public  GoogleSignIn(AppCompatActivity activity,SocialIntgration socialIntgration){
          this.activity = activity;
          this.socialIntgration = socialIntgration;

          configureSignIn();
          buildClient();
          socialIntgration.googleInitDone(true);
      }

    public  GoogleSignIn(AppCompatActivity activity){
        this.activity = activity;
     //   this.socialIntgration = socialIntgration;
        buidNewGoogleApiClient();
       /* configureSignIn();
        buildClient();*/
        socialIntgration.googleInitDone(true);
    }

    private void buidNewGoogleApiClient(){

        mGoogleApiClient =  new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API,Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    public  void onStart() {

        mGoogleApiClient.connect();
    }

    protected void onStop() {

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void gPlusSignOut() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
          //  changeUI(false);
        }
    }
  private void configureSignIn(){
// [START configure_signin]
      // Configure sign-in to request the user's ID, email address, and basic
      // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
       gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestEmail()
              .build();
      // [END configure_signin]

  }

    private void buildClient(){
        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
              Log.d(TAG,"ConnectionFailed.....");
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    /* private void start(){
      OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
      if (opr.isDone()) {
          // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
          // and the GoogleSignInResult will be available instantly.
          Log.d(TAG, "Got cached sign-in");
          GoogleSignInResult result = opr.get();
          handleSignInResult(result);
      } else {
          // If the user has not previously signed in on this device or the sign-in has expired,
          // this asynchronous branch will attempt to sign in the user silently.  Cross-device
          // single sign-on will occur in this branch.
          showProgressDialog();
          opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
              @Override
              public void onResult(GoogleSignInResult googleSignInResult) {
                  hideProgressDialog();
                  handleSignInResult(googleSignInResult);
              }
          });
      }
  }*/

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG,"Display Name:::"+acct.getDisplayName());
           // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
           // updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
           // updateUI(false);
        }
    }
    // [END handleSignInResult]

}


/*
api key:AIzaSyAQ0GcW42dLkWfSFI1XF7aLha-Oqbk7OCU

OAuth client
Here is your client ID
99193507046-emt20imbijm3d3q2a8pntpcnf0ijtuoo.apps.googleusercontent.com
 */