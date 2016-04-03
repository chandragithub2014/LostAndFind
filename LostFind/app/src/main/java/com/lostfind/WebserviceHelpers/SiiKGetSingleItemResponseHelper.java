package com.lostfind.WebserviceHelpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.lostfind.SharedPreferencesUtils;
import com.lostfind.application.MyApplication;
import com.lostfind.interfaces.SiikReceiveListener;
import com.lostfind.utils.BikeConstants;
import com.lostfind.utils.NetworkCheck;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;

/**
 * Created by CHANDRASAIMOHAN on 3/21/2016.
 */
public class SiiKGetSingleItemResponseHelper extends AsyncTask<String, Void, String> {


    private final String TAG = this.getClass().getSimpleName();
    private Context ctx = null;
    //private ContentValues contentvalues = null;
    private ProgressDialog progressDialog = null;
    private SiikReceiveListener receiveListener = null;
   private JSONObject jsonPayLoadList;
SharedPreferencesUtils sharedPreferencesUtils;
    public SiiKGetSingleItemResponseHelper(Context ctx, SiikReceiveListener receiveListener/*,JSONObject jsonPayLoadList*/){
        this.ctx = ctx;
        this.receiveListener = receiveListener;
      //  this.jsonPayLoadList = jsonPayLoadList;
        sharedPreferencesUtils = new SharedPreferencesUtils();
        showProgressDialog();

    }
    @Override
    protected String doInBackground(String... params) {

        String serviceUrl = params[0];
        //serviceUrl+":"+id
        String serverResponse = "";
        boolean isNetworkAvailable = new NetworkCheck().checkNetworkConnectivity(ctx);
        if(!isNetworkAvailable){
            serverResponse = "Network Fail";
        }else{
            serverResponse = fetchURLConnectionServerResponse(serviceUrl);
        }
        return serverResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if (receiveListener != null) {
            receiveListener.receiveResult(result);
        }if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage("Receiving");
        if(!progressDialog.isShowing())
            progressDialog.show();
    }


    private String fetchURLConnectionServerResponse(String serviceUrl){
        String response = "";
        InputStream inputStream = null;

        HttpURLConnection urlConnection = getHttpURLConnection(serviceUrl);
        try {
            urlConnection.connect();
        }  catch (SocketException e) {
            Log.e(TAG,
                    "SocketException occurred while excecuting the  Service."
                            + e.getMessage());
            e.printStackTrace();
            response = "Network Fail";
        } catch (IOException e) {
            Log.e(TAG,
                    "IOException occurred while while excecuting the  Service."
                            + e.getMessage());
            e.printStackTrace();
            response = "Network Fail";

        } catch (Exception e) {
            Log.e(TAG,
                    "Exception occurred while while excecuting the  Service."
                            + e.getMessage());
            e.printStackTrace();
            response = "Network Fail";
        }

        if(urlConnection!=null){
            try{
                int statusCode = urlConnection.getResponseCode();
                if (statusCode ==  BikeConstants.STATUSCODE_OK) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    response = convertStreamToString(inputStream);
                    Log.d(TAG, "GET Response for HTTPURLConnection:::"+response);
                    MyApplication.getInstance().setSearchResponse(response);
                    response = "success";

                }else
                    inputStream  = new BufferedInputStream(urlConnection.getErrorStream());
                String serviceResponse = convertStreamToString(inputStream);
                Log.d(TAG,"Service Response"+serviceResponse);
                if(!TextUtils.isEmpty(serviceResponse)) {
                    try{
                        JSONObject responseJSON = new JSONObject(serviceResponse);
                        String responseMessage = responseJSON.getString("message");
                        MyApplication.getInstance().setRegistrationResponseMessage(responseMessage);
                        response = "Get Failed";

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
            catch (SocketException e) {
                Log.e(TAG,
                        "SocketException occurred while excecuting the  Service."
                                + e.getMessage());
                e.printStackTrace();
                response = "Network Fail";
                MyApplication.getInstance().setRegistrationResponseMessage("Network Fail");
            } catch (IOException e) {
                Log.e(TAG,
                        "IOException occurred while while excecuting the  Service."
                                + e.getMessage());
                e.printStackTrace();
                response = "Network Fail";
                MyApplication.getInstance().setRegistrationResponseMessage("Network Fail");

            }  catch (Exception e) {
                Log.e(TAG,
                        "Exception occurred while while excecuting the  Service."
                                + e.getMessage());
                e.printStackTrace();
                response = "Network Fail";
                MyApplication.getInstance().setRegistrationResponseMessage("Network Fail");
            }
        }else{
            response = "Network Fail";
            MyApplication.getInstance().setRegistrationResponseMessage("Network Fail");
        }
        return  response;
    }


    private HttpURLConnection getHttpURLConnection(String url){
        //Added code for HttpURLConnection
        // AppMonitor

        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) obj.openConnection();
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        // optional default is GET
        try {
            int timeoutSocket = 5000;
            conn.setReadTimeout(timeoutSocket);
            conn.setConnectTimeout(timeoutSocket);
			         /* optional request header */
 String headerToken =            sharedPreferencesUtils.getStringPreferences(ctx,"token");
            if(!TextUtils.isEmpty(headerToken)){
                String basicAuth = "Bearer " +headerToken;
                conn.setRequestProperty("Authorization", basicAuth);
            }


            conn.setRequestProperty("Content-Type", "application/json");

		                /* optional request header */
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
        } catch (ProtocolException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        conn.setDoInput(true);

        return conn;
        //End HttpURL Connection
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
