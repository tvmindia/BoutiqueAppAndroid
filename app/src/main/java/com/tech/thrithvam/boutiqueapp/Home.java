package com.tech.thrithvam.boutiqueapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setElevation(0);
        startService(new Intent(this, Notification.class)); //calling the service
        db.flushNotifications();
        if (isOnline()){
            new GetCategories().execute();
        }
        else {
            Toast.makeText(Home.this,R.string.network_off_alert,Toast.LENGTH_LONG).show();
        }

        sideBar=(ListView)findViewById(R.id.left_drawer);

        inflater = (LayoutInflater)Home.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        homeScreen=(LinearLayout)findViewById(R.id.homeScreen);
       // final TextView newArrivalsLabel = (TextView) findViewById(R.id.new_arrivals_label);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");



        //-------------------------hide actionbar on scroll----------------------------
      final ObservableScrollView scrollView=(ObservableScrollView)findViewById(R.id.homeScroll);
      scrollView.setScrollViewCallbacks(this);

        //------------------------------slider for new arrivals-------------------------------
        SliderLayout sliderShow6 = (SliderLayout) findViewById(R.id.slider6);
        for (int i = 0; i < 5; i++) {
            final String image = "na" + (Integer.toString(i + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.Accordion.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow6.addSlider(textSliderViews);

            sliderShow6.setPresetTransformer(SliderLayout.Transformer.Accordion);
        }
        //-------------------------------- Items Grid----------------------------------



        itemhorizontal("Saree", true); //temporary logic
        itemhorizontal("", false);
        itemhorizontal("Tops",true);
        itemhorizontal("",false);
        itemhorizontal("Skirts",true);
        itemhorizontal("",false);
        itemhorizontal("Kids",true);
        itemhorizontal("",false);
        itemhorizontal("Kurthas",true);
        itemhorizontal("",false);

    }
    public void itemhorizontal(String category, Boolean isNewCat){
        if(isNewCat) { //temporary logic
            //Title-----------
            TextView categoryTitle = new TextView(Home.this);
            categoryTitle.setText(category);
            if (Build.VERSION.SDK_INT < 23) {
                categoryTitle.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            } else {
                categoryTitle.setTextAppearance(android.R.style.TextAppearance_Medium);
            }
            categoryTitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            categoryTitle.setPadding(5, 5, 0, 5);
            categoryTitle.setTextColor(Color.BLUE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                categoryTitle.setTextColor(Home.this.getColor(R.color.accent));
                categoryTitle.setBackgroundColor(Home.this.getColor(R.color.whiteBackground));
            }
            else {
                categoryTitle.setTextColor(getResources().getColor(R.color.accent));
            categoryTitle.setBackgroundColor(getResources().getColor(R.color.whiteBackground));}
            homeScreen.addView(categoryTitle);
        }
        LinearLayout itemRow=new LinearLayout(Home.this);
        itemRow.setOrientation(LinearLayout.HORIZONTAL);
        itemRow= (LinearLayout) inflater.inflate(R.layout.items_two_coloum_frame,null);
        LinearLayout leftItem=(LinearLayout)itemRow.findViewById(R.id.LinearLeft);
        LinearLayout rightItem=(LinearLayout)itemRow.findViewById(R.id.LinearRight);
        View item=inflater.inflate(R.layout.grid_item, null);
        ImageView imageView=(ImageView)item.findViewById(R.id.gridImg);
        if(isNewCat) {
        Picasso.with(Home.this).load(R.drawable.s1).into(imageView);}
        else {
            Picasso.with(Home.this).load(R.drawable.s3).into(imageView);
        }
        leftItem.addView(item);
        View item2=inflater.inflate(R.layout.grid_item, null);
        ImageView imageView2=(ImageView)item2.findViewById(R.id.gridImg);
        if(isNewCat) {
        Picasso.with(Home.this).load(R.drawable.s2).into(imageView2);}
        else {
            Picasso.with(Home.this).load(R.drawable.s4).into(imageView2);
        }
        rightItem.addView(item2);
        homeScreen.addView(itemRow);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, ItemDetails.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
            }
        });
        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home.this,ItemDetails.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
            }
        });
        if(!isNewCat) { ////////temporary logic
            //More---------------
            TextView more = new TextView(Home.this);
            more.setText("more");
            more.setGravity(Gravity.RIGHT);
            if (Build.VERSION.SDK_INT < 23) {
                more.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            } else {
                more.setTextAppearance(android.R.style.TextAppearance_Medium);
            }
            more.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            more.setPadding(5, 5, 5, 5);
            more.setTextColor(Color.BLUE);
            homeScreen.addView(more);
        }
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
                    categoryList.add("\uD83D\uDC49\t"+jsonObject.optString("Name"));
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
                categoryAdapter = new ArrayAdapter<>(Home.this, R.layout.side_bar_item, categoryList);
                sideBar.setAdapter(categoryAdapter);
                sideBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent categoryIntent=new Intent(Home.this,GridOfProducts.class);
                        categoryIntent.putExtra("CategoryCode",categoryCode.get(categoryList.get(position)));
                        categoryIntent.putExtra("Category",categoryList.get(position).replace("\uD83D\uDC49\t",""));
                        Toast.makeText(Home.this,categoryList.get(position)+"-"+categoryCode.get(categoryList.get(position)),Toast.LENGTH_SHORT).show();
                        startActivity(categoryIntent);
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
