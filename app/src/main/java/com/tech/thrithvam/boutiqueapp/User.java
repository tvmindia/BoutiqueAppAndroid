package com.tech.thrithvam.boutiqueapp;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class User extends AppCompatActivity {
    Animation slideEntry1;
    Animation slideEntry2;
    Animation slideExit1;
    Animation slideExit2;
    ScrollView login,signup,userDetails;
    TextView dobPicker, anniversaryPicker;
    Calendar dob, anniversary;
    TextView points;
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

        //------------Sign up------------------------------------------
        signup=(ScrollView)findViewById(R.id.SignUp);
        dob=Calendar.getInstance();
        anniversary =Calendar.getInstance();
        dobPicker=(TextView)findViewById(R.id.dob_signup);
        anniversaryPicker =(TextView)findViewById(R.id.anniversary_signup);
        dobPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today=Calendar.getInstance();
                DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dob.set(Calendar.YEAR,year);
                        dob.set(Calendar.MONTH,monthOfYear);
                        dob.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

                        dobPicker.setText(formatted.format(dob.getTime()));
                    }
                };
                new DatePickerDialog(User.this, dateSetListener, today.get(Calendar.YEAR),today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        anniversaryPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today=Calendar.getInstance();
                DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        anniversary.set(Calendar.YEAR,year);
                        anniversary.set(Calendar.MONTH,monthOfYear);
                        anniversary.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                        anniversaryPicker.setText(formatted.format(anniversary.getTime()));
                    }
                };
                new DatePickerDialog(User.this, dateSetListener, today.get(Calendar.YEAR),today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
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

    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_exit1,R.anim.slide_exit2);
    }
    public void sign_up(View view){
        signup.setVisibility(View.VISIBLE);
        login.startAnimation(slideExit2);
        signup.startAnimation(slideExit1);
    }
    public void sign_up_cancel(View view){
        login.startAnimation(slideEntry1);
        signup.startAnimation(slideEntry2);
        signup.setVisibility(View.GONE);
    }
    public void login(View view){
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
    public void logout(View view){
        userDetails.startAnimation(slideExit2);
        login.startAnimation(slideExit1);
        userDetails.setVisibility(View.GONE);
    }
}
