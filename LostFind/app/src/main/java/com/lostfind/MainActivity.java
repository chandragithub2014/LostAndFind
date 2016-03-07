package com.lostfind;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import com.lostfind.application.MyApplication;
import com.lostfind.fragments.SocialFragment;
import com.lostfind.fragments.SplashScreenFragment;

public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Login");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        MyApplication.getInstance().setActivity(MainActivity.this);
        LayoutInflater mInflater= LayoutInflater.from(getApplicationContext());
        View mCustomView = mInflater.inflate(R.layout.toolbar_custom_view, null);
        mToolbar.addView(mCustomView);


        getFragmentManager().beginTransaction()
                .replace(R.id.loginparentLayout,  SplashScreenFragment.newInstance("", ""))
                .commit();
    }
}
