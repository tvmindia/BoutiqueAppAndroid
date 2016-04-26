package com.tech.thrithvam.boutiqueapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User extends AppCompatActivity {
    DatabaseHandler db=new DatabaseHandler(User.this);
    Constants constants=new Constants();
    Animation slideEntry1;
    Animation slideEntry2;
    Animation slideExit1;
    Animation slideExit2;
    ScrollView login, signUp, userDetails;
    EditText dobPicker, anniversaryPicker;
    Calendar dob, anniversary;
    TextView points;
    EditText mobileLogin;
    EditText passwordLogin;
    EditText nameSignUp;
    EditText mobileNoSignUp;
    EditText emailSignUp;
    String genderString;
    int OTP=0;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setElevation(0);

        slideEntry1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_entry1);
        slideEntry2= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_entry2);
        slideExit1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_exit1);
        slideExit2= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_exit2);

        //------------Login---------------------------------------------
        login=(ScrollView)findViewById(R.id.Login);
        //validation-------
        mobileLogin=(EditText)findViewById(R.id.mobLogin);
        mobileLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {}
                else {
                    mobileLogin.setText(mobileLogin.getText().toString().trim());
                    if( (mobileLogin.length()<constants.MobileNumberMin) || (mobileLogin.length()>constants.MobileNumberMax) || !(mobileLogin.getText().toString().matches(constants.MobileNumberRegularExpression))) {
                        mobileLogin.setError(getResources().getString(R.string.mob_no_error));
                    }
                }
            }
        });
        passwordLogin=(EditText)findViewById(R.id.passwordLogin);
        passwordLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {}
                else {
                    passwordLogin.setText(passwordLogin.getText().toString().trim());
                    if( passwordLogin.getText().toString().equals("")) {
                        passwordLogin.setError(getResources().getString(R.string.no_pass_error));
                    }
                }
            }
        });

        //------------Sign up------------------------------------------
        signUp =(ScrollView)findViewById(R.id.SignUp);
        nameSignUp=(EditText)findViewById(R.id.user_name_signup);
        mobileNoSignUp=(EditText)findViewById(R.id.mob_no_signup);
        emailSignUp=(EditText)findViewById(R.id.email_signup);
        dob=Calendar.getInstance();
        anniversary =Calendar.getInstance();
        dobPicker=(EditText)findViewById(R.id.dob_signup);
       // dobPicker.setEnabled(false);
        anniversaryPicker =(EditText)findViewById(R.id.anniversary_signup);
       // anniversaryPicker.setEnabled(false);
        dobPicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Calendar today = Calendar.getInstance();
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            dob.set(Calendar.YEAR, year);
                            dob.set(Calendar.MONTH, monthOfYear);
                            dob.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

                            dobPicker.setText(formatted.format(dob.getTime()));
                        }
                    };
                    new DatePickerDialog(User.this, dateSetListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
        anniversaryPicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Calendar today = Calendar.getInstance();
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            anniversary.set(Calendar.YEAR, year);
                            anniversary.set(Calendar.MONTH, monthOfYear);
                            anniversary.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                            anniversaryPicker.setText(formatted.format(anniversary.getTime()));
                        }
                    };
                    new DatePickerDialog(User.this, dateSetListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
        //validation------------------
        nameSignUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    nameSignUp.setError(null);
                }
                else {
                    nameSignUp.setText(nameSignUp.getText().toString().trim());
                    if( !(nameSignUp.getText().toString().matches(constants.UserNameRegularExpression)) || nameSignUp.length()<constants.UserNameMin)
                    {
                        nameSignUp.setError(getResources().getString(R.string.name_error));
                    }
                }
            }
        });
        mobileNoSignUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {}
                else {
                    mobileNoSignUp.setText(mobileNoSignUp.getText().toString().trim());
                    if( mobileNoSignUp.length()<constants.MobileNumberMin || mobileNoSignUp.length()>constants.MobileNumberMax || !(mobileNoSignUp.getText().toString().matches(constants.MobileNumberRegularExpression))) {
                        mobileNoSignUp.setError(getResources().getString(R.string.mob_no_error));
                    }
                }
            }
        });
        emailSignUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    emailSignUp.setError(null);
                }
                else {
                    emailSignUp.setText(emailSignUp.getText().toString().trim());
                    if( !android.util.Patterns.EMAIL_ADDRESS.matcher(emailSignUp.getText().toString()).matches()) {
                        emailSignUp.setError(getResources().getString(R.string.email_error));
                    }
                }
            }
        });
        //-----------------------User details-----------------------
        userDetails=(ScrollView)findViewById(R.id.UserDetails);
        Typeface fontType1 = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        Typeface fontType2 = Typeface.createFromAsset(getAssets(), "fonts/handwriting.ttf");

        TextView greeting=(TextView)findViewById(R.id.greeting);
        greeting.setTypeface(fontType2);
        TextView user_name=(TextView)findViewById(R.id.user_name);
        user_name.setTypeface(fontType2);
        TextView textView1=(TextView)findViewById(R.id.textView1);
        textView1.setTypeface(fontType1);
        TextView textView2=(TextView)findViewById(R.id.textView2);
        textView2.setTypeface(fontType1);
        TextView textView10=(TextView)findViewById(R.id.textView10);
        textView10.setTypeface(fontType1);
        TextView loyaltyCardNo=(TextView)findViewById(R.id.loyalty_card_number);
        loyaltyCardNo.setTypeface(fontType1);
        points=(TextView)findViewById(R.id.points);

        //greeting--------
        int timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 12){
            greeting.setText("Good Morning");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            greeting.setText("Good Afternoon");
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            greeting.setText("Good Evening");
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            greeting.setText("Good Night");
        }

        //Already logged in--------------------

        if(db.GetUserDetail("UserID")!=""){
            userDetails.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            //points animation---------
            int count=150;
            ValueAnimator animator = new ValueAnimator();
            animator.setObjectValues(0, count);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    points.setText(String.valueOf(animation.getAnimatedValue()));
                }
            });
            animator.setEvaluator(new TypeEvaluator<Integer>() {
                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                    return Math.round(startValue + (endValue - startValue) * fraction);
                }
            });
            animator.setDuration(2000);
            animator.start();
        }

    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_exit1,R.anim.slide_exit2);
    }
    public void sign_up(View view){
        mobileNoSignUp.setText(mobileLogin.getText().toString()); //getting mobile number from login page if user already entered
        signUp.setVisibility(View.VISIBLE);
        login.startAnimation(slideExit2);
        signUp.startAnimation(slideExit1);
        login.setVisibility(View.GONE);
    }
    public void sign_up_cancel(View view){
        login.setVisibility(View.VISIBLE);
        login.startAnimation(slideEntry1);
        signUp.startAnimation(slideEntry2);
        signUp.setVisibility(View.GONE);
    }
    public void login(View view){
        //Validation-------------
        mobileLogin.requestFocus();
        passwordLogin.requestFocus();
        mobileLogin.clearFocus();
        passwordLogin.clearFocus();
        if(mobileLogin.getError()==null &&passwordLogin.getError()==null){
            //Proceeding------------------
            userDetails.setVisibility(View.VISIBLE);
            login.startAnimation(slideEntry2);
            userDetails.startAnimation(slideEntry1);
            //points animation---------
            int count=150;
            ValueAnimator animator = new ValueAnimator();
            animator.setObjectValues(0, count);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    points.setText(String.valueOf(animation.getAnimatedValue()));
                }
            });
            animator.setEvaluator(new TypeEvaluator<Integer>() {
                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                    return Math.round(startValue + (endValue - startValue) * fraction);
                }
            });
            animator.setDuration(2000);
            animator.start();
        }
    }
    public void sign_up_filled(View view){
        nameSignUp.requestFocus();
        mobileNoSignUp.requestFocus();
        emailSignUp.requestFocus();
        nameSignUp.clearFocus();
        emailSignUp.clearFocus();
        mobileNoSignUp.clearFocus();
        RadioGroup rButtons=(RadioGroup)findViewById(R.id.gender);
        switch (rButtons.getCheckedRadioButtonId())
        {
            case R.id.male:
                genderString="MALE";
                break;
            case R.id.female:
                genderString="FEMALE";
                break;
            default:Toast.makeText(User.this, R.string.gender_error_msg,Toast.LENGTH_LONG).show();
                return;
        }
        if(nameSignUp.getError()==null && mobileNoSignUp.getError()==null && emailSignUp.getError()==null){
            if(isOnline()){new UserRegistration().execute();}
            else {
                Toast.makeText(User.this,R.string.network_off_alert,Toast.LENGTH_LONG).show();
            }
        }
    }
    public void logout(View view){
        mobileLogin.setText("");
        passwordLogin.setText("");
        userDetails.startAnimation(slideExit2);
        login.startAnimation(slideExit1);
        userDetails.setVisibility(View.GONE);
        login.setVisibility(View.VISIBLE);
        db.UserLogout();
    }

    //-------------------- Async tasks---------------------------------
    public class UserRegistration extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(User.this);
        String nameString,mobileString,emailString,dobString,anniversaryString;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage(getResources().getString(R.string.wait));
            pDialog.setCancelable(false);
            pDialog.show();
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
            nameString=nameSignUp.getText().toString();
            mobileString=mobileNoSignUp.getText().toString();
            emailString=emailSignUp.getText().toString();
            SimpleDateFormat formatted = new SimpleDateFormat("yyyy MM dd", Locale.US);
            dobString = formatted.format(dob.getTime());
            anniversaryString=formatted.format(anniversary.getTime());
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/UserRegistration";
            HttpURLConnection c = null;
            try {
                postData = "{\"name\":\"" + nameString + "\",\"mobile\":\"" + mobileString + "\",\"email\":\"" + emailString + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\",\"dob\":\"" + dobString + "\",\"anniversary\":\"" + anniversaryString + "\",\"gender\":\"" + genderString+ "\"}";
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
                    pass=jsonObject.optBoolean("Flag");
                    OTP=jsonObject.optInt("OTP");
                    userID=jsonObject.optString("UserID");
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
                new AlertDialog.Builder(User.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setCancelable(false).show();
            }
            else {
                UserVerification();
            }
        }
    }
    public void UserVerification(){
        Toast.makeText(User.this,Integer.toString(OTP),Toast.LENGTH_LONG).show();
        AlertDialog.Builder alert = new AlertDialog.Builder(User.this);
        alert.setTitle(R.string.enter_otp);
        final EditText otp=new EditText(User.this);
        otp.setInputType(InputType.TYPE_CLASS_NUMBER);
        otp.setGravity(Gravity.CENTER_HORIZONTAL);
        alert.setView(otp);
        alert.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (otp.getText().toString().equals("")) {
                    UserVerification();
                }
                else if(Integer.parseInt(otp.getText().toString())==OTP){
                    Toast.makeText(User.this,"Matches",Toast.LENGTH_LONG).show();
                    new UserActivation().execute();
                }
                else {
                    Toast.makeText(User.this,"Not Matching",Toast.LENGTH_LONG).show();
                    UserVerification();
                }
            }
        });
        alert.setCancelable(false);
        alert.show();
    }
    public class UserActivation extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(User.this);
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
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/UserActivation";
            HttpURLConnection c = null;
            try {
                postData = "{\"userId\":\"" + userID + "\",\"boutiqueID\":\"" + constants.BoutiqueID + "\"}";
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
                    pass=jsonObject.optBoolean("Flag");
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
                new AlertDialog.Builder(User.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setCancelable(false).show();
            }
            else {
                Toast.makeText(User.this,msg, Toast.LENGTH_LONG).show();
                db.UserLogin(userID);
                Intent intentUser = new Intent(User.this, User.class);
                startActivity(intentUser);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
                db.GetUserDetail("UserID");
                finish();

            }
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
