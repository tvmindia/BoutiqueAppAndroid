package com.tech.thrithvam.boutiqueapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemDetails extends AppCompatActivity {
    Constants constants=new Constants();
ImageView favorite;
    ImageView share;
    Boolean isFav=false;
    Integer favCount;
    TextView favCountString;
    SliderLayout itemImages;
    LayoutInflater inflater;
    TextView description;
    TextView viewDesigner;
    TextView price;
    TextView stock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        getSupportActionBar().setElevation(0);
        if (isOnline()){
            new ProductDetails().execute();
        }
        else {
            Toast.makeText(ItemDetails.this,R.string.network_off_alert,Toast.LENGTH_LONG).show();
            finish();
        }
        inflater = (LayoutInflater)ItemDetails.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Typeface fontType1 = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        description=(TextView)findViewById(R.id.description);
        description.setTypeface(fontType1);
        itemImages = (SliderLayout) findViewById(R.id.itemImages);
        for (int i = 0; i < 3; i++) {
            final String image = "f" + (Integer.toString(i + 1));
            DefaultSliderView sliderViews = new DefaultSliderView(this);
            sliderViews
                    .description(SliderLayout.Transformer.DepthPage.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName())).setScaleType(BaseSliderView.ScaleType.CenterInside);
            sliderViews.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    Intent intent=new Intent(ItemDetails.this,ImageViewer.class);
                    intent.putExtra("Image",image);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                }
            });
            itemImages.addSlider(sliderViews);
        }
        itemImages.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        itemImages.stopAutoCycle();

        viewDesigner=(TextView)findViewById(R.id.view_designer);


        price=(TextView)findViewById(R.id.price);
        stock=(TextView)findViewById(R.id.stock);
        //-----------Add to favorite and Sharing--------------------------
        favorite=(ImageView)findViewById(R.id.fav);
        favCountString=(TextView)findViewById(R.id.favCount);
        favCountString.setText(getResources().getString(R.string.favorite_count,favCount));
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    favCount--;
                    favorite.setImageResource(R.drawable.fav_no);
                    Toast.makeText(ItemDetails.this, R.string.remove_fav_msg, Toast.LENGTH_LONG).show();
                    favCountString.setText(getResources().getString(R.string.favorite_count, favCount));
                    isFav = false;
                } else {
                    favCount++;
                    favorite.setImageResource(R.drawable.fav);
                    Toast.makeText(ItemDetails.this, R.string.add_fav_msg, Toast.LENGTH_LONG).show();
                    favCountString.setText(getResources().getString(R.string.favorite_count, favCount));
                    isFav = true;
                }
            }
        });

        share=(ImageView)findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ItemDetails.this,R.string.share_image,Toast.LENGTH_LONG).show();
                try {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "f" + Integer.toString(itemImages.getCurrentPosition() + 1)));
                    startActivity(Intent.createChooser(share, getString(R.string.share_image)));
                    overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                } catch (Exception e) {
                    Toast.makeText(ItemDetails.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        //-----------------------Similar items grid---------------------------
        itemsGrid((LinearLayout)findViewById(R.id.similarProducts));
        //-----------------------Related items grid---------------------------
        itemsGrid((LinearLayout)findViewById(R.id.relatedProducts));
    }
    public void itemsGrid(LinearLayout fillingArea){
        LinearLayout itemRow=new LinearLayout(ItemDetails.this);
        itemRow.setOrientation(LinearLayout.HORIZONTAL);
        itemRow= (LinearLayout) inflater.inflate(R.layout.items_two_coloum_frame, null);
        itemRow.setPadding(0,10,0,5);
        LinearLayout leftItem=(LinearLayout)itemRow.findViewById(R.id.LinearLeft);
        LinearLayout rightItem=(LinearLayout)itemRow.findViewById(R.id.LinearRight);
        View item=inflater.inflate(R.layout.grid_item, null);
        ImageView imageView=(ImageView)item.findViewById(R.id.gridImg);
        Picasso.with(ItemDetails.this).load(R.drawable.k1).into(imageView);
        leftItem.addView(item);
        View item2=inflater.inflate(R.layout.grid_item, null);
        ImageView imageView2=(ImageView)item2.findViewById(R.id.gridImg);
        Picasso.with(ItemDetails.this).load(R.drawable.k2).into(imageView2);
        rightItem.addView(item2);
        fillingArea.addView(itemRow);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetails.this, ItemDetails.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
            }
        });
        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetails.this, ItemDetails.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
            }
        });
        //More---------------
        TextView more = new TextView(ItemDetails.this);
        more.setText("more");
        more.setGravity(Gravity.RIGHT);
        if (Build.VERSION.SDK_INT < 23) {
            more.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        } else {
            more.setTextAppearance(android.R.style.TextAppearance_Medium);
        }
        more.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            more.setTextColor(getColor(R.color.accent));
        }
        else {
            more.setTextColor(getResources().getColor(R.color.accent));
        }
        more.setPadding(5,5,5,0);
        fillingArea.addView(more);
    }
    //---------------Menu creation------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user:
                Intent intentUser = new Intent(this, User.class);
                startActivity(intentUser);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                break;
            case R.id.boutique:
                Intent intentBoutique = new Intent(this, BoutiqueDetails.class);
                startActivity(intentBoutique);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_exit1,R.anim.slide_exit2);
    }

    //-------------------- Async tasks---------------------------------
    public class ProductDetails extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData,userID;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(ItemDetails.this);
        String productName,descriptionString,priceString,designerID;
        Boolean isOutOfStock;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
              pDialog.setMessage(getResources().getString(R.string.wait));
            pDialog.setCancelable(false);
            pDialog.show();
            //----------encrypting ---------------------------
           // usernameString=cryptography.Encrypt(usernameString);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/Products";
            HttpURLConnection c = null;
            try {
                postData = "{\"productID\":\"" +"570A044A-4DBA-4770-BCA7-331D2C0834AE" + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\",\"userID\":\"" + "2E522A80-5FED-4BDB-A433-15D78ED22162"+ "\"}";
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-type", "application/json; charset=utf-16");
                c.setRequestProperty("Content-length", Integer.toString(postData.length()));
                c.setDoInput(true);
                c.setDoOutput(true);
                c.setUseCaches(false);
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                DataOutputStream wr = new DataOutputStream(c.getOutputStream());
                wr.writeBytes(postData);
                wr.flush();
                wr.close();
                status = c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201: BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();
                        int a=sb.indexOf("[");
                        int b=sb.lastIndexOf("]");
                        strJson=sb.substring(a, b + 1);
                     //   strJson=cryptography.Decrypt(strJson);
                        strJson="{\"JSON\":" + strJson.replace("\\\"","\"") + "}";
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                msg=ex.getMessage();
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                        msg=ex.getMessage();
                    }
                }
            }
            if(strJson!=null)
            {try {
                JSONObject jsonRootObject = new JSONObject(strJson);
                jsonArray = jsonRootObject.optJSONArray("JSON");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    msg=jsonObject.optString("Message");
                    pass=jsonObject.optBoolean("Flag",true);
                    productName=jsonObject.optString("Name");
                    descriptionString=jsonObject.optString("Description");
                    priceString=String.format(Locale.US,"%.2f", jsonObject.optDouble("Price"));
                    isOutOfStock =jsonObject.optBoolean("IsOutOfStock");
                    designerID=jsonObject.optString("DesignerID");
                    isFav=jsonObject.optBoolean("isFav");
                    favCount=jsonObject.optInt("FavCount");
                }
            } catch (Exception ex) {
                msg=ex.getMessage();
            }}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(!pass) {
                new AlertDialog.Builder(ItemDetails.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setCancelable(false).show();
            }
            else {
                android.support.v7.app.ActionBar ab = getSupportActionBar();
                if (ab != null) {
                    ab.setTitle(productName);
                }
                description.setText(descriptionString);
                price.setText(getResources().getString(R.string.rs, priceString));
                if(isOutOfStock){
                    stock.setText("Out of Stock");
                    stock.setTextColor(Color.RED);
                }
                if (isFav) {
                    favorite.setImageResource(R.drawable.fav);
                    favCountString.setText(getResources().getString(R.string.favorite_count, favCount));
                } else {
                    favorite.setImageResource(R.drawable.fav_no);
                    favCountString.setText(getResources().getString(R.string.favorite_count, favCount));
                }
                viewDesigner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ItemDetails.this, OwnerAndDesigner.class);
                        intent.putExtra("ownerORdesigner","designer");
                        intent.putExtra("designerID",designerID);
                        startActivity(intent  );
                        overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                    }
                });
            }
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
