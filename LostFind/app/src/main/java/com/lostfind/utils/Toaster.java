package com.lostfind.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Created by CHANDRASAIMOHAN on 1/9/2016.
 */
public class Toaster {
    private static Toaster instance;
    Context ctx;

    private Toaster(){

    }

    public static Toaster getInstance(){
        if(instance == null){
            instance = new Toaster();
        }
        return instance;
    }

    public void displayToast(String toastText,Context ctx){
        this.ctx = ctx;
        Toast.makeText(ctx,toastText,Toast.LENGTH_LONG).show();
    }

    public  void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
