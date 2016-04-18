package com.tech.thrithvam.boutiqueapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BoutiqueDetails extends AppCompatActivity {
ImageView boutiqueImg;
    TextView aboutUs;
    TextView caption;
    TextView year;
    TextView location;
    TextView address;
    TextView phone;
    ImageView phoneSymbol;
    TextView timing;
    TextView workingDays;
    TextView fbLink;
    TextView instagramLink;
    TextView owners;
    TextView designers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boutique_details);
        Typeface fontType1 = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        Typeface fontType2 = Typeface.createFromAsset(getAssets(), "fonts/handwriting.ttf");

        boutiqueImg=(ImageView)findViewById(R.id.boutiqueImg);
        aboutUs=(TextView)findViewById(R.id.aboutUs);
        caption=(TextView)findViewById(R.id.caption);
        year=(TextView)findViewById(R.id.year);
        location=(TextView)findViewById(R.id.location);
        address=(TextView)findViewById(R.id.address);
        phone=(TextView)findViewById(R.id.phone);
        phoneSymbol=(ImageView)findViewById(R.id.callSymbol);
        timing=(TextView)findViewById(R.id.timing);
        workingDays=(TextView)findViewById(R.id.workingDays);
        fbLink=(TextView)findViewById(R.id.fbLink);
        instagramLink=(TextView)findViewById(R.id.instagramLink);
        owners=(TextView)findViewById(R.id.owners);
        designers=(TextView)findViewById(R.id.designers);
        //---------------set boutique details------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boutiqueImg.setImageDrawable(getDrawable(R.drawable.boutique));
        }
        else {
            boutiqueImg.setImageDrawable(getResources().getDrawable(R.drawable.boutique));
        }
        aboutUs.setText("Our philosophy is pretty simple: we don’t take fashion or life too seriously. Whether you like to keep one step ahead of the trends, or if subtle style is more your thing, we’re sure we’ve got something you’ll love. And with up to 100 pieces hitting site every day and a new collection each week, we never stop - it’s 24/7 fashion at its best.");
        aboutUs.setTypeface(fontType1);
        caption.setText("Meet the styles");
        caption.setTypeface(fontType2);
        year.setText("Since: 1991");
        year.setTypeface(fontType1);
        location.setText("Chalakudi");
        location.setTypeface(fontType1);
        address.setText("Plaza Building \nOld Highway\nNear St.James Hospital\nChalakudi\nThrissur");
        address.setTypeface(fontType1);

        final String phoneNumber="9598989898";
        phone.setText(phoneNumber);
        phone.setTypeface(fontType1);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + phoneNumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
            }
        });
        phoneSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + phoneNumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
            }
        });

        timing.setText("9:30 to 5:30");

        workingDays.setText("Mon to Fri");

        final String fbLinkString="https://www.facebook.com/ThrithvamTech";
        fbLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fbLinkString));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
            }
        });

        final String instagramLinkString="https://www.instagram.com";
        instagramLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramLinkString));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
            }
        });
        owners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BoutiqueDetails.this, OwnerAndDesigner.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
            }
        });
        designers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BoutiqueDetails.this, OwnerAndDesigner.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_entry1,R.anim.slide_entry2);
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_exit1,R.anim.slide_exit2);
    }
}
