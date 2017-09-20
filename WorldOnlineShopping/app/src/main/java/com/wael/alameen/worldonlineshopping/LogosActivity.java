package com.wael.alameen.worldonlineshopping;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

public class LogosActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private RecyclerView.Adapter adapter;
    private List<Logo> logoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        new LogosTask().execute();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        ((TextView) findViewById(R.id.title)).setTypeface(typeface);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.logo_recycler);
        recyclerView.setHasFixedSize(true);
        adapter = new LogoAdapter(this, logoList, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
                startActivity(new Intent(LogosActivity.this, BasketActivity.class));
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ProductsLogoActivity.class);
        intent.putExtra("logo", logoList.get(position).getName());
        startActivity(intent);
    }

    class LogoAdapter extends RecyclerView.Adapter<LogoAdapter.ViewHolder> {
        private Context context;
        private List<Logo> logoList;
        private AdapterView.OnItemClickListener onItemClickListener;

        LogoAdapter(Context context, List<Logo> logoList, AdapterView.OnItemClickListener onItemClickListener) {
            this.context = context;
            this.logoList = logoList;
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sections_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(context).load(logoList.get(position).getImage()).resize(600, 300).onlyScaleDown().error(R.drawable.app_icon).into(holder.imageView);
            holder.textView.setText(logoList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return logoList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView imageView;
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
                imageView = (ImageView) itemView.findViewById(R.id.image);
                textView = (TextView) itemView.findViewById(R.id.name);
                textView.setTypeface(typeface);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(null, v, getLayoutPosition(), getItemId());
            }
        }
    }

    class Logo {
        private String name;
        private String image;

        Logo(String name, String image) {
            setName(name);
            setImage(image);
        }

        public String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        private void setImage(String image) {
            this.image = image;
        }
    }

    class LogosTask extends AsyncTask<Void, Logo, Void> {
        private static final String ADS_URL = "http://mallnet.me/shopping/read_logos.php";

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
                    Logo logo = new Logo(jsonObj.getString("name"), jsonObj.getString("image"));
                    publishProgress(logo);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Logo... values) {
            super.onProgressUpdate(values);
            logoList.add(0, values[0]);
            adapter.notifyDataSetChanged();
        }
    }
}
