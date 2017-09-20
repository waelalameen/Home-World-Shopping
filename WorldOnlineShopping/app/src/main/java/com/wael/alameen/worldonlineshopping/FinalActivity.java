package com.wael.alameen.worldonlineshopping;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FinalActivity extends AppCompatActivity {

    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
    private String sum, n, p, ph, city, sec;
    private List<String> names = new ArrayList<>();
    private List<String> prices = new ArrayList<>();
    private int t = 0;
    private TextView total;
    private RequestItemsDatabase requestItemsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        ((TextView) findViewById(R.id.title)).setTypeface(typeface);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        new ReadTask().execute();

        requestItemsDatabase = new RequestItemsDatabase(this);
        Cursor cursor = requestItemsDatabase.showAll();

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                sec = cursor.getString(6);
            }
        }

        sum = getIntent().getExtras().getString("sum");

        TextView nn = (TextView) findViewById(R.id.name);
        TextView place = (TextView) findViewById(R.id.place);
        TextView phone = (TextView) findViewById(R.id.phone);
        total = (TextView) findViewById(R.id.total);

        nn.setTypeface(typeface);
        place.setTypeface(typeface);
        phone.setTypeface(typeface);
        total.setTypeface(typeface);
        total.setText(" المجموع الكلي = " + sum + " د.ع ");

        LoginInfo loginInfo = new LoginInfo(this);
        Cursor result = loginInfo.showALL();
        if (result.getCount() > 0) {
            while (result.moveToNext()) {
                n = result.getString(0);
                p = result.getString(1);
                ph = result.getString(2);
            }

            nn.setText(String.format(" اسم الزبون : %s", n));
            place.setText(String.format(" اقرب نقطة دالة : %s", p));
            phone.setText(String.format(" رقم الهاتف : %s", ph));
        }

        spinner = (Spinner) findViewById(R.id.city);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        names.add("اختر المنطقة");
        prices.add("0");

        Button send = (Button) findViewById(R.id.ok);
        send.setTypeface(typeface);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                String currentTimeDate = simpleDateFormat.format(date);
                SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
                String token = sharedPreferences.getString("token", "");

                Cursor cursor = requestItemsDatabase.showAll();

                if (cursor.getCount() > 0) {
                    if (!city.equals("اختر المنطقة")) {
                        while (cursor.moveToNext()) {
                            new RequestTask().execute(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                                    cursor.getString(5), n, p, ph, currentTimeDate, token);
                        }
                    } else {
                        showHint();
                    }
                }
            }
        });
    }

    private void showHint() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        AlertDialog.Builder builder = new AlertDialog.Builder(FinalActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.success, null);
        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.setContentView(view);
        TextView hint = (TextView) dialog.findViewById(R.id.hint);
        hint.setTypeface(typeface);
        hint.setText(" الرجاء قم باختيار المنطقة الخاصة بك لاتمام ارسال الطلب ");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return true;
    }

    class RequestTask extends AsyncTask<String, Void, Void> {
        private static final String ADS_URL = "http://mallnet.me/shopping/request.php";

        @Override
        protected Void doInBackground(String... params) {
            String name = params[0];
            String price = params[1];
            String logo = params[2];
            String color = params[3];
            String size = params[4];
            String number = params[5];
            String customer = params[6];
            String place = params[7];
            String phone = params[8];
            String date = params[9];
            String token = params[10];

            try {
                URL url = new URL(ADS_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8")+"&"
                        +URLEncoder.encode("price", "UTF-8")+"="+URLEncoder.encode(price, "UTF-8")+"&"
                        +URLEncoder.encode("logo", "UTF-8")+"="+URLEncoder.encode(logo, "UTF-8")+"&"
                        +URLEncoder.encode("color", "UTF-8")+"="+URLEncoder.encode(color, "UTF-8")+"&"
                        +URLEncoder.encode("size", "UTF-8")+"="+URLEncoder.encode(size, "UTF-8")+"&"
                        +URLEncoder.encode("number", "UTF-8")+"="+URLEncoder.encode(number, "UTF-8")+"&"
                        +URLEncoder.encode("cus", "UTF-8")+"="+URLEncoder.encode(customer, "UTF-8")+"&"
                        +URLEncoder.encode("place", "UTF-8")+"="+URLEncoder.encode(place, "UTF-8")+"&"
                        +URLEncoder.encode("phone", "UTF-8")+"="+URLEncoder.encode(phone, "UTF-8")+"&"
                        +URLEncoder.encode("date", "UTF-8")+"="+URLEncoder.encode(date, "UTF-8")+"&"
                        +URLEncoder.encode("token", "UTF-8")+"="+URLEncoder.encode(token, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStreamWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            requestItemsDatabase.delete();
            Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
            AlertDialog.Builder builder = new AlertDialog.Builder(FinalActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.success, null);
            builder.setView(view);
            final Dialog dialog = builder.create();
            dialog.setContentView(view);
            TextView hint = (TextView) dialog.findViewById(R.id.hint);
            hint.setTypeface(typeface);
            Button cancel = (Button) dialog.findViewById(R.id.close);
            cancel.setTypeface(typeface);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(FinalActivity.this, ProductsActivity.class);
                    intent.putExtra("name", sec);
                    startActivity(intent);
                }
            });
            dialog.show();
        }
    }

    class ReadTask extends AsyncTask<Void, Void, Void> {
        private static final String ADS_URL = "http://mallnet.me/shopping/read_cities.php";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(ADS_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = buffer.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                String json_string = stringBuilder.toString().trim();
                JSONObject object = new JSONObject(json_string);
                JSONArray jsonArray = object.getJSONArray("server_response");
                int i = 0;
                while (i < jsonArray.length()) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    i++;
                    names.add(jsonObj.getString("name"));
                    prices.add(jsonObj.getString("price"));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            arrayAdapter.notifyDataSetChanged();
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (names != null && prices != null) {
                        city = parent.getItemAtPosition(position).toString();
                        t = Integer.parseInt(prices.get(position));
                        t = t + Integer.parseInt(sum);
                        total.setText(" المجموع الكلي = " + t + " د.ع ");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
}
