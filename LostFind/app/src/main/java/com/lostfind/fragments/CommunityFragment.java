package com.lostfind.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lostfind.Adapter.MyRecyclerViewAdapter;
import com.lostfind.DTO.DataObject;
import com.lostfind.DTO.SearchDTO;
import com.lostfind.R;
import com.lostfind.interfaces.MyClickListener;
import com.lostfind.interfaces.SiikReceiveListener;
import com.lostfind.utils.BikeConstants;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment implements MyClickListener,View.OnClickListener,SiikReceiveListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String TAG = "CommunityFragment";
    private int PICK_IMAGE_REQUEST = 1;
    private View searchView;
    AutoCompleteTextView location_spinner;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDixji8saFmpOFmSnKXY6-uP_2mnDYG3Js";
    RecyclerView searchResults;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Button category;
    String[] categoryNames;
    CheckBox lost_check,found_check;

    public CommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityFragment newInstance(String param1, String param2) {
        CommunityFragment fragment = new CommunityFragment();
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

            super.onCreateView(inflater, container, savedInstanceState);
            searchView = inflater.inflate(R.layout.fragment_community, container,
                    false);

            Toolbar mToolBar = (Toolbar)getActivity().findViewById(R.id.toolbar);
            TextView toolBarTitle = (TextView)mToolBar.findViewById(R.id.title);
            toolBarTitle.setText("Your Community");
            categoryNames = getResources().getStringArray(R.array.array_name);
        LinearLayout check_search_Layout = (LinearLayout)searchView.findViewById(R.id.community_edit_layout);

        AutoCompleteTextView communityEditor = (AutoCompleteTextView)check_search_Layout.findViewById(R.id.community);
        String[] communityArray = getActivity().getResources().getStringArray(R.array.communities);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,communityArray);

        communityEditor.setAdapter(adapter);
        communityEditor.setThreshold(1);
            ImageView submit = (ImageView) check_search_Layout.findViewById(R.id.search_btn);
            initRecyclerView();
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchResultsList();
                }
            });
        /*category = (Button) searchView
                .findViewById(R.id.category);
        category.setOnClickListener(this);*/
	/*	final Spinner location = (Spinner) searchView
				.findViewById(R.id.location);*/
      /*  final EditText comments= (EditText) searchView
                .findViewById(R.id.search_comments);*/
     /*   location_spinner = (AutoCompleteTextView)searchView.findViewById(R.id.find_loss_location);*/



            //
       /* final Calendar c = Calendar
                .getInstance();
        final Button dateBtn = (Button) searchView.findViewById(R.id.dateButton);
        final Button toDateBtn = (Button) searchView.findViewById(R.id.todateButton);

        toDateBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy",Locale.getDefault());
                Date date = null;
                date = new Date();
                c.setTime(date);
                year =  c.get(Calendar.YEAR);
                month =  c.get(Calendar.MONTH);
                day =   c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(getActivity(), toDatePickerListener,
                        year, month,day).show();
            }
        });

        dateBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy",Locale.getDefault());
                Date date = null;
                date = new Date();
                c.setTime(date);
                year =  c.get(Calendar.YEAR);
                month =  c.get(Calendar.MONTH);
                day =   c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(getActivity(), datePickerListener,
                        year, month,day).show();
            }
        });*/




            return searchView;
        }


    private void initRecyclerView(){
        // ScrollView resultsView = (ScrollView)searchView.findViewById(R.id.results_table);
        LinearLayout searchResultsllayout = (LinearLayout)searchView.findViewById(R.id.results_table);
        searchResults = (RecyclerView)searchResultsllayout.findViewById(R.id.search_results_list);
    }

    private void launchResultsList(){
        searchResults.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        searchResults.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet(),CommunityFragment.this);
        searchResults.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        searchResults.addItemDecoration(itemDecoration);
    }

    private ArrayList<SearchDTO> getDataSet() {
        String[] stringArray = getActivity().getResources().getStringArray(R.array.array_name);
        ArrayList results = new ArrayList<SearchDTO>();

        for(int i=0;i<stringArray.length;i++) {
            //   DataObject obj = new DataObject("",stringArray[i],"found");
            //       results.add(i, obj);

            SearchDTO obj = new 	SearchDTO("", "desc","found", ""+(i+1), stringArray[i]);
            results.add(i, obj);
        }
        return results;
    }
    @Override
    public void onSpecificViewOnItemClick(int position, View v) {
        Fragment  infoFragment = null;
        Log.d(TAG, "Clicked Position:::" + position);
      /*    if(position==0){
            infoFragment =          InfoFragment.newInstance(position,"");
        }else if(position==1){
            infoFragment =          InfoFragment.newInstance(position,"");
        }else if(position==2){
            infoFragment =          InfoFragment.newInstance(position,"");
        }
      getActivity().getSupportFragmentManager().beginTransaction()
                .replace(mContainerId, infoFragment).addToBackStack(null)
                .commit();*/
    }

    @Override
    public void onItemClick(int position, View v,String tag) {
        Log.d(TAG,"Clicked Position:::"+position);
    }

    @Override
    public void receiveResult(String result) {
        if(result!=null){
            if(result.equalsIgnoreCase(BikeConstants.WEBSERVICE_NETWORK_FAIL)){
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }else{

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(category.getWindowToken(), 0);
    }


}
