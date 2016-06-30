package com.tech.thrithvam.boutiqueapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Home extends AppCompatActivity implements ObservableScrollViewCallbacks {
    Constants constants=new Constants();
    DatabaseHandler db=new DatabaseHandler(this);
    LinearLayout homeScreen;
    LayoutInflater inflater;
    ListView sideBar;
    ArrayList<String> categoryList;
    Dictionary<String,String> categoryCode=new Hashtable<>();
    ArrayAdapter categoryAdapter;
    SliderLayout newArrivals;
    //  int loadedCategoryCount=0;
 //   ArrayList<View> cards=new ArrayList<>();
    ObservableScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setElevation(0);
        startService(new Intent(this, Services.class)); //calling the service
        db.flushNotifications();
        if (isOnline()){
            new GetCategories().execute();
            new BannerSlider().execute();
        }
        else {
            Toast.makeText(Home.this,R.string.network_off_alert,Toast.LENGTH_LONG).show();
        }

        sideBar=(ListView)findViewById(R.id.drawer);

        inflater = (LayoutInflater)Home.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        homeScreen=(LinearLayout)findViewById(R.id.homeScreen);
       // final TextView newArrivalsLabel = (TextView) findViewById(R.id.new_arrivals_label);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");

        //-------------------------hide actionbar on scroll----------------------------
        scrollView=(ObservableScrollView)findViewById(R.id.homeScroll);
        scrollView.setScrollViewCallbacks(this);

        //------------------------------Home Screen Slider-------------------------------
         newArrivals = (SliderLayout) findViewById(R.id.newArrivals);

//see whether reached scroll bottom
      //  ScrollView scrollView=(ScrollView)findViewById(R.id.ScrlViewOfSPDetails);



    }
    //-------------------------------- Items Grid----------------------------------
    public void productsOfCategory(Integer loadedCategoryCount){
        int currentPos=loadedCategoryCount+1;
        if(currentPos<categoryList.size())
        {
            new GetProductsByCategory().execute(currentPos);
        }


       // homeScreen.addView(cards.get(loadedCategoryCount));


    }

    //---------------Menu creation---------------------------------------------
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
            case R.id.sidebar:
                DrawerLayout drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
                RelativeLayout drawer=(RelativeLayout)findViewById(R.id.rightDrawer);
                if(drawerLayout.isDrawerOpen(Gravity.RIGHT))
                    drawerLayout.closeDrawer(drawer);
                else
                    drawerLayout.openDrawer(drawer);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
    //--------------Actionbar hiding while scrolling-------------------------------
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }
    @Override
    public void onDownMotionEvent() {
    }
    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
        if (scrollState == ScrollState.UP) {
                if (ab.isShowing()) {
                    ab.hide();
                }
        } else if (scrollState == ScrollState.DOWN) {
                if (!ab.isShowing()) {
                    ab.show();
                }
            }
        }
    }

    //------------------------------Async Tasks-----------------------------
    public class GetCategories extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(Home.this);
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
                new AlertDialog.Builder(Home.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
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

                categoryAdapter = new ArrayAdapter<>(Home.this, R.layout.side_bar_item, categoryList);
                sideBar.setAdapter(categoryAdapter);
                sideBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(!categoryList.get(position).equals("")){
                            if(categoryList.get(position).equals(getResources().getString(R.string.my_orders_sidebar))){
                                Intent orderIntent=new Intent(Home.this,OrderStatus.class);
                                startActivity(orderIntent);
                                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                            }
                            else {
                                Intent categoryIntent=new Intent(Home.this,GridOfProducts.class);
                                categoryIntent.putExtra("CategoryCode",categoryCode.get(categoryList.get(position)));
                                categoryIntent.putExtra("Category",categoryList.get(position).replace("\uD83D\uDC49\t",""));
                                startActivity(categoryIntent);
                                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                            }
                        }
                    }
                });
                //products under category loading on Home screen
                productsOfCategory(-1);
            }
        }
    }
    public class GetProductsByCategory extends AsyncTask<Integer, Void, Integer> {
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
         /*   pDialog.setMessage(getResources().getString(R.string.wait));
            pDialog.setCancelable(false);
            pDialog.show();*/
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/ProductsByCategory";
            HttpURLConnection c = null;
            try {
                postData =  "{\"CategoryCode\":\"" + categoryCode.get(categoryList.get(arg0[0])) + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\",\"userID\":\"" + (db.GetUserDetail("UserID")==null?"":db.GetUserDetail("UserID"))+ "\",\"limit\":\"" + constants.productsCountLimit + "\"}";
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
                    data[0]=jsonObject.optString("ProductID");
                    data[1]=jsonObject.optString("Name").replace("\\u0026", "&");
                    data[2]=jsonObject.optString("Image");
                    data[3]=jsonObject.optString("Discount");
                    productItems.add(data);
                }
                String[] data=new String[4];                //For more
                data[0]="";
                data[1]=categoryCode.get(categoryList.get(arg0[0]));
                data[2]=categoryList.get(arg0[0]);
                data[3]="";
                productItems.add(data);
            } catch (Exception ex) {
                msg=ex.getMessage();
            }}
            return arg0[0];
        }

        @Override
        protected void onPostExecute(final Integer viewPos) {
           // super.onPostExecute(result);
        /*    if (pDialog.isShowing())
                pDialog.dismiss();*/
            final LinearLayout categoryCard= (LinearLayout) inflater.inflate(R.layout.products_of_category,null);
            if(!pass) {
              /*  new AlertDialog.Builder(Home.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(R.string.no_items)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                             //   finish();
                            }
                        }).setCancelable(false).show();*/
            }
            else {
                TextView categoryTitle =(TextView)categoryCard.findViewById(R.id.title);
                Typeface type = Typeface.createFromAsset(getAssets(), "fonts/avenirnextregular.ttf");
               // categoryTitle.setTypeface(type);
                categoryTitle.setText(categoryList.get(viewPos).replace("\uD83D\uDC49\t",""));
                categoryTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent categoryIntent=new Intent(Home.this,GridOfProducts.class);
                        categoryIntent.putExtra("CategoryCode",categoryCode.get(categoryList.get(viewPos)));
                        categoryIntent.putExtra("Category",categoryList.get(viewPos).replace("\uD83D\uDC49\t",""));
                        startActivity(categoryIntent);
                        overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                    }
                });

                CustomAdapter adapter=new CustomAdapter(Home.this, productItems,"homeGrid");
                TwoWayView horizontalGrid=(TwoWayView)categoryCard.findViewById(R.id.gridHorizontal);
                horizontalGrid.setOrientation(TwoWayView.Orientation.HORIZONTAL);
                horizontalGrid.setItemMargin(15);
                horizontalGrid.setAdapter(adapter);


                homeScreen.addView(categoryCard);
            }
            productsOfCategory(viewPos);
           /* scrollView.getViewTreeObserver().removeOnScrollChangedListener(null);
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    // if(!otherTypesLoaded){
                    int diff = homeScreen.getBottom()-(scrollView.getHeight()+scrollView.getScrollY());
                    if( diff <= 0 )
                    { //  Toast.makeText(BeautyParlour.this, "Bottom has been reached",Toast.LENGTH_LONG).show();
                        //  new GetDetailsOfOtherStyles().execute();
                        //    otherTypesLoaded =true;
                        // loadedCategoryCount++;
                        productsOfCategory(viewPos);

                    } // super.onScrollChanged(l, t, oldl, oldt);
                    //  }
                }
            });*/
        }
    }
    public class BannerSlider extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ArrayList<String[]> bannerItems=new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/BannerImages";
            HttpURLConnection c = null;
            try {
                postData =  "{\"boutiqueID\":\"" + constants.BoutiqueID + "\"}";
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
                    String[] data=new String[3];
                    data[0]=jsonObject.optString("ProductID");
                    data[1]=jsonObject.optString("CategoryCode");
                    data[2]=getResources().getString(R.string.url) + jsonObject.optString("Image").substring((jsonObject.optString("Image")).indexOf("Media"));
                    bannerItems.add(data);
                }
            } catch (Exception ex) {
                msg=ex.getMessage();
            }}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!pass) {
                newArrivals.setVisibility(View.GONE);
            }
            else {
                for (int i=0;i<bannerItems.size();i++) {
                    final int fi=i;
                    DefaultSliderView sliderViews = new DefaultSliderView(Home.this);
                    sliderViews
                            .image(bannerItems.get(fi)[2])
                            .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                    sliderViews.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            Intent intent;
                            if(!bannerItems.get(fi)[0].equals("null")){
                                intent= new Intent(Home.this, ItemDetails.class);
                                intent.putExtra("ProductID",bannerItems.get(fi)[0]);
                            }
                            else if(!bannerItems.get(fi)[1].equals("null")){
                                intent= new Intent(Home.this, GridOfProducts.class);
                                intent.putExtra("CategoryCode",bannerItems.get(fi)[1]);
                                intent.putExtra("Category","");
                            }
                            else{
                                return;
                            }
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                        }
                    });
                    newArrivals.addSlider(sliderViews);
                    newArrivals.setPresetTransformer(SliderLayout.Transformer.Accordion);
                }
            }
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle(R.string.exit)
                .setMessage(R.string.exit_q)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //clear cache
                        Toast.makeText(Home.this,"Cache Memory Cleared!!",Toast.LENGTH_SHORT).show();
                        try {
                            File dir = getApplicationContext().getCacheDir();
                            if (dir != null && dir.isDirectory()) {
                                deleteDir(dir);
                            }
                        } catch (Exception e) {}
                        finish();

                    }
                }).setNegativeButton(R.string.no, null).show();

    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();}
}
