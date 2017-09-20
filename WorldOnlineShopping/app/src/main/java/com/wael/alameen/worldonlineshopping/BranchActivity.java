package com.wael.alameen.worldonlineshopping;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;
import java.util.List;

public class BranchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private RecyclerView.Adapter adapter;
    private List<Section> sectionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        ((TextView) findViewById(R.id.title)).setTypeface(typeface);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        String sec = getIntent().getExtras().getString("sec");
        ((TextView) findViewById(R.id.title)).setText(sec);
        new SectionsTask().execute(sec);
        RecyclerView sectionsRecycler = (RecyclerView) findViewById(R.id.sections_recycler);
        sectionsRecycler.setHasFixedSize(true);
        sectionsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new SectionsAdapter(this, this);
        sectionsRecycler.setAdapter(adapter);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ProductsActivity.class);
        intent.putExtra("name", sectionList.get(position).getName());
        startActivity(intent);
    }

    class SectionsAdapter extends RecyclerView.Adapter<SectionsAdapter.ViewHolder> {
        private AdapterView.OnItemClickListener onItemClickListener;
        private Context context;

        SectionsAdapter(AdapterView.OnItemClickListener onItemClickListener, Context context) {
            this.onItemClickListener = onItemClickListener;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sections_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(context).load(sectionList.get(position).getImage()).resize(600, 300).onlyScaleDown().error(R.drawable.app_icon).into(holder.imageView);
            holder.textView.setText(sectionList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return sectionList.size();
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

    class SectionsTask extends AsyncTask<String, Section, Void> {

        private static final String ADS_URL = "http://mallnet.me/shopping/read_branch.php";

        @Override
        protected Void doInBackground(String... params) {
            String sec = params[0];

            try {
                URL url = new URL(ADS_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("sec", "UTF-8")+"="+URLEncoder.encode(sec, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStreamWriter.close();
                outputStream.close();

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
                    Section section = new Section(jsonObj.getString("name"), jsonObj.getString("image"));
                    publishProgress(section);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Section... values) {
            super.onProgressUpdate(values);
            sectionList.add(0, values[0]);
            adapter.notifyDataSetChanged();
        }
    }

    class Section {
        private String name;
        private String image;

        Section(String name, String image) {
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
}
