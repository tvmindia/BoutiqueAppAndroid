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
        SearchView searchView = (SearchView) findViewById(R.id.searchView);

        //---------------Creates Folder for app--------------------
        File folder = new File(Environment.getExternalStorageDirectory() + "/"+ R.string.app_name);
        if (!folder.exists()) {
            folder.mkdir();
        }

        //-------------------------hide actionbar on scroll----------------------------



        final ObservableScrollView scrollView=(ObservableScrollView)findViewById(R.id.homeScroll);
        scrollView.setScrollViewCallbacks(this);
     //   ab.hide();
        /*scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollView.getScrollY()>0){

                }
            }
        });*/

        /*scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(!otherServicesLoaded){
                    int diff = (otherServiceLabel.getBottom()-(SPDetails.getHeight()+SPDetails.getScrollY()));
                    if( diff <= 0 )
                    {  // Toast.makeText(BeautyParlour.this, "Bottom has been reached",Toast.LENGTH_LONG).show();
                        new GetDetailsOfOtherServices().execute();
                        otherServicesLoaded =true;
                    }
                }
            }
        });*/



        //----------------------- New Arrivals with animation--------------------------

        //   SliderLayout sliderShow = (SliderLayout) findViewById(R.id.slider);
      /*  SliderLayout sliderShow = (SliderLayout) findViewById(R.id.slider);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.Background2Foreground.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow.addSlider(textSliderViews);

            sliderShow.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        }


        SliderLayout sliderShow2 = (SliderLayout) findViewById(R.id.slider2);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.Tablet.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow2.addSlider(textSliderViews);

            sliderShow2.setPresetTransformer(SliderLayout.Transformer.Tablet);
        }

        SliderLayout sliderShow3 = (SliderLayout) findViewById(R.id.slider3);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.Stack.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow3.addSlider(textSliderViews);

            sliderShow3.setPresetTransformer(SliderLayout.Transformer.Stack);
        }

        SliderLayout sliderShow4 = (SliderLayout) findViewById(R.id.slider4);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.RotateUp.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow4.addSlider(textSliderViews);

            sliderShow4.setPresetTransformer(SliderLayout.Transformer.RotateUp);
        }

        SliderLayout sliderShow5 = (SliderLayout) findViewById(R.id.slider5);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.RotateDown.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow5.addSlider(textSliderViews);

            sliderShow5.setPresetTransformer(SliderLayout.Transformer.RotateDown);
        }
*/
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

/*


        SliderLayout sliderShow8 = (SliderLayout) findViewById(R.id.slider8);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.CubeIn.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow8.addSlider(textSliderViews);

            sliderShow8.setPresetTransformer(SliderLayout.Transformer.CubeIn);
        }

        SliderLayout sliderShow9 = (SliderLayout) findViewById(R.id.slider9);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.DepthPage.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow9.addSlider(textSliderViews);

            sliderShow9.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        }

        SliderLayout sliderShow10 = (SliderLayout) findViewById(R.id.slider10);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.Fade.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow10.addSlider(textSliderViews);

            sliderShow10.setPresetTransformer(SliderLayout.Transformer.Fade);
        }

        SliderLayout sliderShow11 = (SliderLayout) findViewById(R.id.slider11);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.FlipHorizontal.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow11.addSlider(textSliderViews);

            sliderShow11.setPresetTransformer(SliderLayout.Transformer.FlipHorizontal);
        }

        SliderLayout sliderShow12 = (SliderLayout) findViewById(R.id.slider12);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.FlipPage.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow12.addSlider(textSliderViews);

            sliderShow12.setPresetTransformer(SliderLayout.Transformer.FlipPage);
        }

        SliderLayout sliderShow13 = (SliderLayout) findViewById(R.id.slider13);
        for (int i = 0; i < 5; i++) {
            final int Finali = i;
            final String image = "na" + (Integer.toString(Finali + 1));
            TextSliderView textSliderViews = new TextSliderView(this);
            textSliderViews
                    .description(SliderLayout.Transformer.Foreground2Background.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName()));
            sliderShow13.addSlider(textSliderViews);

            sliderShow13.setPresetTransformer(SliderLayout.Transformer.Foreground2Background);
        }


        sliderShow.stopAutoCycle();

        sliderShow2.stopAutoCycle();
        sliderShow3.stopAutoCycle();
        sliderShow4.stopAutoCycle();
        sliderShow5.stopAutoCycle();
        sliderShow6.stopAutoCycle();
        sliderShow8.stopAutoCycle();
        sliderShow9.stopAutoCycle();
        sliderShow10.stopAutoCycle();
        sliderShow11.stopAutoCycle();
        sliderShow12.stopAutoCycle();
        sliderShow13.stopAutoCycle();
*/







//--------------------Categories text-----------------------------------------
       /* final TextView categoryScroll=(TextView)findViewById(R.id.categoryScroll);
        animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animSlideDown.setDuration(2000);
        animSlideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                i++;
                categoryScroll.setText("Category " + i);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                categoryScroll.startAnimation(animSlideDown);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        categoryScroll.startAnimation(animSlideDown);*/


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
