package com.tech.thrithvam.boutiqueapp;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class User extends AppCompatActivity {
    Animation slideEntry1;
    Animation slideEntry2;
    Animation slideExit1;
    Animation slideExit2;
    ScrollView login,signup,userDetails;
    TextView dobPicker,anniversoryPicker;
    Calendar dob,anniversory;
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
        anniversory=Calendar.getInstance();
        dobPicker=(TextView)findViewById(R.id.dob_signup);
        anniversoryPicker=(TextView)findViewById(R.id.anniversary_signup);
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
        anniversoryPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today=Calendar.getInstance();
                DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        anniversory.set(Calendar.YEAR,year);
                        anniversory.set(Calendar.MONTH,monthOfYear);
                        anniversory.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                        anniversoryPicker.setText(formatted.format(anniversory.getTime()));
                    }
                };
                new DatePickerDialog(User.this, dateSetListener, today.get(Calendar.YEAR),today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
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
}
