package com.firatnet.businessapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.firatnet.businessapp.R;
import com.firatnet.businessapp.entities.Mp3File;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


public class RecyclerMP3FileCardAdapter extends RecyclerView.Adapter<RecyclerMP3FileCardAdapter.ViewHolder> {

    public  ArrayList<Mp3File> mp3Files;
    Context context;
    private static int lastPosition=-1;


    public RecyclerMP3FileCardAdapter(ArrayList<Mp3File> mp3Files, Context context) {
        this.mp3Files = mp3Files;
        this.context=context;

    }

    @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_mp3_file_layout,parent,false);
            ViewHolder viewHolder=new ViewHolder(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.name.setText(mp3Files.get(position).getName());
            holder.date.setText("Created at "+mp3Files.get(position).getCreated_at());


            // Here you apply the animation when the view is bound
            setAnimation(holder.itemView, position);
        }
    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

        @Override
        public int getItemCount() {
            return mp3Files.size();
        }


        class ViewHolder extends  RecyclerView.ViewHolder{
            public TextView title;
            public TextView name;
            public TextView date;
            public  ViewHolder(final View itemView)
            {
                super(itemView);


                name=(TextView)itemView.findViewById(R.id.name);
                date=(TextView)itemView.findViewById(R.id.date);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position=getAdapterPosition();


                    }
                });


            }



        }
    }
