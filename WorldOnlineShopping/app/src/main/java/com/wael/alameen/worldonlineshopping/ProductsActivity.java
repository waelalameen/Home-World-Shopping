package com.wael.alameen.worldonlineshopping;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

public class ProductsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> filtered = new ArrayList<>();
    private TextView title;
    private String name;
    private static boolean pointer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        name = getIntent().getExtras().getString("name");
        new ProductsTask().execute(name);
        Log.d("name--", name);
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(typeface);
        title.setText(name);
        recyclerView = (RecyclerView) findViewById(R.id.products_recycler);
        recyclerView.setHasFixedSize(true);
        adapter = new ProductsAdapter(this, productList, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DetailsActivity.class);
        if (pointer) {
            intent.putExtra("name", filtered.get(position).getName());
            intent.putExtra("id", filtered.get(position).getId());
            intent.putExtra("sec", filtered.get(position).getSection());
            pointer = false;
        } else {
            intent.putExtra("name", productList.get(position).getName());
            intent.putExtra("id", productList.get(position).getId());
            intent.putExtra("sec", productList.get(position).getSection());
        }
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("ابحث عن سلعتك المفضلة");
        searchView.setQuery(searchView.getQuery(), true);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                pointer = false;
                setItemsVisibility(menu, item, true);
                title.setText(name);
                MenuItemCompat.collapseActionView(item);
                reInitiate();
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemsVisibility(menu, item, false);
                title.setText("");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        });
        return true;
    }

    private void reInitiate() {
        adapter = new ProductsAdapter(this, productList, ProductsActivity.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ProductsActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void setItemsVisibility(Menu menu, MenuItem search, boolean visible) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);

            if(item != search) {
                item.setVisible(visible);
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(TextUtils.isEmpty(query)) {
            adapter.notifyDataSetChanged();
        } else {
            pointer = true;
            filtered = filter(productList, query);
            Log.d("query", query);
            adapter = new ProductsAdapter(this, filtered, ProductsActivity.this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ProductsActivity.this, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        return true;
    }

    private List<Product> filter(List<Product> productList, String query) {
        query = query.toLowerCase();
        List<Product> filteredList = new ArrayList<>();
        if (productList != null && productList.size() > 0) {
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(query)) {
                    filteredList.add(product);
                }
            }
        }
        return filteredList;
    }

    class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
        private AdapterView.OnItemClickListener onItemClickListener;
        private List<Product> list;
        private Context context;

        ProductsAdapter(AdapterView.OnItemClickListener onItemClickListener, List<Product> productList, Context context) {
            this.onItemClickListener = onItemClickListener;
            list = productList;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(context).load(list.get(position).getImage()).resize(600, 300).onlyScaleDown().error(R.drawable.app_icon).into(holder.imageView);
            holder.textView.setText(list.get(position).getName());
            if (!list.get(position).getNewPrice().equals("no")) {
                holder.price.setText(String.format("%sد.ع", list.get(position).getNewPrice()));
            } else {
                holder.price.setText(String.format("%sد.ع", list.get(position).getPrice()));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView imageView;
            TextView textView, price;

            public ViewHolder(View itemView) {
                super(itemView);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
                imageView = (ImageView) itemView.findViewById(R.id.image);
                textView = (TextView) itemView.findViewById(R.id.name);
                price = (TextView) itemView.findViewById(R.id.price);
                textView.setTypeface(typeface);
                price.setTypeface(typeface);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(null, v, getLayoutPosition(), getItemId());
            }
        }
    }

    class ProductsTask extends AsyncTask<String , Product, Void> {

        private static final String ADS_URL = "http://mallnet.me/shopping/read_prods.php";

        @Override
        protected Void doInBackground(String... params) {
            String name = params[0];
            try {
                URL url = new URL(ADS_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("sec", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8");
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
                    Product product = new Product(jsonObj.getString("id"), jsonObj.getString("name"), jsonObj.getString("price"), jsonObj.getString("image"),
                            jsonObj.getString("sec"), jsonObj.getString("new_price"));
                    publishProgress(product);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Product... values) {
            super.onProgressUpdate(values);
            productList.add(0, values[0]);
            adapter.notifyDataSetChanged();
        }
    }

    class Product {
        private String name, price, newPrice;
        private String image;
        private String id;
        private String section;

        Product(String id, String name, String price, String image, String section, String newPrice) {
            setId(id);
            setName(name);
            setPrice(price);
            setImage(image);
            setSection(section);
            setNewPrice(newPrice);
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

        public String getImage() {
            return image;
        }

        private void setImage(String image) {
            this.image = image;
        }

        public String getId() {
            return id;
        }

        private void setId(String id) {
            this.id = id;
        }

        public String getSection() {
            return section;
        }

        private void setSection(String section) {
            this.section = section;
        }

        public String getNewPrice() {
            return newPrice;
        }

        private void setNewPrice(String newPrice) {
            this.newPrice = newPrice;
        }
    }
}
