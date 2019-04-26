package com.digits.business.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.digits.business.R;
import com.digits.business.activities.BusinessDetailsActivity;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.entities.Business;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder>{


    private Context context;
    private ArrayList<Business> businesses;

    private static int lastPosition = -1;

    public BusinessAdapter(Context context, ArrayList<Business> businesses) {
        this.businesses = businesses;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_business_layout,
                                                                  parent,false);

        return  new BusinessAdapter.ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(businesses.get(position).getBusinessName());
        /*holder.type.setText(businesses.get(position).getBusinessType());
        holder.year.setText(businesses.get(position).getYearEstablished());
        holder.country.setText(businesses.get(position).getCountry());
        holder.city.setText(businesses.get(position).getCity());*/

        getBusinessPhoto(holder.image, businesses.get(position).getImageURL());

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);
    }


    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)  {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return businesses.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        /*private TextView type;
        private TextView year;
        private TextView country;
        private TextView city;*/
        private CircleImageView image;

         ViewHolder(final View itemView)  {

            super(itemView);

            name = itemView.findViewById(R.id.name);
            //type = itemView.findViewById(R.id.businessType);
            //year = itemView.findViewById(R.id.year);
            //country = itemView.findViewById(R.id.country);
            //city = itemView.findViewById(R.id.city);
            image = itemView.findViewById(R.id.photo);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    Intent detailsIntent = new Intent(context, BusinessDetailsActivity.class);

                    detailsIntent.putExtra("BUSINESS", businesses.get(position));

                    context.startActivity(detailsIntent);

                }
            });


        }


    }



    private void getBusinessPhoto(CircleImageView image, String url) {

        ImageLoader imageLoader = ImageLoader.getInstance();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .build();

        ImageLoader.getInstance().init(config);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();


        imageLoader.displayImage(url, image, options);

        //image.setBackgroundResource(R.drawable.user512);

    }

}
