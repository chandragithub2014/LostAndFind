package com.lostfind.Adapter;

/**
 * Created by CHANDRASAIMOHAN on 2/27/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.image.SmartImage;
import com.loopj.android.image.SmartImageView;
import com.lostfind.DTO.DataObject;
import com.lostfind.DTO.SearchDTO;
import com.lostfind.R;
import com.lostfind.interfaces.MyClickListener;

import java.util.ArrayList;
import java.util.List;
//http://loopj.com/android-smart-image-view/

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private  static String LOG_TAG = "MyRecyclerViewAdapter";
    private List<SearchDTO> mDataset;
    private static MyClickListener myClickListener;
    Context ctx;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView description,lostStatus;
        SmartImageView searchIcon;

        public DataObjectHolder(View itemView) {
            super(itemView);

            description = (TextView) itemView.findViewById(R.id.desc);
            searchIcon = (SmartImageView)itemView.findViewById(R.id.lost_item);
            lostStatus  = (TextView) itemView.findViewById(R.id.status);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);

           /* searchIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (Integer) v.getTag();
                    Log.d(LOG_TAG,"Clicked on info Button"+tag);
                    myClickListener.onSpecificViewOnItemClick(tag,v);
                }
            });*/
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(),v);


        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(List<SearchDTO> myDataset, MyClickListener myClickListener) {
        mDataset = myDataset;
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(Context ctx,List<SearchDTO> myDataset, MyClickListener myClickListener) {
        mDataset = myDataset;
        this.myClickListener = myClickListener;
        this.ctx = ctx;
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
        holder.lostStatus.setText(mDataset.get(position).getStatus());
        Log.d("Adapter","ImageURL:::"+mDataset.get(position).getImageURL());
       // if(mDataset.get(position).getImageURL()!=null ) {
            if (!TextUtils.isEmpty(mDataset.get(position).getImageURL())) {
                // holder.searchIcon.setImageUrl(mDataset.get(position).getImageURL());
                if(!mDataset.get(position).getImageURL().equalsIgnoreCase("null")) {
                    holder.searchIcon.setImageUrl(mDataset.get(position).getImageURL());
                }
             else {
                //      holder.searchIcon.setImageResource(R.drawable.bikepooler_img);
                Log.d("Adapter", "Elseeeeeeeee");
                int resID = ctx.getResources().getIdentifier("bikepooler_img", "drawable", ctx.getPackageName());
                holder.searchIcon.setImageResource(resID);
           /* holder.searchIcon.setImageResource(getActivity().getResources()
                    .getIdentifier(imageURL, OMSConstants.DRAWABLE,
                            getActivity().getPackageName()));*/
            }
            }
     //   }

       holder.searchIcon.setTag(mDataset.get(position).getItemId());


    }

    public void addItem(SearchDTO dataObj, int index) {
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