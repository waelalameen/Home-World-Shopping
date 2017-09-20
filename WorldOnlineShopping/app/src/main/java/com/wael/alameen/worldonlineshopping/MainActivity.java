package com.wael.alameen.worldonlineshopping;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    private List<NavItems> items = new ArrayList<>();
    private BasketDatabase database;
    private static final String phoneNumber = "07704227728";
    private static boolean isReachable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(typeface);

        //call network

        if (!isInternetAccessible()) {
            showNoInternetWarning();
        }

        database = new BasketDatabase(this);

        FragmentPager fragmentPager = new FragmentPager(getSupportFragmentManager());
        fragmentPager.add(new Home(), "الرئيسية");
        fragmentPager.add(new Sections(), "الاقسام");

        ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setPageTransformer(true, new DepthPagerTransformer());
        viewPager.setAdapter(fragmentPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_shopping_basket_black_24dp);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            }
        });

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        for (int i = 0; i < vg.getChildCount(); i++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(i);
            for (int j = 0; j < vgTab.getChildCount(); j++) {
                View tabViewChild = vgTab.getChildAt(j);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BasketActivity.class));
            }
        });

        int count = 0;
        NavItems navItems;
        String[] names = getResources().getStringArray(R.array.item_names);
        for (String name : names) {
            if (count == 0) {
                navItems = new NavItems(name, R.drawable.ic_person_add_black_24dp);
                items.add(navItems);
                count++;
            } else if (count == 1) {
                navItems = new NavItems(name, R.drawable.ic_local_offer_black_24dp);
                items.add(navItems);
                count++;
            } else if (count == 2) {
                navItems = new NavItems(name, R.drawable.ic_local_shipping_black_24dp);
                items.add(navItems);
                count++;
            } else if (count == 3) {
                navItems = new NavItems(name, R.drawable.ic_share_black_24dp);
                items.add(navItems);
                count++;
            } else {
                navItems = new NavItems(name, R.drawable.ic_stay_current_portrait_black_24dp);
                items.add(navItems);
                count++;
            }
        }

        RecyclerView recyclerViewNavMenu = (RecyclerView) findViewById(R.id.nav);
        recyclerViewNavMenu.setHasFixedSize(true);
        recyclerViewNavMenu.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter navAdapter = new NavMenuAdapter(this, items, this);
        recyclerViewNavMenu.setAdapter(navAdapter);
        navAdapter.notifyDataSetChanged();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        Cursor cursor = database.showALL();
        if (cursor.getCount() > 0) {
            Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.before_exit, null);
            builder.setView(view);
            builder.setCancelable(false);
            final Dialog dialog = builder.create();
            dialog.setContentView(view);
            TextView hint = (TextView) dialog.findViewById(R.id.hint);
            hint.setTypeface(typeface);
            if (cursor.getCount() == 1) {
                hint.setText(" لديك حاليا طلبية واحدة في سلة المشتريات لم تقم بتاكيد شرائها، هل انت متاكد من انك تريد الخروج من التطبيق ؟ ");
            } else if (cursor.getCount() == 2) {
                hint.setText(" لديك حاليا طلبيتان في سلة المشتريات لم تقم بتاكيد شرائها، هل انت متاكد من انك تريد الخروج من التطبيق ؟ ");
            } else {
                hint.setText(" لديك حاليا " + cursor.getCount() + " طلبيات في سلة المشتريات لم تقم بتاكيد شرائها، هل انت متاكد من انك تريد الخروج من التطبيق ؟ ");
            }

            Button cancel = (Button) dialog.findViewById(R.id.cancel);
            cancel.setTypeface(typeface);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            Button exit = (Button) dialog.findViewById(R.id.exit);
            exit.setTypeface(typeface);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveTaskToBack(true);
                }
            });
            dialog.show();
        } else {
            super.onBackPressed();
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInternetAccessible()) {
            showNoInternetWarning();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(MainActivity.this, LogInActivity.class));
                break;
            case 1:
                startActivity(new Intent(MainActivity.this, LogosActivity.class));
                break;
            case 2:
                startActivity(new Intent(MainActivity.this, DeliveryActivity.class));
                break;
            case 3:
                share();
                break;
            case 4:
                showAbout();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void showAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.about_layout, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setContentView(view);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        ((TextView) dialog.findViewById(R.id.first_hint)).setTypeface(typeface);
        ((TextView) dialog.findViewById(R.id.second_hint)).setTypeface(typeface);
        ((TextView) dialog.findViewById(R.id.hint)).setTypeface(typeface);
        Button face = (Button) dialog.findViewById(R.id.face);
        face.setTypeface(typeface);
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String faceLink = "https://www.facebook.com/worldonlineshopping9/";
                Uri uri = Uri.parse(faceLink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Button num = (Button) dialog.findViewById(R.id.number);
        num.setTypeface(typeface);
        num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });
        dialog.show();
    }

//    private void contactUs() {
//        String faceLink = "https://www.facebook.com/SantalandGifts/?fref=ts";
//        Uri uri = Uri.parse(faceLink);
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);
//    }

    private void share() {
        String message = "\nلا تفوت متعة التبضع مع عالم التسوق الالكتروني حمل التطبيق الان\n\n";
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Santa Land");
        //String googlePlayLink = message + "https://play.google.com/store/apps/details?id=com.wael.alameen.santaland&hl=en\n\n";
        i.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(i, "choose one"));
    }

    private boolean isWiFiAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo active = connectivityManager.getActiveNetworkInfo();

        if (active != null) {
            if (active.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (active.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }

        return false;
    }

    private boolean isInternetAccessible() {
        if (isWiFiAvailable()) {
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        URL url = new URL("http://www.google.com");
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setConnectTimeout(3000);
                        httpURLConnection.connect();
                        if (httpURLConnection.getResponseCode() == 200) {
                            return true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    isReachable = result;
                }
            }.execute();
        } else {
            return false;
        }

        return isReachable;
    }

    private void showNoInternetWarning() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.no_internet_layout, null);
        builder.setCancelable(false);
        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.setContentView(view);
        ((TextView) dialog.findViewById(R.id.h1)).setTypeface(typeface);
        ((TextView) dialog.findViewById(R.id.h2)).setTypeface(typeface);
        Button cancel = (Button) dialog.findViewById(R.id.retry);
        cancel.setTypeface(typeface);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
        dialog.show();
    }
}
