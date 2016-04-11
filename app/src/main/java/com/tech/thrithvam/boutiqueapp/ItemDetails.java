package com.tech.thrithvam.boutiqueapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);



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
    }
}
