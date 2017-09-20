package com.wael.alameen.worldonlineshopping;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private static final String phoneNumber = "07704227728";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        ((TextView) findViewById(R.id.title)).setTypeface(typeface);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        TextView hint = (TextView) findViewById(R.id.hint);
        hint.setTypeface(typeface);
        hint.setText("عالم التسوق الالكتروني:\n" +
                "هو تطبيق  لتقديم خدمة التسوق الألكتروني والتوصيل الى المنازل. انطلق المشروع في سنة 2017 وتم تصميمه على ايادي خبرات عراقية متخصصة في مجال تطوير البرمجيات والمواقع.\n" +
                "عالم التسوق الالكتروني موقع يعتمد على استيراد المواد من اشهر الماركات العالميه وشراء من اشهر التجار ضمن مجال عملهم لتوفير انسب الاسعار ضمن خدمه التوصيل السريعه\n");

        ((Button) findViewById(R.id.face)).setTypeface(typeface);
        (findViewById(R.id.face)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String faceLink = "https://www.facebook.com/worldonlineshopping9/";
                Uri uri = Uri.parse(faceLink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        ((Button) findViewById(R.id.number)).setTypeface(typeface);
        (findViewById(R.id.number)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (ActivityCompat.checkSelfPermission(AboutActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return true;
    }
}
