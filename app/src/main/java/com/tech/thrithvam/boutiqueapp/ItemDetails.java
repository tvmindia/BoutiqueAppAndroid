package com.tech.thrithvam.boutiqueapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemDetails extends AppCompatActivity {
    Constants constants=new Constants();
    DatabaseHandler db=new DatabaseHandler(this);
    ImageView favorite;
    ImageView share;
    ImageView review;
    ImageView chat;
    Boolean isFav=false;
    Integer favCount=0;
    TextView favCountString;
    SliderLayout itemImages;
    LayoutInflater inflater;
    TextView description;
    TextView viewDesigner;
    TextView price;
    TextView actualPrice;
    ImageView offer;
    TextView stock;
    String productID;
    String productName;
    Bundle extras;
    Boolean relatedItemsLoaded=false;
    ListView sideBar;
    ArrayList<String> categoryList;
    Dictionary<String,String> categoryCode=new Hashtable<>();
    ArrayAdapter categoryAdapter;
    CardView itemDetailsCard;
    CardView relatedItemsCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        extras = getIntent().getExtras();
        productID = extras.getString("ProductID");
        getSupportActionBar().setElevation(0);
        sideBar = (ListView) findViewById(R.id.left_drawer);
        if (isOnline()) {
            new ProductDetails().execute();
            new GetCategories().execute();
            new ProductImages().execute();
        } else {
            Toast.makeText(ItemDetails.this, R.string.network_off_alert, Toast.LENGTH_LONG).show();
            finish();
        }
        inflater = (LayoutInflater) ItemDetails.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Typeface fontType1 = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        description = (TextView) findViewById(R.id.description);
        description.setTypeface(fontType1);
        itemImages = (SliderLayout) findViewById(R.id.itemImages);
       /* for (int i = 0; i < 3; i++) {
            final String image = "f" + (Integer.toString(i + 1));
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
        }*/


        viewDesigner = (TextView) findViewById(R.id.view_designer);

        price = (TextView) findViewById(R.id.price);
        actualPrice = (TextView) findViewById(R.id.actualPrice);
        offer = (ImageView) findViewById(R.id.offer);
        stock = (TextView) findViewById(R.id.stock);
        //-----------Add to favorite and Sharing and review--------------------------
        favorite = (ImageView) findViewById(R.id.fav);
        favCountString = (TextView) findViewById(R.id.favCount);
        favCountString.setText(getResources().getString(R.string.favorite_count, favCount));
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.GetUserDetail("UserID") != null) {
                    new AddOrRemoveFromFavorite().execute();
                } else {
                    Toast.makeText(ItemDetails.this, R.string.please_login, Toast.LENGTH_LONG).show();
                    Intent intentUser = new Intent(ItemDetails.this, User.class);
                    startActivity(intentUser);
                    finish();
                    overridePendingTransition(R.anim.slide_entry1, R.anim.slide_entry2);
                }
            }
        });

        share = (ImageView) findViewById(R.id.share);

        review = (ImageView) findViewById(R.id.review);
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReview = new Intent(ItemDetails.this, ProductReviews.class);
                intentReview.putExtra("productName", productName);
                intentReview.putExtra("productID", productID);
                startActivity(intentReview);
                overridePendingTransition(R.anim.slide_entry1, R.anim.slide_entry2);
            }
        });

        chat = (ImageView) findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.GetUserDetail("UserID") != null) {
                    Intent intentChat = new Intent(ItemDetails.this, Chat.class);
                    intentChat.putExtra("productName", productName);
                    intentChat.putExtra("productID", productID);
                    startActivity(intentChat);
                    overridePendingTransition(R.anim.slide_entry1, R.anim.slide_entry2);
                } else {
                    Toast.makeText(ItemDetails.this, R.string.please_login, Toast.LENGTH_LONG).show();
                    Intent intentUser = new Intent(ItemDetails.this, User.class);
                    startActivity(intentUser);
                    overridePendingTransition(R.anim.slide_entry1, R.anim.slide_entry2);
                }

            }
        });

        final ScrollView scrollView=(ScrollView)findViewById(R.id.itemDetailsScroll);
        itemDetailsCard=(CardView)findViewById(R.id.itemDetailsCard);
        relatedItemsCard=(CardView)findViewById(R.id.relatedItemsCard);
        scrollView.getViewTreeObserver().removeOnScrollChangedListener(null);
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if(!relatedItemsLoaded){
                    int diff = itemDetailsCard.getBottom()-(scrollView.getHeight()+scrollView.getScrollY());
                    if( diff <= 0 )
                    {
                       new RelatedProducts().execute();
                        relatedItemsLoaded=true;
                    } // super.onScrollChanged(l, t, oldl, oldt);
                    }
                }
            });
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
                finish();
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
    public class GetCategories extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(ItemDetails.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage(getResources().getString(R.string.wait));
            pDialog.setCancelable(false);
            pDialog.show();
            categoryList=new ArrayList<>();
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/Categories";
            HttpURLConnection c = null;
            try {
                postData = "{\"boutiqueID\":\"" + constants.BoutiqueID + "\"}";
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
                    categoryList.add("\uD83D\uDC49\t"+jsonObject.optString("Name").replace("\\u0026", "&"));
                    categoryCode.put("\uD83D\uDC49\t"+jsonObject.optString("Name"),jsonObject.optString("CategoryCode"));
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
                                finish();
                            }
                        }).setCancelable(false).show();
            }
            else {
                //Links other than category
                categoryList.add("");
                categoryList.add(getResources().getString(R.string.trending));
                categoryCode.put(getResources().getString(R.string.trending),"trends");
                categoryList.add(getResources().getString(R.string.my_favorites));
                categoryCode.put(getResources().getString(R.string.my_favorites),"myfav");
                categoryList.add(getResources().getString(R.string.my_orders_sidebar));

                categoryAdapter = new ArrayAdapter<>(ItemDetails.this, R.layout.side_bar_item, categoryList);
                sideBar.setAdapter(categoryAdapter);
                sideBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(!categoryList.get(position).equals("")){
                            if(categoryList.get(position).equals(getResources().getString(R.string.my_orders_sidebar))){
                                Intent orderIntent=new Intent(ItemDetails.this,OrderStatus.class);
                                startActivity(orderIntent);
                                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                            }
                            else {
                                Intent categoryIntent=new Intent(ItemDetails.this,GridOfProducts.class);
                                categoryIntent.putExtra("CategoryCode",categoryCode.get(categoryList.get(position)));
                                categoryIntent.putExtra("Category",categoryList.get(position).replace("\uD83D\uDC49\t",""));
                                startActivity(categoryIntent);
                                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                            }
                        }
                    }
                });
            }
        }
    }
    public class ProductDetails extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(ItemDetails.this);
        String descriptionString,priceString,discount,designerID,designerName;
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
                postData = "{\"productID\":\"" + productID + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\",\"userID\":\"" + (db.GetUserDetail("UserID")==null?"":db.GetUserDetail("UserID"))+ "\"}";
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
                    productName=jsonObject.optString("Name").replace("\\u0026", "&");
                    descriptionString=jsonObject.optString("Description").replace("\\u0026", "&");
                    priceString=String.format(Locale.US,"%.2f", jsonObject.optDouble("Price"));
                    discount=jsonObject.optString("Discount");
                    isOutOfStock =jsonObject.optBoolean("IsOutOfStock");
                    designerID=jsonObject.optString("DesignerID");
                    designerName=jsonObject.optString("DesignerName");
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
                if(!discount.equals("null")){
                    if(Integer.parseInt(discount)>0){
                        discount=String.format(Locale.US,"%.2f", Double.parseDouble(discount));
                        price.setText(getResources().getString(R.string.rs, String.format(Locale.US,"%.2f",(Double.parseDouble(priceString)-Double.parseDouble(discount)))));
                        actualPrice.setVisibility(View.VISIBLE);
                        actualPrice.setText(getResources().getString(R.string.rs, priceString));
                        actualPrice.setPaintFlags(actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        offer.setVisibility(View.VISIBLE);
                    }
                    else {
                        price.setText(getResources().getString(R.string.rs, priceString));
                    }
                }
                else {
                    price.setText(getResources().getString(R.string.rs, priceString));
                }

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
                if(!designerName.equals("null")){
                    viewDesigner.setText(getResources().getString(R.string.designer_name, designerName));
                }
            }
        }
    }
    public class AddOrRemoveFromFavorite extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg,AddOrRemove;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(ItemDetails.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage(getResources().getString(R.string.wait));
            pDialog.setCancelable(false);
            pDialog.show();
            if (isFav) {
                AddOrRemove="remove";
                favCount--;
                favorite.setImageResource(R.drawable.fav_no);
                Toast.makeText(ItemDetails.this, R.string.remove_fav_msg, Toast.LENGTH_LONG).show();
                favCountString.setText(getResources().getString(R.string.favorite_count, favCount));
                isFav = false;
            } else {
                AddOrRemove="add";
                favCount++;
                favorite.setImageResource(R.drawable.fav);
                Toast.makeText(ItemDetails.this, R.string.add_fav_msg, Toast.LENGTH_LONG).show();
                favCountString.setText(getResources().getString(R.string.favorite_count, favCount));
                isFav = true;
            }
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/Favorites";
            HttpURLConnection c = null;
            try {
                postData = "{\"productID\":\"" + productID + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\",\"userID\":\"" + db.GetUserDetail("UserID")+ "\",\"AddOrRemove\":\"" + AddOrRemove + "\"}";
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
                Toast.makeText(ItemDetails.this,msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class ProductImages extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(ItemDetails.this);
        ArrayList<String> imgurls=new ArrayList<>();
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
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/ProductImages";
            HttpURLConnection c = null;
            try {
                postData = "{\"productID\":\"" + productID + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\"}";
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
                    imgurls.add(getResources().getString(R.string.url) + jsonObject.optString("Image").substring((jsonObject.optString("Image")).indexOf("Media")));
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
                for (int i=0;i<imgurls.size();i++) {
                    final int fi=i;
                    DefaultSliderView sliderViews = new DefaultSliderView(ItemDetails.this);
                    sliderViews
                            .description(SliderLayout.Transformer.DepthPage.toString())
                            .image(imgurls.get(i))
                            .setScaleType(BaseSliderView.ScaleType.CenterInside);
                    sliderViews.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            Intent intent=new Intent(ItemDetails.this,ImageViewer.class);
                            intent.putExtra("Imageurl",imgurls.get(fi));
                            intent.putExtra("ProductName",productName);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                        }
                    });
                    itemImages.addSlider(sliderViews);
                    itemImages.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
                    itemImages.stopAutoCycle();

                    share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(ItemDetails.this,R.string.share_image,Toast.LENGTH_LONG).show();

                            // Get access to bitmap image from view
                            final ImageView ivImage = new ImageView(ItemDetails.this);
                            Picasso.with(ItemDetails.this)
                                    .load(imgurls.get(itemImages.getCurrentPosition()))
                                    .into(ivImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    Uri bmpUri = getLocalBitmapUri(ivImage,productName+"@"+constants.BoutiqueName);
                                    if (bmpUri != null) {
                                        Intent shareIntent = new Intent();
                                        shareIntent.setAction(Intent.ACTION_SEND);
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                        shareIntent.setType("image/*");
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, productName+"\t@\t"+constants.BoutiqueName);
                                        startActivity(Intent.createChooser(shareIntent, "Share Image"));
                                    }
                                }
                                @Override
                                public void onError() {
                                }
                            });
                        }
                    });
                }
            }
        }
    }
    public class RelatedProducts extends AsyncTask<Void, Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        // ProgressDialog pDialog=new ProgressDialog(Home.this);
        ArrayList<String[]> productItems=new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            relatedItemsCard.setVisibility(View.GONE);
         /*   pDialog.setMessage(getResources().getString(R.string.wait));
            pDialog.setCancelable(false);
            pDialog.show();*/
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/RelatedProducts";
            HttpURLConnection c = null;
            try {
                postData =  "{\"productID\":\"" + productID + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\",\"limit\":\"" + constants.relatedProductsCountLimit + "\"}";
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
                    String[] data=new String[4];
                    data[0]=jsonObject.optString("RelatedProductsID");
                    data[1]=jsonObject.optString("Name").replace("\\u0026", "&");
                    data[2]=jsonObject.optString("Image");
                    data[3]=jsonObject.optString("Discount");
                    productItems.add(data);
                }
            } catch (Exception ex) {
                msg=ex.getMessage();
            }}
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
             super.onPostExecute(result);
            if(!pass) {
                relatedItemsCard.setVisibility(View.GONE);
            }
            else {
                relatedItemsCard.setVisibility(View.VISIBLE);
                CustomAdapter adapter=new CustomAdapter(ItemDetails.this, productItems,"relatedItems");
                TwoWayView horizontalGrid=(TwoWayView)findViewById(R.id.gridRelatedItems);
                horizontalGrid.setOrientation(TwoWayView.Orientation.HORIZONTAL);
                horizontalGrid.setItemMargin(15);
                horizontalGrid.setAdapter(adapter);
            }
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView,String fileName) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
