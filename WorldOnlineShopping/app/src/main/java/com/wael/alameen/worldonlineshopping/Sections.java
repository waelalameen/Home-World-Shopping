package com.wael.alameen.worldonlineshopping;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Sections extends Fragment implements AdapterView.OnItemClickListener {

    private RecyclerView.Adapter adapter;
    private List<Section> sectionList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    public Sections() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sections, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new SectionsTask().execute();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK, getActivity().getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                //sectionList.clear();
                //new SectionsTask().execute();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        RecyclerView sectionsRecycler = (RecyclerView) view.findViewById(R.id.sections_recycler);
        sectionsRecycler.setHasFixedSize(true);
        sectionsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new SectionsAdapter(this);
        sectionsRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (sectionList.get(position).getHas().equals("true")) {
            Intent intent = new Intent(getContext(), BranchActivity.class);
            intent.putExtra("sec", sectionList.get(position).getName());
            startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), ProductsActivity.class);
            intent.putExtra("name", sectionList.get(position).getName());
            startActivity(intent);
        }
    }

    class SectionsAdapter extends RecyclerView.Adapter<SectionsAdapter.ViewHolder> {
        private AdapterView.OnItemClickListener onItemClickListener;

        SectionsAdapter(AdapterView.OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public SectionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sections_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SectionsAdapter.ViewHolder holder, int position) {
            Picasso.with(getContext()).load(sectionList.get(position).getImage()).resize(600, 300).onlyScaleDown().error(R.drawable.app_icon).into(holder.imageView);
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
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "Jannal.ttf");
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

    class SectionsTask extends AsyncTask<Void, Section, Void> {

        private static final String ADS_URL = "http://mallnet.me/shopping/read_sections.php";

        @Override
        protected Void doInBackground(Void... params) {
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
                    Section section = new Section(jsonObj.getString("name"), jsonObj.getString("image"), jsonObj.getString("has"));
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    class Section {
        private String name;
        private String image;
        private String has;

        Section(String name, String image, String has) {
            setName(name);
            setImage(image);
            setHas(has);
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

        public String getHas() {
            return has;
        }

        private void setHas(String has) {
            this.has = has;
        }
    }
}
