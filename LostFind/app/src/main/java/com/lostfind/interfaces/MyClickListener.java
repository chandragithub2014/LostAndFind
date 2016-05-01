package com.lostfind.interfaces;

import android.view.View;

/**
 * Created by CHANDRASAIMOHAN on 2/27/2016.
 */
public interface MyClickListener {
    public void onItemClick(int position, View v,String tag);
    public void onSpecificViewOnItemClick(int position, View v);
}