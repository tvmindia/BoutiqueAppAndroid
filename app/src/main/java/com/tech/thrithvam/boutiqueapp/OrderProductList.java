package com.tech.thrithvam.boutiqueapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderProductList extends AppCompatActivity {
    DatabaseHandler db=new DatabaseHandler(OrderProductList.this);
    Constants constants=new Constants();
    Bundle extras=new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_product_list);
        extras=getIntent().getExtras();
        if(isOnline()){
            new OrderItems(extras.getString("orderID")).execute();
        }
        else {
            Toast.makeText(OrderProductList.this,R.string.network_off_alert,Toast.LENGTH_LONG).show();
            finish();
        }
        TextView orderDescription = (TextView) findViewById(R.id.description);
        TextView orderNo = (TextView) findViewById(R.id.orderNo);
        TextView amount = (TextView) findViewById(R.id.amount);
        TextView orderDate = (TextView) findViewById(R.id.orderDate);
        TextView expectedDeliveryDate = (TextView) findViewById(R.id.expectedDeliveryDate);
        TextView lastUpdatedDate = (TextView) findViewById(R.id.lastUpdatedDate);
        TextView orderStatus =(TextView) findViewById(R.id.orderStatus);
        TextView readyLabel=(TextView)findViewById(R.id.readyDateLabel);
        orderDescription.setText(extras.getString("orderDescription"));
        orderNo.setText(extras.getString("orderNo"));
        amount.setText(extras.getString("amount"));
        orderDate.setText(extras.getString("orderDate"));
        expectedDeliveryDate.setText(extras.getString("expectedDeliveryDate"));
        lastUpdatedDate.setText(extras.getString("lastUpdatedDate"));
        orderStatus.setText(extras.getString("orderStatus"));
        readyLabel.setText(extras.getString("readyLabel"));
        if(orderStatus.getText().toString().equals(getString(R.string.order_placed))){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                orderStatus.setTextColor(getColor(R.color.primary_text));
            }
            else {
                orderStatus.setTextColor(getResources().getColor(R.color.primary_text));
            }
        }
        else if(orderStatus.getText().toString().equals(getString(R.string.your_order_is_ready))){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                orderStatus.setTextColor(getColor(R.color.green));
            }
            else {
                orderStatus.setTextColor(getResources().getColor(R.color.green));
            }
        }
        else if(orderStatus.getText().toString().equals(getString(R.string.item_delivered))){
                orderStatus.setTextColor(Color.BLUE);
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public class OrderItems extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ArrayList<String[]> products=new ArrayList<>();
        String orderID;
        public OrderItems(String orderID){
            this.orderID=orderID;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/OrderItems";
            HttpURLConnection c = null;
            try {
                postData = "{\"orderID\":\"" + orderID + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\"}";
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
                    data[1]=jsonObject.optString("Product").replace("\\u0026", "&");
                    data[2]=jsonObject.optString("CustomerRemarks").replace("\\u0026", "&");
                    products.add(data);
                }
            } catch (Exception ex) {
                msg=ex.getMessage();
            }}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            TextView loadingTxt=(TextView)findViewById(R.id.loadingText);
            if(!pass) {
                    loadingTxt.setText(R.string.no_items);
                    loadingTxt.setVisibility(View.VISIBLE);
            }
            else {
                loadingTxt.setVisibility(View.GONE);
                CustomAdapter adapter=new CustomAdapter(OrderProductList.this, products,"orderItems");

                ListView productList= (ListView) findViewById(R.id.productList);
                productList.setAdapter(adapter);
                productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(OrderProductList.this,ItemDetails.class);
                        intent.putExtra("ProductID",products.get(position)[0]);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                    }});
            }
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_exit1,R.anim.slide_exit2);
    }
}
