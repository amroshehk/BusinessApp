package com.digits.business.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digits.business.R;
import com.digits.business.activities.PlayMp3FilesActivity;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.entities.Mp3File;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

import static com.digits.business.classes.JsonTAG.TAG_FILE_URL;
import static com.digits.business.classes.JsonTAG.TAG_ID;
import static com.digits.business.classes.JsonTAG.TAG_NAME;
import static com.digits.business.classes.JsonTAG.TAG_URL;
import static com.digits.business.classes.URLTAG.SET_DEFAULT_FILE_URL;


public class RecyclerVoiceEmailCardAdapter extends RecyclerView.Adapter<RecyclerVoiceEmailCardAdapter.ViewHolder> {

    public  ArrayList<Mp3File> mp3Files;
    Context context;
    ProgressDialog progressDialog;
    private static int lastPosition=-1;


    public RecyclerVoiceEmailCardAdapter(ArrayList<Mp3File> mp3Files, Context context) {
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
            PreferenceHelper helper=new PreferenceHelper(context);

            if( helper.getSettingValueDefaultFile().equals(mp3Files.get(position).getUrl()))
                holder.default_file.setVisibility(View.VISIBLE);
            else
                holder.default_file.setVisibility(View.GONE);


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
            public TextView name;
            public TextView date;
            public TextView default_file;

            public  ViewHolder(final View itemView)
            {
                super(itemView);


                name=(TextView)itemView.findViewById(R.id.name);
                date=(TextView)itemView.findViewById(R.id.date);
                default_file=(TextView)itemView.findViewById(R.id.default_file);

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
                        Intent intent = new Intent(context, PlayMp3FilesActivity.class);
                        intent.putExtra(TAG_URL, mp3Files.get(position).getUrl());
                        intent.putExtra(TAG_NAME, mp3Files.get(position).getName());
                        context.startActivity(intent);
                    }
                });


            }




        }



}
