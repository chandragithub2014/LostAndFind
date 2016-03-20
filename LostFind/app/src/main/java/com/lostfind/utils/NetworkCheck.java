package com.lostfind.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by CHANDRASAIMOHAN on 3/21/2016.
 */
public class NetworkCheck {
    private  ConnectivityManager connectionManager;


    // Verify Network Availability.
    public  boolean checkNetworkConnectivity(Context ctx) {
        connectionManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectionManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
