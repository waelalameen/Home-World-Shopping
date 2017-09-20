package com.wael.alameen.worldonlineshopping;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestActivity extends AppCompatActivity {

    private String pName, pPrice, pLogo, color, size, url, sec;
    private BasketDatabase database;
    private EditText num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        ((TextView) findViewById(R.id.title)).setTypeface(typeface);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        database = new BasketDatabase(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ((TextView) findViewById(R.id.hint1)).setTypeface(typeface);
        ((TextView) findViewById(R.id.hint2)).setTypeface(typeface);

        pName = getIntent().getExtras().getString("pName");
        pPrice = getIntent().getExtras().getString("pPrice");
        pLogo = getIntent().getExtras().getString("pLogo");
        List<String> pColors = getIntent().getStringArrayListExtra("colors");
        pColors.add(0, "لا يوجد");
        List<String> pSizes = getIntent().getStringArrayListExtra("sizes");
        pSizes.add(0, "لا يوجد");
        url = getIntent().getExtras().getString("url");
        sec = getIntent().getExtras().getString("section");

        TextView name = (TextView) findViewById(R.id.name);
        name.setFocusable(true);
        name.setTypeface(typeface);
        name.setText(String.format("الاسم : %s", pName));
        TextView price = (TextView) findViewById(R.id.price);
        price.setTypeface(typeface);
        price.setText(" السعر : " + pPrice + " د.ع ");
        TextView logo = (TextView) findViewById(R.id.logo);
        logo.setTypeface(typeface);
        logo.setText(String.format("الماركة : %s", pLogo));
        num = (EditText) findViewById(R.id.num);
        num.setTypeface(typeface);

//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.colors_linear);
//        for (int i = 0; i < pColors.size(); i++) {
//            CircleImageView circleImageView = new CircleImageView(this);
//            circleImageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(50, 50));
//            circleImageView.setMaxWidth(20);
//            circleImageView.setMaxHeight(20);
//            circleImageView.setBackgroundColor(Color.parseColor(pColors.get(i)));
//            linearLayout.addView(circleImageView);
//        }

        final Spinner colors = (Spinner) findViewById(R.id.colors);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pColors);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colors.setAdapter(arrayAdapter2);
        arrayAdapter2.notifyDataSetChanged();
        colors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner sizes = (Spinner) findViewById(R.id.sizes);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pSizes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizes.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        sizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                size = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button confirm = (Button) findViewById(R.id.ok);
        confirm.setTypeface(typeface);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = num.getText().toString().trim();
                if (color != null && size != null) {
                    database.insert(pName, pPrice, url, pLogo, color, size, n, sec);
                    showHint();
                } else {
                    database.insert(pName, pPrice, url, pLogo, "لا يوجد", "لا يوجد", n, sec);
                    showHint();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.badge_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_bagde);
        RelativeLayout relativeLayout = (RelativeLayout) menuItem.getActionView();
        Button badgeButton = (Button) relativeLayout.findViewById(R.id.badge_button);
        TextView counter = (TextView) relativeLayout.findViewById(R.id.counter);
        BasketDatabase database = new BasketDatabase(this);
        Cursor res = database.showALL();
        counter.setText(Integer.toString(res.getCount()));
        badgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestActivity.this, BasketActivity.class));
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return true;
    }

    private void showHint() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        AlertDialog.Builder builder = new AlertDialog.Builder(RequestActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.success, null);
        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.setContentView(view);
        TextView hint = (TextView) dialog.findViewById(R.id.hint);
        hint.setTypeface(typeface);
        hint.setText("تم اضافة الطلب الى سلة المشتريات، قم بتاكيد الطلب من هناك");
        Button cancel = (Button) dialog.findViewById(R.id.close);
        cancel.setTypeface(typeface);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(RequestActivity.this, MainActivity.class));
            }
        });
        dialog.show();
    }
}
