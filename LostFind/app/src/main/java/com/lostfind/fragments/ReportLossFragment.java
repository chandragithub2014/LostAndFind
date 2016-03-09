package com.lostfind.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.lostfind.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportLossFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportLossFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button spinnerType;
View view = null;
    String[] itemNames;
    public ReportLossFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportLossFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportLossFragment newInstance(String param1, String param2) {
        ReportLossFragment fragment = new ReportLossFragment();
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
       itemNames = getResources().getStringArray(R.array.array_name);
        view =  inflater.inflate(R.layout.fragment_report_loss, container, false);
        initInputFieldView(view);
        return view;
    }


    private void initInputFieldView(View v){
        ScrollView inputScrollView = (ScrollView)v.findViewById(R.id.find_loss_input);
         spinnerType = (Button)inputScrollView.findViewById(R.id.find_loss_selector);
        spinnerType.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.find_loss_selector:
                hideKeyboard();
                launchSelector();
                break;
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(spinnerType.getWindowToken(), 0);
    }
    private void launchSelector(){
       /* InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Make your selection");
        builder.setItems(R.array.array_name, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                spinnerType.setText(itemNames[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
      /*  new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(R.array.array_name, 0, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                       String selectted =    itemNames[selectedPosition];
                        spinnerType.setText(selectted);
                        // Do something useful withe the position of the selected radio button
                    }
                })
                .show();*/
    }
}
