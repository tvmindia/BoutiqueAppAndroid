package com.tech.thrithvam.boutiqueapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
        //Order items-----------------------------------------------
        TextView orderDescription,orderNo,amount,orderDate,expectedDeliveryDate,lastUpdatedDate,orderReady;
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
        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        Calendar cal= Calendar.getInstance();
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
            case "homeGrid":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.homescreen_items, null);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.gridImg);
                    holder.title = (TextView) convertView.findViewById(R.id.gridTxt);
                    holder.offer=(ImageView)convertView.findViewById(R.id.offer);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //More Image----------------------------------
                if(objects.get(position)[0].equals("")){        //More
                    Picasso.with(adapterContext)
                            .load(R.drawable.more)
                            .into(holder.imageView)
                    ;
                    holder.title.setVisibility(View.GONE);
                    holder.offer.setVisibility(View.GONE);
                    final int FinalPos = position;
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isOnline()) {
                                Intent categoryIntent=new Intent(adapterContext,GridOfProducts.class);
                                categoryIntent.putExtra("CategoryCode",objects.get(FinalPos)[1]);
                                categoryIntent.putExtra("Category",objects.get(FinalPos)[2].replace("\uD83D\uDC49\t",""));
                                categoryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                adapterContext.startActivity(categoryIntent);
                                ((Activity)adapterContext).overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                            }
                            else {
                                Toast.makeText(adapterContext, R.string.network_off_alert, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    break;
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
                final int FinalPos = position;
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isOnline()) {
                            Intent intent=new Intent(adapterContext,ItemDetails.class);
                            intent.putExtra("ProductID",objects.get(FinalPos)[0]);
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
            //--------------------------for order status items------------------
            case "orders":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.order_item, null);
                    holder.orderDescription = (TextView) convertView.findViewById(R.id.description);
                    holder.orderNo = (TextView) convertView.findViewById(R.id.orderNo);
                    holder.amount = (TextView) convertView.findViewById(R.id.amount);
                    holder.orderDate = (TextView) convertView.findViewById(R.id.orderDate);
                    holder.expectedDeliveryDate = (TextView) convertView.findViewById(R.id.expectedDeliveryDate);
                    holder.lastUpdatedDate = (TextView) convertView.findViewById(R.id.lastUpdatedDate);
                    holder.orderReady=(TextView) convertView.findViewById(R.id.orderReady);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.orderDescription.setText(objects.get(position)[0]);
                holder.orderNo.setText(objects.get(position)[1]);

                if(!objects.get(position)[2].equals("null"))
                    holder.amount.setText(adapterContext.getResources().getString(R.string.rs, objects.get(position)[2]));
                else
                    holder.amount.setVisibility(View.GONE);

                if(!objects.get(position)[3].equals("null")) {
                    cal.setTimeInMillis(Long.parseLong(objects.get(position)[3]));
                    holder.orderDate.setText(formatted.format(cal.getTime()));
                }
                else
                    holder.orderDate.setText("-");

                if(!objects.get(position)[4].equals("null")){
                    cal.setTimeInMillis(Long.parseLong(objects.get(position)[4]));
                    holder.expectedDeliveryDate.setText(formatted.format(cal.getTime()));
                }
                else
                    holder.expectedDeliveryDate.setText("-");

                //order ready----------
                if(!objects.get(position)[6].equals("null")){
                    Calendar now= Calendar.getInstance();
                    cal.setTimeInMillis(Long.parseLong(objects.get(position)[6]));
                    if(cal.before(now)){
                        holder.orderReady.setVisibility(View.VISIBLE);
                        holder.orderReady.setText(R.string.item_delivered);
                        holder.orderReady.setTextColor(Color.BLUE);
                    }
                    else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            holder.orderReady.setTextColor(adapterContext.getColor(R.color.green));
                        }
                        else {
                            holder.orderReady.setTextColor(adapterContext.getResources().getColor(R.color.green));
                        }
                        holder.orderReady.setVisibility(View.GONE);
                        holder.orderReady.setText(R.string.your_order_is_ready);
                        if (!objects.get(position)[5].equals("null")) {
                            cal.setTimeInMillis(Long.parseLong(objects.get(position)[5]));
                            if (cal.before(now))
                                holder.orderReady.setVisibility(View.VISIBLE);
                            else
                                holder.orderReady.setVisibility(View.GONE);
                        } else
                            holder.orderReady.setVisibility(View.GONE);
                    }
                }
                else {
                    holder.orderReady.setText(R.string.your_order_is_ready);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.orderReady.setTextColor(adapterContext.getColor(R.color.green));
                    }
                    else {
                        holder.orderReady.setTextColor(adapterContext.getResources().getColor(R.color.green));
                    }
                    if (!objects.get(position)[5].equals("null")) {
                        Calendar now = Calendar.getInstance();
                        cal.setTimeInMillis(Long.parseLong(objects.get(position)[5]));
                        if (cal.before(now))
                            holder.orderReady.setVisibility(View.VISIBLE);
                        else
                            holder.orderReady.setVisibility(View.GONE);
                    } else
                        holder.orderReady.setVisibility(View.GONE);
                }
                //last updated------------
                if(!objects.get(position)[8].equals("null")){                                    //updated
                    cal.setTimeInMillis(Long.parseLong(objects.get(position)[8]));
                    holder.lastUpdatedDate.setText(formatted.format(cal.getTime()));
                }
                else if(!objects.get(position)[7].equals("null")) {                         //Only created
                    cal.setTimeInMillis(Long.parseLong(objects.get(position)[7]));
                    holder.lastUpdatedDate.setText(formatted.format(cal.getTime()));
                }
                else
                    holder.lastUpdatedDate.setText("-");

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
