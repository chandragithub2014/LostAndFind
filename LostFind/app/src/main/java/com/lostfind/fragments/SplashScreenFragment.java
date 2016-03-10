package com.lostfind.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lostfind.DBManager.SiikDBManager;
import com.lostfind.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashScreenFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view = null;
    int mContainerId = -1;
    private Handler handler;
    private Runnable callback;

    public SplashScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SplashScreenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SplashScreenFragment newInstance(String param1, String param2) {
        SplashScreenFragment fragment = new SplashScreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_splash_screen, container, false);
        mContainerId = container.getId();
        Toolbar mtoolBar = (Toolbar)((AppCompatActivity) getActivity()).findViewById(R.id.toolbar);
        mtoolBar.setVisibility(View.GONE);
        callSiikDB();
        callback = new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction()
                        .replace(mContainerId,  SocialFragment.newInstance("", ""))
                        .commit();
            }
        };
        handler.postDelayed(callback, 2000);
      /*  Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){

                    e.printStackTrace();
                    getFragmentManager().beginTransaction()
                            .replace(mContainerId,  SocialFragment.newInstance("", ""))
                            .commit();
                }finally{

                    getFragmentManager().beginTransaction()
                            .replace(mContainerId,  SocialFragment.newInstance("", ""))
                            .commit();
                }
            }
        };
        timerThread.start();*/
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(callback);

    }

    private void callSiikDB(){
        new SiikDBManager(getActivity()).load();
    }
}
