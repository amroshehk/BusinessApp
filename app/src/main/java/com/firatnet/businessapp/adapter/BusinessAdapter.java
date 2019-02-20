package com.firatnet.businessapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.firatnet.businessapp.R;
import com.firatnet.businessapp.activities.BusinessDetailsActivity;
import com.firatnet.businessapp.activities.PlayMp3FilesActivity;
import com.firatnet.businessapp.classes.PreferenceHelper;
import com.firatnet.businessapp.entities.Business;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_ADDRESS;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_CITY;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_COUNTRY;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_EMPLOYESS_NUMBER;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_KEYWORDS;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_PARTNERSHIP_TYPE;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_PIN_CODE;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_PRODUCTS;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_TURNOVER;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_TYPE;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_YEAR_ESTABLISHED;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_URL;


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
        holder.type.setText(businesses.get(position).getBusinessType());
        holder.year.setText(businesses.get(position).getYearEstablished());
        holder.country.setText(businesses.get(position).getCountry());
        holder.city.setText(businesses.get(position).getCity());


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
        private TextView type;
        private TextView year;
        private TextView country;
        private TextView city;

         ViewHolder(final View itemView)  {

            super(itemView);

            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.businessType);
            year = itemView.findViewById(R.id.year);
            country = itemView.findViewById(R.id.country);
            city = itemView.findViewById(R.id.city);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    Intent detailsIntent = new Intent(context, BusinessDetailsActivity.class);

                    detailsIntent.putExtra("BUSINESS", businesses.get(position));
                /*    detailsIntent.putExtra(BUSINESS_NAME, businesses.get(position).getBusinessName());
                    detailsIntent.putExtra(BUSINESS_TYPE, businesses.get(position).getBusinessType());
                    detailsIntent.putExtra(BUSINESS_PARTNERSHIP_TYPE,
                                            businesses.get(position).getPartnershipType());
                    detailsIntent.putExtra(BUSINESS_YEAR_ESTABLISHED,
                                             businesses.get(position).getYearEstablished());
                    detailsIntent.putExtra(BUSINESS_EMPLOYESS_NUMBER,
                                            businesses.get(position).getEmployeesNumber());
                    detailsIntent.putExtra(BUSINESS_ADDRESS, businesses.get(position).getAddress());
                    detailsIntent.putExtra(BUSINESS_COUNTRY, businesses.get(position).getCountry());
                    detailsIntent.putExtra(BUSINESS_CITY, businesses.get(position).getCity());
                    detailsIntent.putExtra(BUSINESS_PIN_CODE, businesses.get(position).getPinCode());
                    detailsIntent.putExtra(BUSINESS_KEYWORDS, businesses.get(position).getKeywords());
                    detailsIntent.putExtra(BUSINESS_PRODUCTS, businesses.get(position).getProducts());
                    detailsIntent.putExtra(BUSINESS_TURNOVER, businesses.get(position).getTurnover());*/

                    context.startActivity(detailsIntent);

                }
            });


        }


    }

}
