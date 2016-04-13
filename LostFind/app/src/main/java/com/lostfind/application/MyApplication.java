package com.lostfind.application;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by CHANDRASAIMOHAN on 12/6/2015.
 */
public class MyApplication extends Application {
    private static MyApplication singleton;
    JSONObject publishJSON;
    AppCompatActivity activity;
    GoogleApiClient mGoogleAPIClient;
    private SQLiteDatabase configDB;
    private  String registrationResponseMessage;
    private String userIDForEmail;
    private String searchResponse;
    private String imageURL;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
  /*  private static final String TWITTER_KEY = "LEZjsy3ZQwyscBjvLdaz7bNgv";
    private static final String TWITTER_SECRET = "CTsRGP431ghMRSeeDadFxn5usbh4exlmD7qQMINch258Cb3JX9";*/

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
      /*  TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));*/
        printHashKey();
    }
    public static MyApplication getInstance() {
        return singleton;
    }
    public void printHashKey(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.lostfind", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", "KEYHASH::::"+Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
               e.printStackTrace();
        }
    }

    public JSONObject getPublishJSON() {
        return publishJSON;
    }

    public void setPublishJSON(JSONObject publishJSON) {
        this.publishJSON = publishJSON;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public GoogleApiClient getmGoogleAPIClient() {
        return mGoogleAPIClient;
    }

    public void setmGoogleAPIClient(GoogleApiClient mGoogleAPIClient) {
        this.mGoogleAPIClient = mGoogleAPIClient;
    }

    public SQLiteDatabase getConfigDB() {
        return configDB;
    }

    public void setConfigDB(SQLiteDatabase configDB) {
        this.configDB = configDB;
    }

    public String getRegistrationResponseMessage() {
        return registrationResponseMessage;
    }

    public void setRegistrationResponseMessage(String registrationResponseMessage) {
        this.registrationResponseMessage = registrationResponseMessage;
    }

    public String getUserIDForEmail() {
        return userIDForEmail;
    }

    public void setUserIDForEmail(String userIDForEmail) {
        this.userIDForEmail = userIDForEmail;
    }

    public String getSearchResponse() {
        return searchResponse;
    }

    public void setSearchResponse(String searchResponse) {
        this.searchResponse = searchResponse;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}

/*
KEYHASH::::sD7LbIpHjg/SNEKY1xXvUzYXML8=
 */
