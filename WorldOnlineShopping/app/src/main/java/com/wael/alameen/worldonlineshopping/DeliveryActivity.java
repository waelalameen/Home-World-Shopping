package com.wael.alameen.worldonlineshopping;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DeliveryActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapter;
    private List<Delivery> deliveryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        ((TextView) findViewById(R.id.title)).setTypeface(typeface);
        ((TextView) findViewById(R.id.title)).setText("اسعار التوصيل");
        ((TextView) findViewById(R.id.city)).setTypeface(typeface);
        ((TextView) findViewById(R.id.price)).setTypeface(typeface);

        new Task().execute();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        adapter = new DeliveryAdapter(this, deliveryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return true;
    }

    class Delivery {
        private String name, price;

        Delivery(String name, String price) {
            setName(name);
            setPrice(price);
        }

        public String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        private void setPrice(String price) {
            this.price = price;
        }
    }

    private class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.ViewHolder> {
        private Context context;
        private List<Delivery> deliveryList;

        public DeliveryAdapter(Context context, List<Delivery> deliveryList) {
            this.context = context;
            this.deliveryList = deliveryList;
            LayoutInflater.from(context);
        }

        @Override
        public DeliveryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DeliveryAdapter.ViewHolder holder, int position) {
            holder.name.setText(deliveryList.get(position).getName());
            holder.price.setText(String.format("%s د.ع ", deliveryList.get(position).getPrice()));
        }

        @Override
        public int getItemCount() {
            return deliveryList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, price;

            public ViewHolder(View itemView) {
                super(itemView);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
                name = (TextView) itemView.findViewById(R.id.name);
                price = (TextView) itemView.findViewById(R.id.price);
                name.setTypeface(typeface);
                price.setTypeface(typeface);
            }
        }
    }

    class Task extends AsyncTask<Void, Delivery, Void> {
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
                    Delivery delivery = new Delivery(jsonObj.getString("name"), jsonObj.getString("price"));
                    publishProgress(delivery);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Delivery... values) {
            super.onProgressUpdate(values);
            deliveryList.add(0, values[0]);
            adapter.notifyDataSetChanged();
        }
    }
}
