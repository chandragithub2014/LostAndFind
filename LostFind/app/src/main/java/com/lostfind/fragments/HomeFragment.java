package com.lostfind.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lostfind.R;
import com.lostfind.slidingmenu.SlidingMenuActivity;
import com.lostfind.utils.BikeConstants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

View homeView;
    Button lost_btn,find_btn;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeView =  inflater.inflate(R.layout.home_layout, container, false);
        Toolbar mToolBar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        TextView toolBarTitle = (TextView)mToolBar.findViewById(R.id.title);
        toolBarTitle.setText("SiiK");
        lost_btn = (Button)homeView.findViewById(R.id.lost_btn);
        find_btn = (Button)homeView.findViewById(R.id.find_btn);

        lost_btn.setOnClickListener(this);
        find_btn.setOnClickListener(this);
        return  homeView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lost_btn:
                laucnchSlidingMenu(BikeConstants.SLIDING_MENU_REPORT_LOSS);
                break;
            case R.id.find_btn:
                laucnchSlidingMenu(BikeConstants.SLIDING_MENU_REPORT_FIND);
                break;
        }
    }


    private void laucnchSlidingMenu(int position){
        Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        i.putExtras(bundle);
        startActivity(i);
        getActivity().finish();
    }
}
