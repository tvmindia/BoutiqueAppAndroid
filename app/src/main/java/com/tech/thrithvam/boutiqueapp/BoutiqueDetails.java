package com.tech.thrithvam.boutiqueapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoutiqueDetails extends AppCompatActivity {
    Constants constants=new Constants();
    ImageView boutiqueImg;
    TextView aboutUs;
    TextView caption;
    TextView year;
    TextView location;
    TextView address;
    TextView viewMap;
    TextView phone;
    TextView timing;
    TextView workingDays;
    TextView fbLink;
    TextView instagramLink;
    TextView owners;
    TextView designers;
    ListView sideBar;
    ArrayList<String> categoryList;
    Dictionary<String,String> categoryCode=new Hashtable<>();
    ArrayAdapter categoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boutique_details);

        sideBar=(ListView)findViewById(R.id.left_drawer);
        if (isOnline()){
            new GetBoutiqueDetails().execute();
            new GetCategories().execute();
        }
        else {
            Toast.makeText(BoutiqueDetails.this,R.string.network_off_alert,Toast.LENGTH_LONG).show();
            finish();
        }

        //----------------setting fonts------------------------
        Typeface fontType1 = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        Typeface fontType2 = Typeface.createFromAsset(getAssets(), "fonts/handwriting.ttf");

        boutiqueImg=(ImageView)findViewById(R.id.boutiqueImg);
        aboutUs=(TextView)findViewById(R.id.aboutUs);
        caption=(TextView)findViewById(R.id.caption);
        year=(TextView)findViewById(R.id.year);
        location=(TextView)findViewById(R.id.location);
        address=(TextView)findViewById(R.id.address);
        viewMap=(TextView)findViewById(R.id.view_map);
        phone=(TextView)findViewById(R.id.phone);
        timing=(TextView)findViewById(R.id.timing);
        workingDays=(TextView)findViewById(R.id.workingDays);
        fbLink=(TextView)findViewById(R.id.fbLink);
        instagramLink=(TextView)findViewById(R.id.instagramLink);
        owners=(TextView)findViewById(R.id.owners);
        designers=(TextView)findViewById(R.id.designers);
        //---------------set boutique details------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boutiqueImg.setImageDrawable(getDrawable(R.drawable.boutique));
        }
        else {
            boutiqueImg.setImageDrawable(getResources().getDrawable(R.drawable.boutique));
        }
        aboutUs.setTypeface(fontType1);
        caption.setTypeface(fontType2);
        year.setTypeface(fontType1);
        location.setTypeface(fontType1);
        address.setTypeface(fontType1);
        viewMap.setTypeface(fontType1);
        phone.setTypeface(fontType1);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_exit1,R.anim.slide_exit2);
    }
    //-------------------- Async tasks---------------------------------
    public class GetBoutiqueDetails extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(BoutiqueDetails.this);
        String nameString, startedYearString, aboutUsString, captionString, locationString, addressString, phoneString, timingString, workingDaysString, fBLinkString, instagramLinkString,latlong;
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
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/Boutique";
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
                    nameString =jsonObject.optString("Name");
                    startedYearString =jsonObject.optString("StartedYear");
                    aboutUsString =jsonObject.optString("AboutUs");
                    captionString =jsonObject.optString("Caption");
                    locationString =jsonObject.optString("Location");
                    addressString =jsonObject.optString("Address");
                    phoneString =jsonObject.optString("Phone");
                    timingString =jsonObject.optString("Timing");
                    workingDaysString =jsonObject.optString("WorkingDays");
                    fBLinkString ="https://"+jsonObject.optString("FBLink");
                    instagramLinkString ="https://"+jsonObject.optString("InstagramLink");
                    latlong=jsonObject.optString("latlong");
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
                new AlertDialog.Builder(BoutiqueDetails.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false).show();
            }
            else {
                android.support.v7.app.ActionBar ab = getSupportActionBar();
                if (ab != null) {
                    ab.setTitle(nameString);
                }
                caption.setText(captionString);
                aboutUs.setText(aboutUsString);
                year.setText(getResources().getString(R.string.since,startedYearString));
                location.setText(locationString);
                address.setText(addressString);
                viewMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + (latlong)));
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                phone.setText(phoneString);
                phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {                                   //Phone call function
                        Uri number = Uri.parse("tel:" + phoneString);
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                        startActivity(callIntent);
                        overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                    }
                });


                timing.setText(timingString);
                workingDays.setText(workingDaysString);

                fbLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {                                                   //opening links
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fBLinkString));
                        startActivity(browserIntent);
                        overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                    }
                });

                instagramLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {                                           //opening links
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramLinkString));
                        startActivity(browserIntent);
                        overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                    }
                });
                owners.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(BoutiqueDetails.this, OwnerAndDesigner.class);
                        intent.putExtra("ownerORdesigner","owner");
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                    }
                });
                designers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(BoutiqueDetails.this, OwnerAndDesigner.class);
                        intent.putExtra("ownerORdesigner","designer");
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                    }
                });
            }
        }
    }
    public class GetCategories extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(BoutiqueDetails.this);
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
                new AlertDialog.Builder(BoutiqueDetails.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false).show();
            }
            else {
                categoryAdapter = new ArrayAdapter<>(BoutiqueDetails.this, R.layout.side_bar_item, categoryList);
                sideBar.setAdapter(categoryAdapter);
                sideBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent categoryIntent=new Intent(BoutiqueDetails.this,GridOfProducts.class);
                        categoryIntent.putExtra("CategoryCode",categoryCode.get(categoryList.get(position)));
                        categoryIntent.putExtra("Category",categoryList.get(position).replace("\uD83D\uDC49\t",""));
                        Toast.makeText(BoutiqueDetails.this,categoryList.get(position)+"-"+categoryCode.get(categoryList.get(position)),Toast.LENGTH_SHORT).show();
                        startActivity(categoryIntent);
                    }
                });
                TextView myFav=(TextView)findViewById(R.id.favorites);
                myFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent categoryIntent=new Intent(BoutiqueDetails.this,GridOfProducts.class);
                        categoryIntent.putExtra("CategoryCode","myfav");
                        categoryIntent.putExtra("Category",R.string.my_favorites);
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
}
