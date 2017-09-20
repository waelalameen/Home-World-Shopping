package com.wael.alameen.worldonlineshopping;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.Timer;
import java.util.TimerTask;

public class DetailsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private RecyclerView.Adapter sizesAdapter, relatedAdapter;
    public static List<Size> sizes = new ArrayList<>();
    public static ArrayList<Color> colors = new ArrayList<>();
    private List<Items> itemsList = new ArrayList<>();
    private List<String> c = new ArrayList<>();
    private List<String> s = new ArrayList<>();
    private ViewPager viewPager;
    private ArrayList<String> images = new ArrayList<>();
    private Slider adapter;
    private String jName, jPrice, newPrice, jDesc, jLogo, sec;
    private TextView name, desc, price, newP;
    private static  int counter = 0;
    private static int offset = -6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        String mName = getIntent().getExtras().getString("name");
        String id = getIntent().getExtras().getString("id");
        sec = getIntent().getExtras().getString("sec");
        new DetailsTask().execute(mName, id);
        sizes.clear();
        new SizesTask().execute(mName, id);
        colors.clear();
        new ColorsTask().execute(mName, id);
        new ItemsTask().execute(mName, sec , Integer.toString(offset));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(typeface);
        title.setText(mName);
        ((TextView) findViewById(R.id.hint)).setTypeface(typeface);
        ((TextView) findViewById(R.id.size)).setTypeface(typeface);
        name = (TextView) findViewById(R.id.name);
        desc = (TextView) findViewById(R.id.desc);
        price = (TextView) findViewById(R.id.price);
        newP = (TextView) findViewById(R.id.new_price);
        name.setTypeface(typeface);
        desc.setTypeface(typeface);
        price.setTypeface(typeface);
        newP.setTypeface(typeface);

        Button buy = (Button) findViewById(R.id.buy);
        buy.setTypeface(typeface);
        buy.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("loggedIn", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                if (isLoggedIn) {
                    Intent intent = new Intent(DetailsActivity.this, RequestActivity.class);
                    intent.putExtra("pName", jName);
                    if (newPrice.equals("no")) {
                        intent.putExtra("pPrice", jPrice);
                    } else {
                        intent.putExtra("pPrice", newPrice);
                    }
                    intent.putExtra("pLogo", jLogo);
                    intent.putStringArrayListExtra("colors", (ArrayList<String>) c);
                    intent.putStringArrayListExtra("sizes", (ArrayList<String>) s);
                    intent.putExtra("url", images.get(0));
                    intent.putExtra("section", sec);
                    startActivity(intent);
                } else {
                    showHint("يجب عليك تسجيل الدخول اولا لشراء هذه البضاعة");
                }
            }
        });

        RecyclerView sizes = (RecyclerView) findViewById(R.id.recycler_sizes);
        sizes.setHasFixedSize(true);
        sizes.setFocusable(false);
        sizesAdapter = new SizesAdapter();
        sizes.setLayoutManager(new LinearLayoutManager(this));
        sizes.setAdapter(sizesAdapter);
        sizesAdapter.notifyDataSetChanged();

        relatedAdapter = new RelatedAdapter(this, itemsList, this);
        RecyclerView related = (RecyclerView) findViewById(R.id.related);
        related.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6, GridLayoutManager.HORIZONTAL, false);
        gridLayoutManager.setOrientation(GridLayout.VERTICAL);
        related.setLayoutManager(gridLayoutManager);
        related.setAdapter(relatedAdapter);
        relatedAdapter.notifyDataSetChanged();

        viewPager = (ViewPager) findViewById(R.id.image_viewpager);
        adapter = new Slider(this);
        viewPager.setOffscreenPageLimit(10);
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimer(), 10000, 10000);
    }

    class Slider extends PagerAdapter {

        private Context context;

        Slider(Context context) {
            LayoutInflater.from(context);
            this.context = context;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.slide, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.images_slide);
            if (!images.get(position).equals("")) {
                Picasso.with(context).load(images.get(position)).into(imageView);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailsActivity.this, FullscreenActivity.class);
                    intent.putExtra("url", images.get(position));
                    intent.putExtra("name", jName);
                    intent.putExtra("price", jPrice);
                    startActivity(intent);
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView((RelativeLayout) object);
        }
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
                startActivity(new Intent(DetailsActivity.this, BasketActivity.class));
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
        for (Color color : colors) {
            c.add(color.getColor());
        }

        for (Size size : sizes) {
            s.add(size.getSize());
        }

        Intent intent = new Intent(DetailsActivity.this, DetailsActivity.class);
        intent.putExtra("name", itemsList.get(position).getName());
        intent.putExtra("id", itemsList.get(position).getId());
        intent.putExtra("sec", itemsList.get(position).getSection());
        intent.putStringArrayListExtra("colors", (ArrayList<String>) c);
        intent.putStringArrayListExtra("sizes", (ArrayList<String>) s);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (offset > itemsList.size()) {
            offset = 0;
        } else {
            offset = offset + 6;
        }
    }

    class SizesAdapter extends RecyclerView.Adapter<SizesAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sizes_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.size.setText(sizes.get(position).getSize());
        }

        @Override
        public int getItemCount() {
            return sizes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView size;

            public ViewHolder(View itemView) {
                super(itemView);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
                size = (TextView) itemView.findViewById(R.id.size);
                size.setTypeface(typeface);
            }
        }
    }

    class RelatedAdapter extends RecyclerView.Adapter<RelatedAdapter.ViewHolder> {
        private Context context;
        private List<Items> itemsList;
        private AdapterView.OnItemClickListener onItemClickListener;

        RelatedAdapter(Context context, List<Items> itemsList, AdapterView.OnItemClickListener onItemClickListener) {
            this.context = context;
            this.itemsList = itemsList;
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.related_items, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(context).load(itemsList.get(position).getImage()).resize(600, 300).onlyScaleDown().error(R.drawable.app_icon).into(holder.image);
            holder.name.setText(itemsList.get(position).name);
        }

        @Override
        public int getItemCount() {
            return itemsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name;
            ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
                name = (TextView) itemView.findViewById(R.id.name);
                name.setTypeface(typeface);
                image = (ImageView) itemView.findViewById(R.id.image);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(null, v, getLayoutPosition(), getItemId());
            }
        }
    }

    class ItemsTask extends AsyncTask<String, Items, Void> {
        private static final String ADS_URL = "http://mallnet.me/shopping/read_items.php";

        @Override
        protected Void doInBackground(String... params) {
            String name = params[0];
            String section = params[1];
            String offset = params[2];

            try {
                URL url = new URL(ADS_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("sec", "UTF-8")+"="+URLEncoder.encode(section, "UTF-8")+"&"
                        +URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8")+"&"
                        +URLEncoder.encode("offset", "UTF-8")+"="+URLEncoder.encode(offset, "UTF-8");
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
                    Items items = new Items(jsonObj.getString("name"), jsonObj.getString("image"), jsonObj.getString("id"), jsonObj.getString("sec"));
                    publishProgress(items);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            try {
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Items... values) {
            super.onProgressUpdate(values);
            itemsList.add(0, values[0]);
            relatedAdapter.notifyDataSetChanged();
        }
    }

    class Items {
        private String name;
        private String image;
        private String id;
        private String section;

        Items(String name, String image, String id, String section) {
            setName(name);
            setImage(image);
            setId(id);
            setSection(section);
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
    }

    class MyTimer extends TimerTask {

        @Override
        public void run() {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() == 0) {
                            viewPager.setCurrentItem(1);
                        } else if (viewPager.getCurrentItem() == 1) {
                            viewPager.setCurrentItem(2);
                        } else if (viewPager.getCurrentItem() == 2) {
                            viewPager.setCurrentItem(0);
                        } else if (viewPager.getCurrentItem() == 3) {
                            viewPager.setCurrentItem(4);
                        } else if (viewPager.getCurrentItem() == 4) {
                            viewPager.setCurrentItem(5);
                        } else if (viewPager.getCurrentItem() == 5) {
                            viewPager.setCurrentItem(6);
                        } else if (viewPager.getCurrentItem() == 6) {
                            viewPager.setCurrentItem(7);
                        } else if (viewPager.getCurrentItem() == 7) {
                            viewPager.setCurrentItem(8);
                        } else if (viewPager.getCurrentItem() == 8) {
                            viewPager.setCurrentItem(9);
                        } else if (viewPager.getCurrentItem() == 9) {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showHint(String message) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.success2, null);
        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.setContentView(view);
        TextView hint = (TextView) dialog.findViewById(R.id.hint);
        hint.setTypeface(typeface);
        hint.setText(message);

        Button go = (Button) dialog.findViewById(R.id.go);
        go.setTypeface(typeface);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailsActivity.this, LogInActivity.class));
            }
        });

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

    class DetailsTask extends AsyncTask<String, Void, Void> {
        private static final String ADS_URL = "http://mallnet.me/shopping/read_detail.php";

        @Override
        protected Void doInBackground(String... params) {
            String name = params[0];
            Log.d("name-------------", name);
            String id = params[1];

            try {
                URL url = new URL(ADS_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8")+"&"
                        +URLEncoder.encode("id", "UTF-8")+"="+URLEncoder.encode(id, "UTF-8");
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
                    jName = jsonObj.getString("name");
                    jDesc = jsonObj.getString("desc");
                    jPrice = jsonObj.getString("price");
                    newPrice = jsonObj.getString("new_price");
                    jLogo = jsonObj.getString("logo");
                    String img1 = jsonObj.getString("img1");
                    String img2 = jsonObj.getString("img2");
                    String img3 = jsonObj.getString("img3");
                    String img4 = jsonObj.getString("img4");
                    String img5 = jsonObj.getString("img5");
                    String img6 = jsonObj.getString("img6");
                    String img7 = jsonObj.getString("img7");
                    String img8 = jsonObj.getString("img8");
                    String img9 = jsonObj.getString("img9");
                    String img10 = jsonObj.getString("img10");

                    if (!img1.equals("") && img2.equals("")  && img3.equals("")  && img4.equals("")  && img5.equals("")  && img6.equals("")  && img7.equals("") && img8.equals("")  && img9.equals("")  && img10.equals("")) {
                        images.add(0, img1);
                    } else if (!img1.equals("") && !img2.equals("")  && img3.equals("")  && img4.equals("")  && img5.equals("")  && img6.equals("")  && img7.equals("") && img8.equals("")  && img9.equals("")  && img10.equals("")) {
                        images.add(0, img1);
                        images.add(1, img2);
                    } else if (!img1.equals("") && !img2.equals("")  && !img3.equals("")  && img4.equals("")  && img5.equals("")  && img6.equals("")  && img7.equals("") && img8.equals("")  && img9.equals("")  && img10.equals("")) {
                        images.add(0, img1);
                        images.add(1, img2);
                        images.add(2, img3);
                    } else if (!img1.equals("") && !img2.equals("")  && !img3.equals("")  && !img4.equals("")  && img5.equals("")  && img6.equals("")  && img7.equals("") && img8.equals("")  && img9.equals("")  && img10.equals("")) {
                        images.add(0, img1);
                        images.add(1, img2);
                        images.add(2, img3);
                        images.add(3, img4);
                    } else if (!img1.equals("") && !img2.equals("")  && !img3.equals("")  && !img4.equals("")  && !img5.equals("")  && img6.equals("")  && img7.equals("") && img8.equals("")  && img9.equals("")  && img10.equals("")) {
                        images.add(0, img1);
                        images.add(1, img2);
                        images.add(2, img3);
                        images.add(3, img4);
                        images.add(4, img5);
                    } else if (!img1.equals("") && !img2.equals("")  && !img3.equals("")  && !img4.equals("")  && !img5.equals("")  && !img6.equals("")  && img7.equals("") && img8.equals("")  && img9.equals("")  && img10.equals("")) {
                        images.add(0, img1);
                        images.add(1, img2);
                        images.add(2, img3);
                        images.add(3, img4);
                        images.add(4, img5);
                        images.add(5, img6);
                    } else if (!img1.equals("") && !img2.equals("")  && !img3.equals("")  && !img4.equals("")  && !img5.equals("")  && !img6.equals("")  && !img7.equals("") && img8.equals("")  && img9.equals("")  && img10.equals("")) {
                        images.add(0, img1);
                        images.add(1, img2);
                        images.add(2, img3);
                        images.add(3, img4);
                        images.add(4, img5);
                        images.add(5, img6);
                        images.add(6, img7);
                    } else if (!img1.equals("") && !img2.equals("")  && !img3.equals("")  && !img4.equals("")  && !img5.equals("")  && !img6.equals("")  && !img7.equals("") && !img8.equals("")  && img9.equals("")  && img10.equals("")) {
                        images.add(0, img1);
                        images.add(1, img2);
                        images.add(2, img3);
                        images.add(3, img4);
                        images.add(4, img5);
                        images.add(5, img6);
                        images.add(6, img7);
                        images.add(7, img8);
                    } else if (!img1.equals("") && !img2.equals("")  && !img3.equals("")  && !img4.equals("")  && !img5.equals("")  && !img6.equals("")  && !img7.equals("") && !img8.equals("")  && !img9.equals("")  && img10.equals("")) {
                        images.add(0, img1);
                        images.add(1, img2);
                        images.add(2, img3);
                        images.add(3, img4);
                        images.add(4, img5);
                        images.add(5, img6);
                        images.add(6, img7);
                        images.add(7, img8);
                        images.add(8, img9);
                    } else {
                        images.add(0, img1);
                        images.add(1, img2);
                        images.add(2, img3);
                        images.add(3, img4);
                        images.add(4, img5);
                        images.add(5, img6);
                        images.add(6, img7);
                        images.add(7, img8);
                        images.add(8, img9);
                        images.add(9, img10);
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            try {
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            name.setText(jName);
            if (newPrice != null) {
                if (newPrice.equals("no")) {
                    price.setText(String.format("%s د.ع ", jPrice));
                } else {
                    newP.setVisibility(View.VISIBLE);
                    price.setText(String.format("%s د.ع ", newPrice));
                    newP.setText(String.format("%s د.ع ", jPrice));
                    newP.setPaintFlags(newP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
            desc.setText(jDesc);
            adapter.notifyDataSetChanged();
        }
    }

    class SizesTask extends AsyncTask<String, Size, Void> {
        private static final String ADS_URL = "http://mallnet.me/shopping/read_sizes.php";

        @Override
        protected Void doInBackground(String... params) {
            String name = params[0];
            String id = params[1];

            try {
                URL url = new URL(ADS_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8")+"&"
                        +URLEncoder.encode("id", "UTF-8")+"="+URLEncoder.encode(id, "UTF-8");
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
                    Size size = new Size(jsonObj.getString("size"));
                    publishProgress(size);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            try {
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Size... values) {
            super.onProgressUpdate(values);
            sizes.add(values[0]);
            s.add(values[0].getSize());
            sizesAdapter.notifyDataSetChanged();
        }
    }

    class Size {
        private String size;

        Size(String size) {
            setSize(size);
        }

        public String getSize() {
            return size;
        }

        private void setSize(String size) {
            this.size = size;
        }
    }

    class ColorsTask extends AsyncTask<String, Color, Void> {
        private static final String ADS_URL = "http://mallnet.me/shopping/read_colors.php";

        @Override
        protected Void doInBackground(String... params) {
            String name = params[0];
            String id = params[1];

            try {
                URL url = new URL(ADS_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8")+"&"
                        +URLEncoder.encode("id", "UTF-8")+"="+URLEncoder.encode(id, "UTF-8");
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
                    Color colors = new Color(jsonObj.getString("color"));
                    publishProgress(colors);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            try {
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Color... values) {
            super.onProgressUpdate(values);
            colors.add(values[0]);
            c.add(values[0].getColor());
        }
    }

    class Color {
        private String color;

        Color(String color) {
            setColor(color);
        }

        public String getColor() {
            return color;
        }

        private void setColor(String color) {
            this.color = color;
        }
    }
}
