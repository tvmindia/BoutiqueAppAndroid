package com.tech.thrithvam.boutiqueapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

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
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Chat extends AppCompatActivity {
    Constants constants=new Constants();
    DatabaseHandler db=new DatabaseHandler(this);
    ListView sideBar;
    ArrayList<String> categoryList;
    Dictionary<String,String> categoryCode=new Hashtable<>();
    ArrayAdapter categoryAdapter;

    EditText inputMessage;
    ImageView send;
    String productID="",lastProductIdSeen="";
    Bundle extras;

    ListView msgList;
    Handler handler = new Handler();
    int loadedMsgCount=0;
    TextView loadingTxt;
    CardView productDetail;
    AsyncTask getCategories,productDetailsFroChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        productDetail=(CardView)findViewById(R.id.productDetail);
        extras=getIntent().getExtras();
        getSupportActionBar().setElevation(0);
        msgList= (ListView) findViewById(R.id.messagesListView);
        if(db.GetUserDetail("UserID")==null){
            Toast.makeText(Chat.this,R.string.please_login,Toast.LENGTH_LONG).show();
            Intent intentUser = new Intent(Chat.this, User.class);
            startActivity(intentUser);
            overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
        }


        sideBar = (ListView) findViewById(R.id.drawer);
        if (isOnline()) {
            getCategories= new GetCategories().execute();
          //  new GetMessages().execute();

        } else {
            Toast.makeText(Chat.this, R.string.network_off_alert, Toast.LENGTH_LONG).show();
            finish();
        }
        inputMessage=(EditText)findViewById(R.id.msgInput);
        send=(ImageView) findViewById(R.id.submitMsg);
        inputMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputMessage.setLines(3);
            }
        });

        //Messages Loading------------------

        msgList.setAdapter(null);


        loadingTxt=(TextView)findViewById(R.id.loadingText);
        loadingTxt.setText(R.string.no_items);


        loadMessages();

        //Product Details----------------------
        if(getIntent().hasExtra("productID")){
            productID=extras.getString("productID");
            lastProductIdSeen=productID;
        }
        productDetailsFroChat=new ProductDetailsForChat().execute();


    }
    //----------Loading messages-----------------
    public void loadMessages()
    {
        final ArrayList<String[]> msgData=db.GetMsgs();
        CustomAdapter adapter=new CustomAdapter(Chat.this, db.GetMsgs(),"chat");
        if(adapter.getCount()>loadedMsgCount)
        {
        msgList.setAdapter(adapter);
        msgList.setOnItemClickListener(null);

            msgList.setSelection(msgList.getCount() - 1);
            loadedMsgCount=msgList.getCount();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                msgList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if(msgList.getLastVisiblePosition()==msgData.size()-1){
                            lastProductIdSeen=productID;
                            productDetailsFroChat=new ProductDetailsForChat().execute();
                        }
                        else if(!msgData.get(msgList.getLastVisiblePosition())[3].equals(lastProductIdSeen)
                                &&
                                    !msgData.get(msgList.getLastVisiblePosition())[3].equals("null")){
                                lastProductIdSeen=msgData.get(msgList.getLastVisiblePosition())[3];
                                productDetailsFroChat=new ProductDetailsForChat().execute();
                        }
                    }
                });
            }
            else {
                msgList.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                        if(msgList.getLastVisiblePosition()==msgData.size()-1){
                            lastProductIdSeen=productID;
                            productDetailsFroChat=new ProductDetailsForChat().execute();
                        }
                        else if(!msgData.get(msgList.getLastVisiblePosition())[3].equals(lastProductIdSeen)
                                &&
                                !msgData.get(msgList.getLastVisiblePosition())[3].equals("null")){
                            lastProductIdSeen=msgData.get(msgList.getLastVisiblePosition())[3];
                            productDetailsFroChat=new ProductDetailsForChat().execute();
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    }
                });
            }

        }
        if(loadedMsgCount==0){
           // msgList.setVisibility(View.INVISIBLE);
           // productDetail.setVisibility(View.INVISIBLE);
            loadingTxt.setVisibility(View.VISIBLE);
        }
        else {
            msgList.setVisibility(View.VISIBLE);
            loadingTxt.setVisibility(View.INVISIBLE);
        }
        handler.postDelayed(new Runnable() {
            public void run() {
               loadMessages();
            }
        },1000);
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
    @Override
    public void onBackPressed() {
        finish();
        handler.removeCallbacksAndMessages(null);
        getCategories.cancel(true);
        productDetailsFroChat.cancel(true);
        overridePendingTransition(R.anim.slide_exit1,R.anim.slide_exit2);
    }
    public void sendMsg(View view){
            if(db.GetUserDetail("UserID")==null){
                Toast.makeText(Chat.this,R.string.please_login,Toast.LENGTH_LONG).show();
                Intent intentUser = new Intent(Chat.this, User.class);
                startActivity(intentUser);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
            }
            if(!inputMessage.getText().toString().trim().equals(""))
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                send.setEnabled(false);
                new SendMessage().execute();
            }
    }
    //-----------------------Async tasks----------------------------
    public class GetCategories extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                        strJson="{\"JSON\":" + strJson.replace("\\\"","\"").replace("\\\\","\\") + "}";
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
            if(!pass) {
                new AlertDialog.Builder(Chat.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false).show();
            }
            else {
                AVLoadingIndicatorView avLoadingIndicatorView=(AVLoadingIndicatorView)findViewById(R.id.catItemsLoading);
                avLoadingIndicatorView.setVisibility(View.GONE);
                //Links other than category
                categoryList.add("");
                categoryList.add(getResources().getString(R.string.trending));
                categoryCode.put(getResources().getString(R.string.trending),"trends");
                categoryList.add(getResources().getString(R.string.my_favorites));
                categoryCode.put(getResources().getString(R.string.my_favorites),"myfav");
                categoryList.add(getResources().getString(R.string.my_orders_sidebar));

                categoryAdapter = new ArrayAdapter<>(Chat.this, R.layout.side_bar_item, categoryList);
                sideBar.setAdapter(categoryAdapter);
                sideBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(!categoryList.get(position).equals("")){
                            if(categoryList.get(position).equals(getResources().getString(R.string.my_orders_sidebar))){
                                Intent orderIntent=new Intent(Chat.this,OrderStatus.class);
                                startActivity(orderIntent);
                                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                            }
                            else {
                                Intent categoryIntent=new Intent(Chat.this,GridOfProducts.class);
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
    public class SendMessage extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        String sendMsg;
        ProgressDialog pDialog=new ProgressDialog(Chat.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage(getResources().getString(R.string.wait));
            pDialog.setCancelable(false);
            pDialog.show();
            sendMsg=inputMessage.getText().toString().trim();
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/InsertChat";
            HttpURLConnection c = null;
            try {
                postData = "{\"productID\":\"" + productID + "\",\"userID\":\"" + (db.GetUserDetail("UserID")==null?"":db.GetUserDetail("UserID")) + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\",\"replyPersonID\":\"" + "" + "\",\"direction\":\"" + "out"  + "\",\"message\":\"" + sendMsg + "\"}";
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
                        strJson="{\"JSON\":" + strJson.replace("\\\"","\"").replace("\\\\","\\") + "}";
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
                new AlertDialog.Builder(Chat.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setCancelable(false).show();
            }
            else {
                inputMessage.setText("");
            }
            send.setEnabled(true);
        }
    }
    public class ProductDetailsForChat extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        String productName,priceString,productImage;
        Integer productNoInt;
        AVLoadingIndicatorView avLoadingIndicatorView;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            avLoadingIndicatorView=(AVLoadingIndicatorView)findViewById(R.id.prodDetLoading);
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/GetProductDetailsOnChat";
            HttpURLConnection c = null;
            try {
                postData = "{\"productID\":\"" + lastProductIdSeen + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\"}";
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
                        strJson="{\"JSON\":" + strJson.replace("\\\"","\"").replace("\\\\","\\") + "}";
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
                    priceString=String.format(Locale.US,"%.2f", jsonObject.optDouble("Price"));
                    productNoInt=jsonObject.optInt("ProductNo");
                    productImage=getResources().getString(R.string.url) + jsonObject.optString("Image").substring((jsonObject.optString("Image")).indexOf("Media"));
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
                /*new AlertDialog.Builder(Chat.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setCancelable(false).show();*/
                productDetail.setVisibility(View.GONE);

            }
            else {
                TextView pName=(TextView)findViewById(R.id.productName);
                TextView pNo=(TextView)findViewById(R.id.productNo);
                TextView pPrice=(TextView)findViewById(R.id.productPrice);
                ImageView pImage=(ImageView)findViewById(R.id.productImg);

                pName.setText(productName);
                pNo.setText(getResources().getString(R.string.product_no, productNoInt));
                pPrice.setText(getResources().getString(R.string.rs, priceString));
                Picasso.with(Chat.this).load(productImage).into(pImage);
                productDetail.setVisibility(View.VISIBLE);
                productDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(Chat.this,ItemDetails.class);
                        intent.putExtra("ProductID",lastProductIdSeen);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                    }
                });
            }
            avLoadingIndicatorView.setVisibility(View.GONE);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
