package com.tech.thrithvam.boutiqueapp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewer extends AppCompatActivity {
    PhotoView photoView;
    Bundle extras;
    ProgressBar progressBar;
    boolean isDetailsVisible=true;
    ObjectAnimator Anim1;
    RelativeLayout imgDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        extras=getIntent().getExtras();
        imgDetails =(RelativeLayout)findViewById(R.id.imgDetails);
        TextView imagename=(TextView)findViewById(R.id.imgName);
        imagename.setText(extras.getString("Image"));
        Anim1 = ObjectAnimator.ofFloat(imgDetails, "y", 1500);
        Anim1.setDuration(300);
        extras=getIntent().getExtras();
        photoView=(PhotoView)findViewById(R.id.punchAttachView);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {

                if (isDetailsVisible) {
                    Anim1.start();
                    isDetailsVisible=false;
                } else {
                    Anim1.reverse();
                    isDetailsVisible=true;
                }
            }
        });
        Picasso.with(ImageViewer.this)
                .load(getResources().getIdentifier(extras.getString("Image"), "drawable", getPackageName()))
                        .into(photoView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
    }
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    //for rotating the image
    public void rotateImage(View view){
        photoView.setRotationBy(90f);
    }
}
