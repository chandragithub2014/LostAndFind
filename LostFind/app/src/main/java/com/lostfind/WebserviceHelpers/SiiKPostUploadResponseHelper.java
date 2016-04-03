package com.lostfind.WebserviceHelpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
public class SiiKPostUploadResponseHelper extends AsyncTask<String, Void, String> {

    private final String TAG = this.getClass().getSimpleName();
    private Context ctx = null;
    //private ContentValues contentvalues = null;
    private ProgressDialog progressDialog = null;
    private SiikReceiveListener receiveListener = null;
    private JSONObject jsonPayLoad;
    private boolean isFromReportLostFound = false;
    private Bitmap bitmap;


    public SiiKPostUploadResponseHelper(Context ctx, SiikReceiveListener receiveListener, Bitmap bitmap) {
        this.ctx = ctx;
        this.receiveListener = receiveListener;
        this.bitmap = bitmap;
        showProgressDialog();

    }

    private String encodedBitMap(Bitmap bitmap) {
        String base64EncodedBitMap = "";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
        byte[] byte_arr = stream.toByteArray();
        String image_str = Base64.encodeToString(byte_arr,
                Base64.NO_WRAP);


        return base64EncodedBitMap;
    }

    @Override
    protected String doInBackground(String... params) {
        String encodedImage = encodedBitMap(bitmap);

        try {
            jsonPayLoad = new JSONObject();
            jsonPayLoad.put("avatar", encodedImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String serviceUrl = params[0];
        String serverResponse = "";
        boolean isNetworkAvailable = new NetworkCheck().checkNetworkConnectivity(ctx);
        if (!isNetworkAvailable) {
            serverResponse = "Network Fail";
        } else {
       //     serverResponse = fetchPostResponse(jsonPayLoad, serviceUrl);
            serverResponse = fetchImageUploadResponse(serviceUrl, bitmap);

        }
        return serverResponse;
    }

    private String fetchImageUploadResponse(String serviceURL, Bitmap bitmap) {
        String postResponse = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.
        byte[] byte_arr = stream.toByteArray();
        InputStream inputStream = null;
        OutputStream os = null;
        HttpURLConnection conn = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        int serverResponseCode = 0;
        DataOutputStream dos = null;

        try {
            System.setProperty("http.keepAlive","false");
            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(byte_arr);
            URL url = new URL(serviceURL);
            conn = (HttpURLConnection) url.openConnection();
          //  String jsonMessage = jsonPayLoad.toString();
         //   Log.d(TAG, "Json MEssage:::" + jsonMessage);

            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(50000 /* milliseconds */);
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs

            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Encoding", "");
            //conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", "avatar");
            SharedPreferencesUtils sharedPreferencesUtils  = new SharedPreferencesUtils();
            String headerToken =            sharedPreferencesUtils.getStringPreferences(ctx,"token");
            if(!TextUtils.isEmpty(headerToken)){
                String basicAuth = "Bearer " +headerToken;
                conn.setRequestProperty("Authorization", basicAuth);
            }
            //open
            conn.connect();
            dos = new DataOutputStream(conn.getOutputStream());


            //forth parameter - filename
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                    + "avatar" + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            //
            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();

            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();
            conn.disconnect();


            int statusCode = conn.getResponseCode();
            Log.d(TAG, "status code:::" + statusCode);


        } catch (SocketTimeoutException se) {
            progressDialog.dismiss();
            Log.e(TAG,
                    "SocketTimeoutException occurred while Posting the Actions"
                            + se.getMessage());
            if (receiveListener != null) {
                receiveListener.receiveResult("Post Failed");
            }
            se.printStackTrace();
        } catch (ProtocolException pe) {
            progressDialog.dismiss();
            postResponse = "Post Failed";
            Log.e(TAG, "ProtocolException");
        } catch (MalformedURLException mle) {
            progressDialog.dismiss();
            postResponse = "Post Failed";
            Log.e(TAG, "MalformedURLException");
        } catch (IOException e) {
            progressDialog.dismiss();
            Log.e(TAG,
                    "IOException occurred while Posting the Actions"
                            + e.getMessage());
            e.printStackTrace();


            return postResponse;
        } finally {
            //clean up
            try {
                os.close();
            } catch (NullPointerException ex) {
                postResponse = "Post Failed";
                Log.e(TAG, "NullPointerException");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (NullPointerException ex) {
                postResponse = "Post Failed";
                Log.e(TAG, "NullPointerException");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            conn.disconnect();
        }
        return postResponse;
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
        progressDialog.setMessage("Registering...");
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
            //make some HTTP header nicety
            conn.setRequestProperty("Content-Type", "multipart/form-data");
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
