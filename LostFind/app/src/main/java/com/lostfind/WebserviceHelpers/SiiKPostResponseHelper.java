package com.lostfind.WebserviceHelpers;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.lostfind.SharedPreferencesUtils;
import com.lostfind.application.MyApplication;
import com.lostfind.interfaces.SiikReceiveListener;
import com.lostfind.utils.NetworkCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by CHANDRASAIMOHAN on 3/30/2016.
 */
public class SiiKPostResponseHelper extends AsyncTask<String, Void, String> {

    private final String TAG = this.getClass().getSimpleName();
    private Context ctx = null;
    //private ContentValues contentvalues = null;
    private ProgressDialog progressDialog = null;
    private SiikReceiveListener receiveListener = null;
    private JSONObject jsonPayLoad;
    private boolean isFromReportLostFound = false;
    private boolean isFound  = false;
    private boolean isPostComment = false;
    private String progressText;

    public SiiKPostResponseHelper(Context ctx, SiikReceiveListener receiveListener,JSONObject jsonPayLoad,String progressText){
        this.ctx = ctx;
        this.receiveListener = receiveListener;
        this.jsonPayLoad = jsonPayLoad;
        this.progressText = progressText;
        showProgressDialog();

    }

    public SiiKPostResponseHelper(Context ctx, SiikReceiveListener receiveListener,JSONObject jsonPayLoad,boolean isPostComment,String progressText){
        this.ctx = ctx;
        this.receiveListener = receiveListener;
        this.jsonPayLoad = jsonPayLoad;
        this.isPostComment = isPostComment;
        this.progressText = progressText;
        showProgressDialog();

    }

    public SiiKPostResponseHelper(Context ctx, SiikReceiveListener receiveListener,JSONObject jsonPayLoad,boolean isFromReportLostFound,boolean isFound,String progressText){
        this.ctx = ctx;
        this.receiveListener = receiveListener;
        this.jsonPayLoad = jsonPayLoad;
        this.isFromReportLostFound = isFromReportLostFound;
        this.isFound = isFound;
        this.progressText = progressText;
        showProgressDialog();

    }
    @Override
    protected String doInBackground(String... params) {

        String serviceUrl = params[0];
        String serverResponse = "";
        boolean isNetworkAvailable = new NetworkCheck().checkNetworkConnectivity(ctx);
        if(!isNetworkAvailable){
            serverResponse = "Network Fail";
        }else{
            serverResponse = fetchPostResponse(jsonPayLoad,serviceUrl);
        }
        return serverResponse;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (receiveListener != null) {
            receiveListener.receiveResult(result);
        }if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(progressText);
        if(!progressDialog.isShowing())
            progressDialog.show();
    }





    private String fetchPostResponse(JSONObject jsonPayLoad,String serviceURL){
        String postResponse="";
        InputStream inputStream = null;
        OutputStream os = null;
        HttpURLConnection conn=null;
        try {
            URL url = new URL(serviceURL);
            conn =  (HttpURLConnection) url.openConnection();
            String jsonMessage = jsonPayLoad.toString();
            Log.d(TAG,"Json MEssage:::"+jsonMessage);
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(50000 /* milliseconds */);
            conn.setRequestMethod("POST");
          //  conn.setUseCaches (false);
            conn.setDoInput(true);
          //  conn.setDoOutput(true);
          conn.setFixedLengthStreamingMode(jsonMessage.getBytes().length);

            if(isFromReportLostFound){
                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
                String headerToken =            sharedPreferencesUtils.getStringPreferences(ctx,"token");
                if(!TextUtils.isEmpty(headerToken)){
                    String basicAuth = "Bearer " +headerToken;
                    conn.setRequestProperty("Authorization", basicAuth);
                }
            }

            if(isFound){
                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
                String headerToken =            sharedPreferencesUtils.getStringPreferences(ctx,"token");
                if(!TextUtils.isEmpty(headerToken)){
                    String basicAuth = "Bearer " +headerToken;
                    conn.setRequestProperty("Authorization", basicAuth);
                }
            }

            if(isPostComment){
                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
                String headerToken =            sharedPreferencesUtils.getStringPreferences(ctx,"token");
                if(!TextUtils.isEmpty(headerToken)){
                    String basicAuth = "Bearer " +headerToken;
                    conn.setRequestProperty("Authorization", basicAuth);
                }
            }
            //make some HTTP header nicety
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
         //   conn.setRequestProperty("Content-Encoding", "gzip");



            //open
            conn.connect();
            //setup send
            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(jsonMessage.getBytes());
         //   int statusCode = conn.getResponseCode();
         //   Log.d(TAG,"Status Code:::"+statusCode);
            //clean up
            os.flush();

            int statusCode = conn.getResponseCode();
            Log.d(TAG,"status code:::"+statusCode);
            if(statusCode ==  201 || statusCode == 200){
                inputStream  = new BufferedInputStream(conn.getInputStream());
                String serviceResponse = convertStreamToString(inputStream);
                Log.d(TAG,"Service Response"+serviceResponse);
                if(!TextUtils.isEmpty(serviceResponse)) {
                    try{
                        JSONObject responseJSON = new JSONObject(serviceResponse);
                        String responseMessage = responseJSON.getString("message");
                        MyApplication.getInstance().setRegistrationResponseMessage(responseMessage);
                        postResponse = "Success";
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }else{
                inputStream  = new BufferedInputStream(conn.getErrorStream());
                String serviceResponse = convertStreamToString(inputStream);
                Log.d(TAG,"Service Response"+serviceResponse);
                if(!TextUtils.isEmpty(serviceResponse)) {
                 try{
                     JSONObject responseJSON = new JSONObject(serviceResponse);
                     String responseMessage = responseJSON.getString("message");
                     MyApplication.getInstance().setRegistrationResponseMessage(responseMessage);
                     postResponse = "Post Failed";

                 }catch (Exception e){
                     e.printStackTrace();
                 }

                }
            }





        } catch (SocketTimeoutException se) {
            progressDialog.dismiss();
            Log.e(TAG,
                    "SocketTimeoutException occurred while Posting the Actions"
                            + se.getMessage());
            if (receiveListener != null) {
                receiveListener.receiveResult("Post Failed");
            }
            se.printStackTrace();
        }
        catch(ProtocolException pe){
            progressDialog.dismiss();
            postResponse = "Post Failed";
            Log.e(TAG, "ProtocolException");
        }
        catch(MalformedURLException mle){
            progressDialog.dismiss();
            postResponse = "Post Failed";
            Log.e(TAG, "MalformedURLException");
        }

        catch (IOException e) {
            progressDialog.dismiss();
            Log.e(TAG,
                    "IOException occurred while Posting the Actions"
                            + e.getMessage());
            e.printStackTrace();


            return postResponse;
        }

        finally {
            //clean up
            try {
                os.close();
            }
            catch(NullPointerException ex){
                postResponse = "Post Failed";
                Log.e(TAG, "NullPointerException");
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch(NullPointerException ex){
                postResponse = "Post Failed";
                Log.e(TAG, "NullPointerException");
            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            conn.disconnect();
        }
        return postResponse;
    }


    private String convertStreamToString(InputStream is) {
        BufferedReader reader = null;
        StringBuilder sb = null;
        String line = null;

        try {
            sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e(TAG,
                    "IOException occurred while converting stream to string."
                            + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                is.close();
            } catch (IOException e) {
                Log.e(TAG,
                        "IOException occurred while converting stream to string."
                                + e.getMessage());
                e.printStackTrace();
            }
        }
        String rr =  sb.toString();
        rr = rr.replaceAll("\\n", "");
        rr = rr.replaceAll("\\t", "");
        return rr;
    }
}
