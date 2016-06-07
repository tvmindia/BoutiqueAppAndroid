package com.tech.thrithvam.boutiqueapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context adapterContext;
    private static LayoutInflater inflater=null;
    private ArrayList<String[]> objects;
    private String calledFrom;
//    DatabaseHandler db;
    public CustomAdapter(Context context, ArrayList<String[]> objects, String calledFrom) {
       // super(context, textViewResourceId, objects);
        adapterContext=context;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.objects=objects;
        this.calledFrom=calledFrom;
//        db=new DatabaseHandler(context);
    }
    public class Holder
    {
        //Grid items-----------------------------------------------
        ImageView imageView;
        TextView title;
        ImageView offer;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        switch (calledFrom) {
            //--------------------------for home screen items------------------
            case "categoryGrid":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.grid_item, null);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.gridImg);
                    holder.title = (TextView) convertView.findViewById(R.id.gridTxt);
                    holder.offer=(ImageView)convertView.findViewById(R.id.offer);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.title.setText(objects.get(position)[1]);
                //Image Loading-------------------
                Picasso.with(adapterContext)
                        .load(adapterContext.getResources().getString(R.string.url) + objects.get(position)[2].substring((objects.get(position)[2]).indexOf("Media")))
                        .into(holder.imageView)
                ;
                //Offer Label-----------------
                if(!objects.get(position)[3].equals("null")){
                    if(Integer.parseInt(objects.get(position)[3])>0){
                        holder.offer.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.offer.setVisibility(View.GONE);
                    }
                }else {
                    holder.offer.setVisibility(View.GONE);
                }
                //Navigation------------------
                final int FinalPosition = position;
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isOnline()) {
                            Intent intent=new Intent(adapterContext,ItemDetails.class);
                            intent.putExtra("ProductID",objects.get(FinalPosition)[0]);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            adapterContext.startActivity(intent);
                            ((Activity)adapterContext).overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                        }
                        else {
                            Toast.makeText(adapterContext, R.string.network_off_alert, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            default:
                break;
        }

        return convertView;
    }
    public boolean isOnline() {
            ConnectivityManager cm =(ConnectivityManager) adapterContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
