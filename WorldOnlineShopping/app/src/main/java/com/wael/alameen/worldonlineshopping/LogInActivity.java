package com.wael.alameen.worldonlineshopping;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LogInActivity extends AppCompatActivity {

    private EditText place, phone, name;
    private LoginInfo loginInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        ((TextView) findViewById(R.id.title)).setTypeface(typeface);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ((TextView) findViewById(R.id.hint)).setTypeface(typeface);
        loginInfo = new LoginInfo(this);

        place = (EditText) findViewById(R.id.place);
        phone = (EditText) findViewById(R.id.phone);
        name = (EditText) findViewById(R.id.name);
        place.setTypeface(typeface);
        phone.setTypeface(typeface);
        name.setTypeface(typeface);

        Button login = (Button) findViewById(R.id.ok);
        login.setTypeface(typeface);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString().trim();
                String p = place.getText().toString().trim();
                String ph = phone.getText().toString().trim();
                loginInfo.insert(n, p, ph);
                SharedPreferences sharedPreferences = getSharedPreferences("loggedIn", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.apply();
                showHint();
                startActivity(new Intent(LogInActivity.this, MainActivity.class));
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

    private void showHint() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.success, null);
        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.setContentView(view);
        TextView hint = (TextView) dialog.findViewById(R.id.hint);
        hint.setTypeface(typeface);
        hint.setText(" تم تسجيل الدخول بنجاح, اهلا بك في عالم التسوق ");
        Button cancel = (Button) dialog.findViewById(R.id.close);
        cancel.setTypeface(typeface);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
