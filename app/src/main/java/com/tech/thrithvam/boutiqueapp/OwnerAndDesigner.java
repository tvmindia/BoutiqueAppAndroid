package com.tech.thrithvam.boutiqueapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

public class OwnerAndDesigner extends AppCompatActivity {
    Constants constants=new Constants();
    TextView profile;
    TextView phone;
    ImageView phoneSymbol;
    Bundle extras;
    Spinner spinner;
    ArrayList<String> arrayListName;
    ArrayList<String> arrayListProfile;
    ArrayList<String> arrayListPhone;
    ArrayList<String> designerID;
    ArrayList<String> imageURL;
    ArrayAdapter<String> adapter;
    ImageView dp;
    ListView sideBar;
    ArrayList<String> categoryList;
    Dictionary<String,String> categoryCode=new Hashtable<>();
    ArrayAdapter categoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_and_designer);
        getSupportActionBar().setElevation(0);
        sideBar=(ListView)findViewById(R.id.left_drawer);
        extras= getIntent().getExtras();
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null) {
            if(extras.getString("ownerORdesigner").equals("designer")){
                ab.setTitle("Designer");
            }
            else {
                ab.setTitle("Owner");
            }
        }
        dp=(ImageView)findViewById(R.id.dp);
        arrayListName = new ArrayList<>();
        arrayListProfile=new ArrayList<>();
        arrayListPhone=new ArrayList<>();
        designerID =new ArrayList<>();
        imageURL =new ArrayList<>();
        profile=(TextView)findViewById(R.id.profile);
        phone=(TextView)findViewById(R.id.phone);
        phoneSymbol=(ImageView)findViewById(R.id.callSymbol);
        if (isOnline()){
            new GetDetails().execute();
            new GetCategories().execute();
        }
        else {
            Toast.makeText(OwnerAndDesigner.this,R.string.network_off_alert,Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_exit1,R.anim.slide_exit2);
    }
    //------------------------------Async Tasks-----------------------------
    public class GetDetails extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(OwnerAndDesigner.this);
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
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/OwnersAndDesigners";
            HttpURLConnection c = null;
            try {
                postData = "{\"ownerORdesigner\":\"" + extras.getString("ownerORdesigner") + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\"}";
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
                    arrayListName.add(jsonObject.optString("Name"));
                    arrayListProfile.add(jsonObject.optString("Profile"));
                    arrayListPhone.add(jsonObject.optString("Mobile"));
                    designerID.add(jsonObject.optString("DesignerID"));
                    imageURL.add(jsonObject.optString("Image","null"));
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
                new AlertDialog.Builder(OwnerAndDesigner.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false).show();
            }
            else {
                adapter = new ArrayAdapter<>(OwnerAndDesigner.this, R.layout.spinner_item, arrayListName);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner=(Spinner)findViewById(R.id.name);
                spinner.setAdapter(adapter);
                //--------Setting spinner-------------------
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        profile.setText(arrayListProfile.get(spinner.getSelectedItemPosition()));
                        if(!imageURL.get(position).equals("null"))
                        {
                            Picasso.with(OwnerAndDesigner.this)
                                    .load(getString(R.string.url)+imageURL.get(position).substring(imageURL.get(position).indexOf("Media")))
                                    .into(dp)
                            ;
                        }
                        else {
                            Picasso.with(OwnerAndDesigner.this)
                                    .load(R.drawable.dp)
                                    .into(dp)
                            ;
                        }
                        final String phoneString=arrayListPhone.get(spinner.getSelectedItemPosition());
                        if(!phoneString.equals("null")){
                        phone.setText(phoneString);
                        phone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri number = Uri.parse("tel:" + phoneString);
                                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                                startActivity(callIntent);
                                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                            }
                        });
                        phoneSymbol.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri number = Uri.parse("tel:" + phoneString);
                                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                                startActivity(callIntent);
                                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                            }
                        });}
                        else {
                            phone.setVisibility(View.GONE);
                            phoneSymbol.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                if("designer".equals(extras.getString("ownerORdesigner")))   //to show specific designer details when comes from product details screen
                    {
                            spinner.setSelection(designerID.indexOf(extras.getString("designerID")));
                    }
                else {
                    spinner.performClick();                 //to allow user to see the list of owners as a popup- to avoid priority in owners
                }

            }
        }
    }
    public class GetCategories extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(OwnerAndDesigner.this);
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
                new AlertDialog.Builder(OwnerAndDesigner.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false).show();
            }
            else {
                categoryAdapter = new ArrayAdapter<>(OwnerAndDesigner.this, R.layout.side_bar_item, categoryList);
                sideBar.setAdapter(categoryAdapter);
                sideBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent categoryIntent=new Intent(OwnerAndDesigner.this,GridOfProducts.class);
                        categoryIntent.putExtra("CategoryCode",categoryCode.get(categoryList.get(position)));
                        categoryIntent.putExtra("Category",categoryList.get(position).replace("\uD83D\uDC49\t",""));
                        Toast.makeText(OwnerAndDesigner.this,categoryList.get(position)+"-"+categoryCode.get(categoryList.get(position)),Toast.LENGTH_SHORT).show();
                        startActivity(categoryIntent);
                    }
                });
                TextView myFav=(TextView)findViewById(R.id.favorites);
                myFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent categoryIntent=new Intent(OwnerAndDesigner.this,GridOfProducts.class);
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
