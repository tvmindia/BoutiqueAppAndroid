package com.tech.thrithvam.boutiqueapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class OwnerAndDesigner extends AppCompatActivity {
TextView profile;
    TextView phone;
    ImageView phoneSymbol;

    Spinner spinner;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_and_designer);
        getSupportActionBar().setElevation(0);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner=(Spinner)findViewById(R.id.name);

        for (int i=0;i<4;i++)
        {
            arrayList.add("Name "+ i);
        }

        spinner.setAdapter(adapter);

    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_exit1,R.anim.slide_exit2);
    }
}
