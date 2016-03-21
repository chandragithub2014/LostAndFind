package com.lostfind.slidingmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.lostfind.MainActivity;
import com.lostfind.R;
import com.lostfind.SharedPreferencesUtils;
import com.lostfind.fragments.BlankFragment;
import com.lostfind.fragments.GoogleSignOut;
import com.lostfind.fragments.HomeFragment;
import com.lostfind.fragments.PasswordResetFragment;
import com.lostfind.fragments.ReportHistory;
import com.lostfind.fragments.ReportLossFragment;
import com.lostfind.fragments.SearchFragment;
import com.lostfind.fragments.UserProfileFragment;


public class SlidingMenuActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{
    boolean isActivity = true ;
    SharedPreferencesUtils sharedPreferencesUtils;


    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    int defaultPostion = 0;
    String currentLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_menu);

        if(getIntent().getExtras()!=null) {
            Bundle bundle = getIntent().getExtras();
            defaultPostion = bundle.getInt("position");
            Toast.makeText(getApplicationContext(),"DefaultPosition::::"+defaultPostion,Toast.LENGTH_LONG).show();
        }

/*
"position", 1);
                bundle.putString("from"
 */
        //get the values out by key
     /*   defaultPostion =bundle.getInt("position");
         currentLocation=bundle.getString("from");
*/        FacebookSdk.sdkInitialize(getApplicationContext());
        sharedPreferencesUtils = new SharedPreferencesUtils();
        initializeSlidingMenu();
        displayView(defaultPostion);
        /*getFragmentManager().beginTransaction()
                .replace(R.id.mapparentLayout, new BikePoolerMapFragment())
                .commit();*/
    }


    public void initializeSlidingMenu(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

       setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LayoutInflater mInflater= LayoutInflater.from(getApplicationContext());
        View mCustomView = mInflater.inflate(R.layout.toolbar_custom_view, null);
        mToolbar.addView(mCustomView);

     /*   ImageView logout_img = (ImageView)mToolbar.findViewById(R.id.map_icon);
        logout_img.setVisibility(View.VISIBLE);
        logout_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*Intent i = new Intent(SlidingMenuActivity.this, MainActivity.class); // Your list's Intent
                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                startActivity(i);
                finish();*//*

                Intent i = new Intent(SlidingMenuActivity.this, MainActivity.class); // Your list's Intent
                //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
            }
        });*/

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        switch (position) {
           /* case  0 :
                Log.d("SlidingMenuActivity", "display View " + position);
              *//*  getFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout, new BikePoolerMapFragment())
                        .commit();*//*
            *//*    Intent mapIntent = new Intent(this,MainActivity.class);
                startActivity(mapIntent);
                finish();*//*

                Fragment searchFrag = new SearchFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout,searchFrag)
                        .commit();
                break;
            case 1:
                Bundle args = new Bundle();
                Fragment reportLossFrag = new ReportLossFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout,reportLossFrag)
                        .commit();
                break;
            case 2:
                Bundle argss = new Bundle();
                Fragment ff = new SearchFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout,ff)
                        .commit();
                break;*/
            case 1:
                Fragment userSettingsFrag = new UserProfileFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout,userSettingsFrag)
                        .commit();
                break;
            case 4:
                Fragment searchFrag = new SearchFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout,searchFrag)
                        .commit();
                break;
            case 5:
                Fragment reporthistory = new ReportHistory();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout,reporthistory)
                        .commit();
                break;
            case 0:
                Fragment homeFrag = new HomeFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout,homeFrag)
                        .commit();

                break;
            case 2:


            case 3:


              /*  Fragment fff = new SearchFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout,fff)
                        .commit();*/
                Bundle args = new Bundle();
                Fragment reportLossFrag = new ReportLossFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout,reportLossFrag)
                        .commit();
                break;

            case 6:
            case 7:

                Fragment blankFrag = new BlankFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout,blankFrag)
                        .commit();
                break;

            case 8:
              /*  SharedPreferencesUtils  prefs = new SharedPreferencesUtils();
                prefs.saveBooleanPreferences(SlidingMenuActivity.this, BikeConstants.BIKE_BOOLEAN_PREFS_DATA, false);*/

               /* LoginManager.getInstance().logOut();
                 Intent i = new Intent(SlidingMenuActivity.this, MainActivity.class); // Your list's Intent
                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                startActivity(i);
                finish();*/

                String loginType  = sharedPreferencesUtils.getStringPreferences(this,"loginType");
                if(loginType.equalsIgnoreCase("facebookprofile")) {
                    disconnectFromFacebook();
                }
                if(loginType.equalsIgnoreCase("googleprofile")) {
                    GoogleSignOut.getInstance().init();
                }

                Intent i = new Intent(SlidingMenuActivity.this, MainActivity.class); // Your list's Intent
                //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                this.overridePendingTransition(0, 0);
               this.finish();

                this.overridePendingTransition(0, 0);
                startActivity(i);
                break;


        }
    }

    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

/* @Override
    public void onBackPressed() {

        isActivity = sharedPreferencesUtils.getBooleanPreferences(SlidingMenuActivity.this,BikeConstants.BIKE_BOOLEAN_PREFS_DATA);
        Log.d("TAG", "" + isActivity+" Stack Count:::"+getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() == 0 && !isActivity) {
            sharedPreferencesUtils.saveBooleanPreferences(SlidingMenuActivity.this, BikeConstants.BIKE_BOOLEAN_PREFS_DATA,true);
            Log.d("TAG", "onBackPressed");
            finish();
            Intent i = new Intent(SlidingMenuActivity.this, SlidingMenuActivity.class); // Your list's Intent
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
            startActivity(i);

        *//*    Intent intent = new Intent(BikeMapFragmentActivity.this, BikeMapFragmentActivity.class);
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*//*
            // call this to finish the current activity
        } else if(!isActivity && getFragmentManager().getBackStackEntryCount() != 0){
            Log.d("SlidingMEnuActivit","Super.onBackPressed");
            super.onBackPressed();
        }
        else if(isActivity) {
           //finish();
            super.onBackPressed();
        }
    }*/
}
