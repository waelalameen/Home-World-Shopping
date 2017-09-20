package com.wael.alameen.worldonlineshopping;

import android.annotation.SuppressLint;
import android.gesture.Gesture;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FullscreenActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private Matrix drawMatrix;
    private float lastFocusX, lastFocusY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawMatrix = new Matrix();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        ImageView fullImage = (ImageView) findViewById(R.id.full_image);
        TextView name = (TextView) findViewById(R.id.name);
        TextView price = (TextView) findViewById(R.id.price);
        ((TextView) findViewById(R.id.title)).setTypeface(typeface);
        name.setTypeface(typeface);
        price.setTypeface(typeface);
        String url = getIntent().getExtras().getString("url");
        String n = getIntent().getExtras().getString("name");
        String p = getIntent().getExtras().getString("price");
        Picasso.with(this).load(url).into(fullImage);
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this, new Scale());
        fullImage.setFilterTouchesWhenObscured(true);
        name.setText(n);
        price.setText(String.format("%s د.ع ", p));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        drawMatrix.postTranslate(-distanceX, -distanceY);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    class Scale extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastFocusX = detector.getFocusX();
            lastFocusY = detector.getFocusY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Matrix transformationMatrix = new Matrix();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();
            transformationMatrix.postTranslate(-focusX, -focusY);
            transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());
            float focusShiftX = focusX - lastFocusX;
            float focusShiftY = focusY - lastFocusY;
            transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY);
            drawMatrix.postConcat(transformationMatrix);
            lastFocusX = focusX;
            lastFocusY = focusY;
            return true;
        }
    }
}
