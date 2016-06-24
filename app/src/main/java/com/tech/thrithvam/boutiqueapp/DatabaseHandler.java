package com.tech.thrithvam.boutiqueapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "BoutiqueApp.db";
    private SQLiteDatabase db;
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
    // IMPORTANT: if you are changing anything in the below function onCreate(), DO DELETE THE DATABASE file in
    // the emulator or uninstall the application in the phone, to run the application
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS UserAccount (UserID TEXT);";
        db.execSQL(CREATE_USER_ACCOUNTS_TABLE);
        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE IF NOT EXISTS Notifications (NotificationIDs TEXT, ExpiryDate DATE);";
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
        String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS Chat (MsgIDs TEXT PRIMARY KEY, Msg TEXT, Direction TEXT, MsgTime DATETIME, ProductID TEXT);";
        db.execSQL(CREATE_CHAT_TABLE);
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME );
        // Create tables again
        onCreate(db);
    }
    //--------------------------User Accounts-----------------------------
    public void UserLogin(String UserID)
    {
        db=this.getWritableDatabase();
        db.execSQL("INSERT INTO UserAccount (UserID) VALUES ('"+UserID+"');");
        db.close();
    }
    public void UserLogout()
    {
        db=this.getWritableDatabase();
        db.execSQL("DELETE FROM UserAccount;");
        db.close();
    }
    public String GetUserDetail(String detail)
    {db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM UserAccount;",null);
        if (cursor.getCount()>0)
        {cursor.moveToFirst();
            String result=cursor.getString(cursor.getColumnIndex(detail));
            cursor.close();
            return result;
        }
        else return null;
    }
    //------------------------Notifications table------------------------------
    public void insertNotificationIDs(String notIds, String date)
    {
        db=this.getWritableDatabase();
        db.execSQL("INSERT INTO Notifications (NotificationIDs,ExpiryDate) VALUES ('"+notIds+"','"+date+"');");
        db.close();
    }
    public String getNotificationIDs()
    {
        db=this.getReadableDatabase();
        String nIDs="";
        Cursor cursor = db.rawQuery("SELECT (NotificationIDs) FROM Notifications;",null);
        if (cursor.getCount()>0)
        {cursor.moveToFirst();
            nIDs=cursor.getString(cursor.getColumnIndex("NotificationIDs"));
            do {
                nIDs=nIDs+","+cursor.getString(cursor.getColumnIndex("NotificationIDs"));
            }while (cursor.moveToNext());
            cursor.close();
            return nIDs;
        }
        else return "";
    }
    public void flushNotifications()
    {
        db=this.getWritableDatabase();
        long time= System.currentTimeMillis();
        db.execSQL("DELETE FROM Notifications WHERE ExpiryDate<"+time+";");
        db.close();
    }
    //------------------------------Chat table---------------------------------------
    public void insertMessage(String MsgIDs,String Msg,String Direction,String MsgTime,String ProductID)
    {
        db=this.getWritableDatabase();
        try {
            db.execSQL("INSERT INTO Chat (MsgIDs,Msg,Direction,MsgTime,ProductID) VALUES ('"+MsgIDs+"','"+Msg+"','"+Direction+"','"+MsgTime+"','"+ProductID+"');");
        }
        catch (Exception ex){
        }
        db.close();
    }
    public ArrayList<String[]> GetMsgs()
    {db=this.getReadableDatabase();
        ArrayList<String[]> msgs=new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Chat ORDER BY MsgTime ASC;",null);
        if (cursor.getCount()>0)
        {cursor.moveToFirst();
            do {
                String[] data = new String[2];
                data[0] = cursor.getString(cursor.getColumnIndex("Msg"));
                data[1] = cursor.getString(cursor.getColumnIndex("MsgTime"));
                msgs.add(data);
            }while (cursor.moveToNext());

            cursor.close();
            return msgs;
        }
        else return null;
    }
}
