package com.tech.thrithvam.boutiqueapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderStatus extends AppCompatActivity {
    DatabaseHandler db=new DatabaseHandler(OrderStatus.this);
    Constants constants=new Constants();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        getSupportActionBar().setElevation(0);
        //---------threading----------------
        if (isOnline()){
            new Orders().execute();
        }
        else {
            Toast.makeText(OrderStatus.this,R.string.network_off_alert,Toast.LENGTH_LONG).show();
        }

    }
    public class Orders extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(OrderStatus.this);
        ArrayList<String[]> orders=new ArrayList<>();
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
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/Orders";
            HttpURLConnection c = null;
            try {
                postData = "{\"userID\":\"" + db.GetUserDetail("UserID") + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\"}";
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
                    String[] data=new String[10];
                    data[0]=jsonObject.optString("OrderDescription").replace("\\u0026", "&");
                    data[1]=jsonObject.optString("OrderNo");
                    data[2]=String.format(Locale.US,"%.2f", jsonObject.optDouble("TotalOrderAmount"));
                    data[3]=jsonObject.optString("OrderDate").replace("\\/Date(", "").replace(")\\/", "");
                    data[4]=jsonObject.optString("ForecastDeliveryDate").replace("\\/Date(", "").replace(")\\/", "");
                    data[5]=jsonObject.optString("OrderReadyDate").replace("\\/Date(", "").replace(")\\/", "");
                    data[6]=jsonObject.optString("ActualDeliveryDate").replace("\\/Date(", "").replace(")\\/", "");
                    data[7]=jsonObject.optString("CreatedDate").replace("\\/Date(", "").replace(")\\/", "");
                    data[8]=jsonObject.optString("UpdatedDate").replace("\\/Date(", "").replace(")\\/", "");
                    orders.add(data);
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
                new AlertDialog.Builder(OrderStatus.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(R.string.no_items)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false).show();
            }
            else {
                CustomAdapter adapter=new CustomAdapter(OrderStatus.this, orders,"orders");
                ListView orderList=(ListView) findViewById(R.id.orderList);
                orderList.setAdapter(adapter);
            }
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
