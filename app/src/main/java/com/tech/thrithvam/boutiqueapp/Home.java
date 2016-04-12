package com.tech.thrithvam.boutiqueapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.SearchView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class Home extends AppCompatActivity implements ObservableScrollViewCallbacks {


LinearLayout homeScreen;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        inflater = (LayoutInflater)Home.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        homeScreen=(LinearLayout)findViewById(R.id.homeScreen);
      /*  final TextView newArrivalsLabel = (TextView) findViewById(R.id.new_arrivals_label);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        newArrivalsLabel.setTypeface(type);
       */
        //-------------------------hide actionbar on scroll----------------------------
      final ObservableScrollView scrollView=(ObservableScrollView)findViewById(R.id.homeScroll);
      scrollView.setScrollViewCallbacks(this);

        SliderLayout sliderShow6 = (SliderLayout) findViewById(R.id.slider6);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.Accordion.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow6.addSlider(textSliderViews);

            sliderShow6.setPresetTransformer(SliderLayout.Transformer.Accordion);
        }
        //-------------------------------- Items Grid----------------------------------



        itemhorizontal("Saree", true); //temporary logic
        itemhorizontal("", false);
        itemhorizontal("Tops",true);
        itemhorizontal("",false);
        itemhorizontal("Skirts",true);
        itemhorizontal("",false);
        itemhorizontal("Kids",true);
        itemhorizontal("",false);
        itemhorizontal("Kurthas",true);
        itemhorizontal("",false);

    }
    public void itemhorizontal(String category, Boolean isNewCat){
        if(isNewCat) { //temporary logic
            //Title-----------
            TextView categoryTitle = new TextView(Home.this);
            categoryTitle.setText(category);
            if (Build.VERSION.SDK_INT < 23) {
                categoryTitle.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            } else {
                categoryTitle.setTextAppearance(android.R.style.TextAppearance_Medium);
            }
            categoryTitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            categoryTitle.setPadding(5, 5, 0, 5);
            categoryTitle.setTextColor(Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                categoryTitle.setBackgroundColor(Home.this.getColor(R.color.whiteBackground));
            }
            else {
            categoryTitle.setBackgroundColor(getResources().getColor(R.color.whiteBackground));}
            homeScreen.addView(categoryTitle);
        }
        LinearLayout itemRow=new LinearLayout(Home.this);
        itemRow.setOrientation(LinearLayout.HORIZONTAL);
        itemRow= (LinearLayout) inflater.inflate(R.layout.items_two_coloum_frame,null);
        LinearLayout leftItem=(LinearLayout)itemRow.findViewById(R.id.LinearLeft);
        LinearLayout rightItem=(LinearLayout)itemRow.findViewById(R.id.LinearRight);
        View item=inflater.inflate(R.layout.grid_item, null);
        ImageView imageView=(ImageView)item.findViewById(R.id.gridImg);
        Picasso.with(Home.this).load(R.drawable.na2).into(imageView);
        leftItem.addView(item);
        View item2=inflater.inflate(R.layout.grid_item, null);
        ImageView imageView2=(ImageView)item2.findViewById(R.id.gridImg);
        Picasso.with(Home.this).load(R.drawable.na3).into(imageView2);
        rightItem.addView(item2);
        homeScreen.addView(itemRow);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, ItemDetails.class);
                startActivity(intent);
            }
        });
        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home.this,ItemDetails.class);
                startActivity(intent);
            }
        });
        if(!isNewCat) { ////////temporary logic
            //More---------------
            TextView more = new TextView(Home.this);
            more.setText("more");
            more.setGravity(Gravity.RIGHT);
            if (Build.VERSION.SDK_INT < 23) {
                more.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            } else {
                more.setTextAppearance(android.R.style.TextAppearance_Medium);
            }
            more.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            more.setPadding(5, 5, 5, 5);
            more.setTextColor(Color.WHITE);
            homeScreen.addView(more);
        }
    }


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
                //final Intent intentUser = new Intent(this, Login.class);


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
        if (scrollState == ScrollState.UP) {
                if (ab.isShowing()) {
                    ab.hide();
                }
        } else if (scrollState == ScrollState.DOWN) {
                if (!ab.isShowing()) {
                    ab.show();
                }
            }
        }
    }
}
