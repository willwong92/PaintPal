package com.willwong.paintbrushapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * This activity initializes brushes, canvas, drawing board
 */
public class MainActivity extends AppCompatActivity {
    private Animation mWheelAnimation;
    private ImageView mPaintBrush;
    private TextView mPaintText;
    private ProgressBar pBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPaintBrush = (ImageView)findViewById(R.id.brush_imageview);
        pBar = (ProgressBar) findViewById(R.id.ProgressBar01);

        mPaintText = (TextView)findViewById(R.id.title_textview);
        Animation rotation = AnimationUtils.loadAnimation(this,R.anim.rotatatingbrush);
        mPaintBrush.setAnimation(rotation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, CanvasActivity.class);
                startActivity(intent);
                finish();
            }
        }, 6000);
    }

}
