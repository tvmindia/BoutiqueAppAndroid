package com.tech.thrithvam.boutiqueapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Notification extends Service {
    DatabaseHandler db=new DatabaseHandler(this);
    Constants constants=new Constants();
    int TIME_INTERVAL_IN_MINUTE=1;
    public Notification() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
  //      Toast.makeText(this, "The new Service was Created", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    //        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        if(isOnline()){
                new GetNotifications().execute();
        }
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
      //  Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * TIME_INTERVAL_IN_MINUTE),
                PendingIntent.getService(this, 0, new Intent(this, Notification.class), 0)
        );
    }
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    private class GetNotifications extends AsyncTask<Void, Void, Void> {
        int status;
        StringBuilder sb;
        String strJson, postData; JSONArray jsonArray4Notifications;
        String Message;
        Boolean pass=false;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ArrayList<String> titles=new ArrayList<>();
        ArrayList<String> messages=new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {           //loading JSONs from server in background
            String url=getResources().getString(R.string.url)+"WebServices/WebService.asmx/Notifications";
            HttpURLConnection c = null;
            try {
                postData = "{\"notificationIDs\":\"" + db.getNotificationIDs() + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\",\"userID\":\"" + (db.GetUserDetail("UserID")==null?"":db.GetUserDetail("UserID")) + "\"}";
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-type", "application/json");
                c.setRequestProperty("Content-length", Integer.toString(postData.length()));
                c.setDoInput(true);
                c.setDoOutput(true);
                c.setUseCaches(false);
                c.setConnectTimeout(5000);
                c.setReadTimeout(5000);
                DataOutputStream wr = new DataOutputStream(c.getOutputStream ());
                wr.writeBytes(postData);
                wr.flush();
                wr.close();
                // c.connect();
                status = c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();
                        int a=sb.indexOf("[");
                        int b=sb.lastIndexOf("]");
                        strJson=sb.substring(a,b+1);
                        strJson="{\"JSON\":" + strJson.replace("\\\"", "\"")+ "}";
                }
            } catch (Exception ex) {
                stopSelf();
            }  finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        stopSelf();
                    }
                }
            }
            if(strJson!=null){
                try {
                    JSONObject jsonRootObject = new JSONObject(strJson);
                    jsonArray4Notifications = jsonRootObject.optJSONArray("JSON");
                    for (int i = 0; i < jsonArray4Notifications.length(); i++) {
                        JSONObject jsonObject = jsonArray4Notifications.getJSONObject(i);
                        pass=jsonObject.optBoolean("Flag",true);
                        Message=jsonObject.optString("Message","");
                        if(!jsonObject.optString("NotificationID").equals("")){     //to avoiding inserting null values when NotificationID is absent
                            db.insertNotificationIDs(jsonObject.optString("NotificationID"),jsonObject.optString("EndDate").replace("\\/Date(", "").replace(")\\/", ""));
                        }
                        titles.add(jsonObject.optString("Title"));
                        messages.add(jsonObject.optString("Description"));
                    }

                } catch (Exception e) {
                    stopSelf();
                }}
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!pass) {
    //            Toast.makeText(Notification.this,"Error in Boutique app background service: "+Message, Toast.LENGTH_LONG).show();
            }
            else {
                for (int i=0;i<titles.size();i++) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Notification.this);
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    mBuilder.setContentTitle(titles.get(i));
                    mBuilder.setContentText(messages.get(i));
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    mBuilder.setSound(alarmSound);
                    mNotificationManager.notify((int) Math.ceil(Math.random() * 1000), mBuilder.build());//random notification id on phone
                }
            }
        }
    }
}
