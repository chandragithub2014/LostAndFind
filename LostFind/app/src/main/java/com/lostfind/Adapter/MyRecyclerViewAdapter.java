package com.lostfind.Adapter;

/**
 * Created by CHANDRASAIMOHAN on 2/27/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lostfind.DTO.DataObject;
import com.lostfind.R;
import com.lostfind.interfaces.MyClickListener;

import java.util.ArrayList;


public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private  static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView description,lostStatus;
        ImageView searchIcon;

        public DataObjectHolder(View itemView) {
            super(itemView);

            description = (TextView) itemView.findViewById(R.id.desc);
            searchIcon = (ImageView)itemView.findViewById(R.id.lost_item);
            lostStatus  = (TextView) itemView.findViewById(R.id.status);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
            searchIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (Integer) v.getTag();
                    Log.d(LOG_TAG,"Clicked on info Button"+tag);
                    myClickListener.onSpecificViewOnItemClick(tag,v);
                }
            });
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(),v);


        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<DataObject> myDataset, MyClickListener myClickListener) {
        mDataset = myDataset;
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_row_layout, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.description.setText(mDataset.get(position).getItemDescription());
        holder.lostStatus.setText(mDataset.get(position).getItemStatus());
        holder.searchIcon.setTag(position);


    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}