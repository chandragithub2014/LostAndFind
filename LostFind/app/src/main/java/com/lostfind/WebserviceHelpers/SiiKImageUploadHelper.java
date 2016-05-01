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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
/**
 * Created by CHANDRASAIMOHAN on 4/7/2016.
 */
public class SiiKImageUploadHelper  extends AsyncTask<String, Void, String> {

    private final String TAG = this.getClass().getSimpleName();
    private Context ctx = null;
    //private ContentValues contentvalues = null;
    private ProgressDialog progressDialog = null;
    private SiikReceiveListener receiveListener = null;
    private JSONObject jsonPayLoad;
    private boolean isFromReportLostFound = false;
    private Bitmap bitmap;
    String encodedImage = "";
    String path;
    String key;

    public SiiKImageUploadHelper(Context ctx, SiikReceiveListener receiveListener, Bitmap bitmap) {
        this.ctx = ctx;
        this.receiveListener = receiveListener;
        this.bitmap = bitmap;
        showProgressDialog();

    }

    public SiiKImageUploadHelper(Context ctx, SiikReceiveListener receiveListener, String path,String key) {
        this.ctx = ctx;
        this.receiveListener = receiveListener;
        this.path = path;
        this.key = key;
        showProgressDialog();

    }
    private void showProgressDialog(){
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage("Registering...");
        if(!progressDialog.isShowing())
            progressDialog.show();
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
        String response = null;
        String serviceUrl = params[0];
       /*    encodedImage = encodedBitMap(bitmap);
      if(encodedImage!=null) {
            Map<String, String> uriParams = new HashMap<String, String>();
            uriParams.put("file", encodedImage);

            Map<String, String> httpHeaders = new HashMap<String, String>();
            httpHeaders.put("Accept",
                    "application/json");
            httpHeaders.put("Content-type",
                    "application/json");
            SharedPreferencesUtils sharedPreferencesUtils  = new SharedPreferencesUtils();
            String headerToken =            sharedPreferencesUtils.getStringPreferences(ctx,"token");
            Log.d(TAG,"HederToken:::"+headerToken);
            if(!TextUtils.isEmpty(headerToken)){
                String basicAuth = "Bearer " +headerToken;
                httpHeaders.put("Authorization", basicAuth);
            }
            response = postData(serviceUrl);
        }
        else{
            response = "";
        }*/
        if(path!=null){
         response  =    postImageDataAsFile(serviceUrl);
        }else{
            response = "";
        }
        return response;

    }
 private String postImageDataAsFile(String serviceURL){
        String serverresponse = "";

     File file = new File(path);
     try {
         HttpClient client = new DefaultHttpClient();
       //  String postURL = "http://someposturl.com";
         HttpPost post = new HttpPost(serviceURL);
         SharedPreferencesUtils sharedPreferencesUtils  = new SharedPreferencesUtils();
         String headerToken =            sharedPreferencesUtils.getStringPreferences(ctx,"token");
         Log.d(TAG, "HederToken:::" + headerToken);
         if(!TextUtils.isEmpty(headerToken)){
             String basicAuth = "Bearer " +headerToken;
             post.setHeader("Authorization", basicAuth);
         }

         FileBody bin = new FileBody(file);
         MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
         reqEntity.addPart(key, bin);
         post.setEntity(reqEntity);
         HttpResponse response = client.execute(post);
      //  Log.d(TAG,"Status Line:::"+response.getStatusLine().getStatusCode());
         if(response.getStatusLine().getStatusCode()==200) {
             HttpEntity resEntity = response.getEntity();
             if (resEntity != null) {
                 //      Log.i("RESPONSE", EntityUtils.toString(resEntity));
                 serverresponse = EntityUtils.toString(resEntity);
                 Log.d(TAG, "ServerResponse:::" + serverresponse);
                 JSONObject imageJSON = new JSONObject(serverresponse);
                 if(imageJSON.getString("url")!=null){
                     MyApplication.getInstance().setImageURL(imageJSON.getString("url"));
                 }
                 serverresponse = "uploadSuccess";

             }
         }else{
             Log.d(TAG,"Status Line:::"+response.getStatusLine().getStatusCode());
             MyApplication.getInstance().setImageURL("");
             serverresponse = "uploadfail";
         }
     }
     catch (SocketTimeoutException se) {
         Log.e(TAG,
                 "SocketTimeoutException occurred while Posting Image to Server"
                         + se.getMessage());
         se.printStackTrace();

         return "uploadfail";

     }catch (IOException e) {
         Log.e(TAG,
                 "IOException occurred while Posting Image to Server"
                         + e.getMessage());
         e.printStackTrace();
         return "uploadfail";

     }
     catch (Exception e) {
         e.printStackTrace();
     }

     return  serverresponse;
    }
    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG,"Result::::"+result);
        super.onPostExecute(result);
        if (receiveListener != null) {
            receiveListener.receiveResult(result);
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private String postData(String url) {
        String response = null;
        HttpClient httpclient = null;
        HttpResponse httpResponse = null;

        // Variable
        int connectionID = new Random().nextInt(Integer.MAX_VALUE); // AppMonitor
        // Variable

        try {
            // if (Constants.USE_PROXY) {
            // httpclient = getHttpClient();
            // } else {
            // int timeoutSocket = 6000;
            // HttpConnectionParams
            // .setSoTimeout(httpParameters, timeoutSocket);
            httpclient = new DefaultHttpClient();
            // }

            Log.i(TAG, "Image Post Service URL : " + url);

            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            try {
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                byte[] imgdata = null;
                try {
                    imgdata = Base64.decode(encodedImage, 1);
                } catch (Exception e) {
                    Log.e(TAG, "Exception occurred while decoding the Image :"
                            + e.getMessage());
                    e.printStackTrace();
                }

                Calendar cal = Calendar.getInstance();

                entity.addPart("avatar", new ByteArrayBody(
                        imgdata, Long.toString(cal.getTimeInMillis())
                        + ".png"));
                httpPost.setEntity(entity);




                httpResponse = httpclient.execute(httpPost, localContext);
            }

            catch (SocketTimeoutException se) {

                Log.e(TAG, "Timed out" + se.getMessage());
                return "fileUploadFailed";
            } catch (IOException e) {

                Log.e(TAG,
                        "Exception occurred while Posting the Image to Server."
                                + e.getMessage());
                e.printStackTrace();
            }

            StatusLine statusLine = httpResponse.getStatusLine();

            Log.d(TAG, "Response Code :" + statusLine.getStatusCode());

            if (statusLine.getStatusCode() == 200) {

                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {

                    InputStream is = httpEntity.getContent();
                    response = convertStreamtoString(is);

                    JSONObject responseJSON = new JSONObject(response);
                    response = responseJSON.getString("filePath");

                    if (response != null) {
                        return response;
                    }
                } else {
                    Log.e(TAG, "HttpEntity is NULL :" + httpEntity);
                }
            } else {
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    InputStream is = httpEntity.getContent();
                    response = convertStreamtoString(is);

                    if (response != null) {
                        JSONObject responseJSON = new JSONObject(response);
                        response = responseJSON
                                .getString("AuthenticationStatus");
                        return response;
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("error",
                                statusLine.getStatusCode());
                        jsonObject.put("error_description",
                                statusLine.toString());
                        response = jsonObject.toString();
                    }
                }
            }
        } catch (SocketTimeoutException se) {
            Log.e(TAG,
                    "SocketTimeoutException occurred while Posting Image to Server"
                            + se.getMessage());
            se.printStackTrace();

            return "fileUploadFailed";

        } catch (IOException e) {
            Log.e(TAG,
                    "IOException occurred while Posting Image to Server"
                            + e.getMessage());
            e.printStackTrace();

        } catch (JSONException e) {
            Log.e(TAG, "JSONException occurred while Posting Image to Server. Error is:"
                    + e.getMessage());
            e.printStackTrace();
        }

        Log.i(TAG, "Image Post Service Response :" + response);

        return response;
    }

    private String convertStreamtoString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }

}
