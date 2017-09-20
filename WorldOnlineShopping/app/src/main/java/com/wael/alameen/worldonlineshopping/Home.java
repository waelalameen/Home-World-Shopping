package com.wael.alameen.worldonlineshopping;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Timer;
import java.util.TimerTask;

public class Home extends Fragment {

    private List<String> images = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private List<String> ids = new ArrayList<>();
    private Slider adapter;
    private ViewPager viewPager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String image1, image2, image3, name1, name2, name3, id1, id2, id3;
    private ImageView one, two, three;
    private static final String phoneNumber = "07704227728";

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new HomeTask().execute();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK, getActivity().getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new HomeTask().execute();
            }
        });

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "Jannal.ttf");
        ((TextView) view.findViewById(R.id.text1)).setTypeface(typeface);
        ((TextView) view.findViewById(R.id.text2)).setTypeface(typeface);
        ((TextView) view.findViewById(R.id.text3)).setTypeface(typeface);
        one = (ImageView) view.findViewById(R.id.image_one);
        two = (ImageView) view.findViewById(R.id.image_two);
        three = (ImageView) view.findViewById(R.id.image_three);

        ((ImageView) view.findViewById(R.id.contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });

        ((ImageView) view.findViewById(R.id.share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "\nلا تفوت متعة التبضع مع عالم التسوق الالكتروني حمل التطبيق الان\n\n";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Santa Land");
                //String googlePlayLink = message + "https://play.google.com/store/apps/details?id=com.wael.alameen.santaland&hl=en\n\n";
                i.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(i, "choose one"));
            }
        });

        ((ImageView) view.findViewById(R.id.info)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
            }
        });

        viewPager = (ViewPager) view.findViewById(R.id.auto_slide);
        viewPager.setOffscreenPageLimit(4);
        adapter = new Slider(getContext());
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimer(), 10000, 10000);

        BasketDatabase database = new BasketDatabase(getContext());
        Cursor res = database.showALL();
        TextView marquee = (TextView) view.findViewById(R.id.marquee);
        marquee.setTypeface(typeface);
        marquee.setSelected(true);
        marquee.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        marquee.setSingleLine(true);
        if (res.getCount() == 0) {
            marquee.setText("                                             ليس لديك طلبيات حاليا في سلة المشتريات ");
        } else if (res.getCount() == 1) {
            marquee.setText("                                            لديك طلبية واحدة حاليا في سلة المشتريات ");
        } else {
            marquee.setText(" لديك " + res.getCount() + " طلبيات حاليا في سلة المشتريات                           ");
        }

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("name", name1);
                intent.putExtra("id", id1);
                intent.putExtra("sec", "noSce");
                startActivity(intent);
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("name", name2);
                intent.putExtra("id", id2);
                intent.putExtra("sec", "noSce");
                startActivity(intent);
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("name", name3);
                intent.putExtra("id", id3);
                intent.putExtra("sec", "noSce");
                startActivity(intent);
            }
        });
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
            Picasso.with(context).load(images.get(position)).error(R.drawable.app_icon).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra("name", names.get(position));
                    intent.putExtra("id", ids.get(position));
                    intent.putExtra("sec", "noSce");
                    startActivity(intent);
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    class MyTimer extends TimerTask {

        @Override
        public void run() {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() == 0) {
                            viewPager.setCurrentItem(1);
                        } else if (viewPager.getCurrentItem() == 1) {
                            viewPager.setCurrentItem(2);
                        } else if (viewPager.getCurrentItem() == 2) {
                            viewPager.setCurrentItem(3);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class HomeTask extends AsyncTask<Void, Void, Void> {

        private static final String ADS_URL = "http://mallnet.me/shopping/read_ads.php";

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
                    image1 = jsonObj.getString("img5");
                    name1 = jsonObj.getString("name5");
                    id1 = jsonObj.getString("id5");
                    image2 = jsonObj.getString("img6");
                    name2 = jsonObj.getString("name6");
                    id2 = jsonObj.getString("id6");
                    image3 = jsonObj.getString("img7");
                    name3 = jsonObj.getString("name7");
                    id3 = jsonObj.getString("id7");
                    images.add(0, jsonObj.getString("img1"));
                    images.add(1, jsonObj.getString("img2"));
                    images.add(2, jsonObj.getString("img3"));
                    images.add(3, jsonObj.getString("img4"));
                    names.add(0, jsonObj.getString("name1"));
                    names.add(1, jsonObj.getString("name2"));
                    names.add(2, jsonObj.getString("name3"));
                    names.add(3, jsonObj.getString("name4"));
                    ids.add(0, jsonObj.getString("id1"));
                    ids.add(1, jsonObj.getString("id2"));
                    ids.add(2, jsonObj.getString("id3"));
                    ids.add(3, jsonObj.getString("id4"));
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
            Picasso.with(getContext()).load(image1).resize(600, 300).onlyScaleDown().error(R.drawable.app_icon).into(one);
            Picasso.with(getContext()).load(image2).resize(600, 300).onlyScaleDown().error(R.drawable.app_icon).into(two);
            Picasso.with(getContext()).load(image3).resize(600, 300).onlyScaleDown().error(R.drawable.app_icon).into(three);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
