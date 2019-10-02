package com.digits.business.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digits.business.R;
import com.digits.business.activities.BusinessDetailsActivity;
import com.digits.business.activities.CallerNumberDetailsActivity;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.database.DbHelper;
import com.digits.business.entities.Log;
import com.digits.business.twilio.VoiceActivity;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.digits.business.classes.JsonTAG.TAG_PHONE;


public class RecyclerLogCardAdapter extends RecyclerView.Adapter<RecyclerLogCardAdapter.ViewHolder> {

    private ArrayList<Log> logs;
    private Log mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private Context context;
    ProgressDialog progressDialog;
    private static int lastPosition=-1;
    private Activity mActivity;


    public RecyclerLogCardAdapter(ArrayList<Log> logs, Context context,Activity mActivity) {
        this.logs = logs;
        this.context=context;
        this.mActivity=mActivity;

    }

    @NonNull
    @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_log_layout,parent,false);
            ViewHolder viewHolder=new ViewHolder(v);

            return viewHolder;
        }

        public void deleteItem(int position) {
            mRecentlyDeletedItem = logs.get(position);
            mRecentlyDeletedItemPosition = position;
            logs.remove(position);
            notifyItemRemoved(position);
            showUndoSnackbar();
        }
    public void callnumberItem(int position) {
        notifyDataSetChanged();
        Intent intent = new Intent(context, VoiceActivity.class);
        intent.putExtra("GeneratedID", logs.get(position).getPhone_no());
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        }

        private void showUndoSnackbar() {
            View view = mActivity.findViewById(R.id.main_layout);
            Snackbar snackbar = Snackbar.make(view, "successfully deleted",
                    Snackbar.LENGTH_LONG);
            final int[] isclicked = {0};
            snackbar.setAction(R.string.snack_bar_undo, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isclicked[0] =1;
                    undoDelete();
                }
            });
            snackbar.show();
            snackbar.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    //see Snackbar.Callback docs for event details
                    if(isclicked[0] ==0)
                    {
                        DbHelper dbHelper;
                        dbHelper=new DbHelper(context);
                        dbHelper.deleteLogEntry(mRecentlyDeletedItem);
                    }
                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });
        }

        private void undoDelete() {
            logs.add(mRecentlyDeletedItemPosition,
                    mRecentlyDeletedItem);
            notifyItemInserted(mRecentlyDeletedItemPosition);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.name.setText(logs.get(position).getName());

            Date date=new Date(logs.get(position).getTimestamp());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm");
            String strDate= formatter.format(date);
            holder.date.setText(strDate);

            holder.number.setText(logs.get(position).getPhone_no());

            Long duration=logs.get(position).getDuration();
            int min =(int)(duration/60);
            int sec =(int)(duration-((int)(duration/60))*60);

            String min_str="";
            String sec_str="";
            if(min<10)
                min_str=String.valueOf("0"+min);
            else
                min_str=String.valueOf(min);

            if(sec<10)
                sec_str=String.valueOf("0"+sec);
            else
                sec_str=String.valueOf(sec);

            holder.duration.setText(min_str+":"+sec_str);

           switch (logs.get(position).getType())
           {
               case 0:
                   holder.calltype.setImageResource(R.drawable.ic_missed);
                   break;
               case 1:
                   holder.calltype.setImageResource(R.drawable.ic_incoming);
                   break;
               case 2:
                   holder.calltype.setImageResource(R.drawable.ic_outgoing);
                   break;
           }

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
            return logs.size();
        }


        class ViewHolder extends  RecyclerView.ViewHolder{
            public TextView name;
            public TextView number;
            public TextView date;
            public TextView duration;
            public ImageView calltype;

            public  ViewHolder(final View itemView)
            {
                super(itemView);


                name=itemView.findViewById(R.id.name);
                number=itemView.findViewById(R.id.number);
                duration=itemView.findViewById(R.id.duration);
                date=itemView.findViewById(R.id.date);
                calltype=itemView.findViewById(R.id.calltype);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position=getAdapterPosition();


                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int position=getAdapterPosition();
                        Intent intent = new Intent(context, CallerNumberDetailsActivity.class);
                        intent.putExtra(TAG_PHONE, logs.get(position).getPhone_no());
                        context.startActivity(intent);

                    }
                });


            }




        }



}
