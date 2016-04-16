package com.tech.thrithvam.boutiqueapp;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ItemDetails extends AppCompatActivity {
ImageView favorite;
    ImageView share;
    Boolean isFav=false;
    Integer favCount=220;
    TextView favCountString;
    SliderLayout itemImages;
    LayoutInflater inflater;
    TextView description;
    TextView viewDesigner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        getSupportActionBar().setElevation(0);
        inflater = (LayoutInflater)ItemDetails.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Typeface fontType1 = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        description=(TextView)findViewById(R.id.description);
        description.setTypeface(fontType1);
        itemImages = (SliderLayout) findViewById(R.id.itemImages);
        for (int i = 0; i < 3; i++) {
            final String image = "f" + (Integer.toString(i + 1));
            DefaultSliderView sliderViews = new DefaultSliderView(this);
            sliderViews
                    .description(SliderLayout.Transformer.DepthPage.toString())
                    .image(getResources().getIdentifier(image, "drawable", getPackageName())).setScaleType(BaseSliderView.ScaleType.CenterInside);
            sliderViews.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    Intent intent=new Intent(ItemDetails.this,ImageViewer.class);
                    intent.putExtra("Image",image);
                    startActivity(intent);
                }
            });
            itemImages.addSlider(sliderViews);
//            sliderShow9.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        }
        itemImages.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        itemImages.stopAutoCycle();

        viewDesigner=(TextView)findViewById(R.id.view_designer);
        viewDesigner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ItemDetails.this, OwnerAndDesigner.class);
                startActivity(intent);
            }
        });


        //-----------Add to favorite and Sharing--------------------------
        favorite=(ImageView)findViewById(R.id.fav);
        favCountString=(TextView)findViewById(R.id.favCount);
        favCountString.setText(getResources().getString(R.string.favorite_count,favCount));
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    favCount--;
                    favorite.setImageResource(R.drawable.fav_no);
                    Toast.makeText(ItemDetails.this, R.string.remove_fav_msg, Toast.LENGTH_LONG).show();
                    favCountString.setText(getResources().getString(R.string.favorite_count, favCount));
                    isFav = false;
                } else {
                    favCount++;
                    favorite.setImageResource(R.drawable.fav);
                    Toast.makeText(ItemDetails.this, R.string.add_fav_msg, Toast.LENGTH_LONG).show();
                    favCountString.setText(getResources().getString(R.string.favorite_count, favCount));
                    isFav = true;
                }
            }
        });

        share=(ImageView)findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ItemDetails.this,R.string.share_image,Toast.LENGTH_LONG).show();
                try {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "f" + Integer.toString(itemImages.getCurrentPosition() + 1)));
                    startActivity(Intent.createChooser(share, getString(R.string.share_image)));
                } catch (Exception e) {
                    Toast.makeText(ItemDetails.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        //-----------------------Similar items grid---------------------------
        itemsGrid((LinearLayout)findViewById(R.id.similarProducts));
        //-----------------------Related items grid---------------------------
        itemsGrid((LinearLayout)findViewById(R.id.relatedProducts));
    }
    public void itemsGrid(LinearLayout fillingArea){
        LinearLayout itemRow=new LinearLayout(ItemDetails.this);
        itemRow.setOrientation(LinearLayout.HORIZONTAL);
        itemRow= (LinearLayout) inflater.inflate(R.layout.items_two_coloum_frame, null);
        itemRow.setPadding(0,10,0,5);
        LinearLayout leftItem=(LinearLayout)itemRow.findViewById(R.id.LinearLeft);
        LinearLayout rightItem=(LinearLayout)itemRow.findViewById(R.id.LinearRight);
        View item=inflater.inflate(R.layout.grid_item, null);
        ImageView imageView=(ImageView)item.findViewById(R.id.gridImg);
        Picasso.with(ItemDetails.this).load(R.drawable.k1).into(imageView);
        leftItem.addView(item);
        View item2=inflater.inflate(R.layout.grid_item, null);
        ImageView imageView2=(ImageView)item2.findViewById(R.id.gridImg);
        Picasso.with(ItemDetails.this).load(R.drawable.k2).into(imageView2);
        rightItem.addView(item2);
        fillingArea.addView(itemRow);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetails.this, ItemDetails.class);
                startActivity(intent);
            }
        });
        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetails.this, ItemDetails.class);
                startActivity(intent);
            }
        });
        //More---------------
        TextView more = new TextView(ItemDetails.this);
        more.setText("more");
        more.setGravity(Gravity.RIGHT);
        if (Build.VERSION.SDK_INT < 23) {
            more.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        } else {
            more.setTextAppearance(android.R.style.TextAppearance_Medium);
        }
        more.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            more.setTextColor(getColor(R.color.accent));
        }
        else {
            more.setTextColor(getResources().getColor(R.color.accent));
        }
        more.setPadding(5,5,5,0);
        fillingArea.addView(more);
    }
    //---------------Menu creation---------------------------------------------
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
                break;
            case R.id.boutique:
                Intent intentBoutique = new Intent(this, BoutiqueDetails.class);
                startActivity(intentBoutique);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
