package com.firatnet.businessapp.adapter;

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
import com.firatnet.businessapp.R;
import com.firatnet.businessapp.activities.PlayMp3FilesActivity;
import com.firatnet.businessapp.classes.PreferenceHelper;
import com.firatnet.businessapp.entities.Mp3File;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

import static com.firatnet.businessapp.classes.JsonTAG.TAG_ID;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PHOTO_URL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_URL;
import static com.firatnet.businessapp.classes.URLTAG.SET_DEFAULT_FILE_URL;


public class RecyclerMP3FileCardAdapter extends RecyclerView.Adapter<RecyclerMP3FileCardAdapter.ViewHolder> {

    public  ArrayList<Mp3File> mp3Files;
    Context context;
    ProgressDialog progressDialog;
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

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position=getAdapterPosition();


                        showchangedialog(  position);
                        return false;
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

            void showchangedialog(final int position) {

                final Dialog dialog2;
                Button ensure;
                Button cancel;

                dialog2 = new Dialog(context);
                dialog2.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.dialog_set_default_layout);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                ensure = dialog2.findViewById(R.id.ensure);
                cancel = dialog2.findViewById(R.id.cancel);

                // if button is clicked, close the custom dialog
                ensure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferenceHelper helper=new PreferenceHelper(context);
                        SetDefaultFileServer(helper,mp3Files.get(position).getUrl());

                        dialog2.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//
                        dialog2.dismiss();
                    }
                });
                dialog2.show();

            }



        }


    private void SetDefaultFileServer(final PreferenceHelper helper,final  String url) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Set file as defualt.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        StringRequest request = new StringRequest(Request.Method.POST, SET_DEFAULT_FILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("success")) {


                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        helper.setSettingValueDefaultFile(url);
                        progressDialog.dismiss();
                        notifyItemRangeChanged(0, mp3Files.size());
                    } else  {

                        progressDialog.dismiss();
                          Toast.makeText(context, "error ", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(context, "error JSONException", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    String responseBody = new String(volleyError.networkResponse.data, "utf-8");
                    JSONObject jsonObject = new JSONObject(responseBody);
                    progressDialog.dismiss();
                    Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    //Handle a malformed json response
                } catch (UnsupportedEncodingException error) {

                }
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put(TAG_ID, helper.getSettingValueId());
                params.put(TAG_PHOTO_URL, url);


                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.getCache().clear();
        requestQueue.add(request);

    }

}
