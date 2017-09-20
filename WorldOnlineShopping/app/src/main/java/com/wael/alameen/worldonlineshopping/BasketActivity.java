package com.wael.alameen.worldonlineshopping;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BasketActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private RecyclerView.Adapter adapter;
    private List<Basket> basketList = new ArrayList<>();
    private MenuItem trash;
    private BasketDatabase database;
    private static int  numOfChecks = 0, pos = 0;
    private int mSum = 0;
    private TextView title, sum;
    private RequestItemsDatabase requestItemsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(typeface);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        database = new BasketDatabase(this);
        Cursor result = database.showALL();

        requestItemsDatabase = new RequestItemsDatabase(this);

        if (result.getCount() > 0) {
            while (result.moveToNext()) {
                Basket basket = new Basket(result.getString(0), result.getString(1), result.getString(2), result.getString(3),
                        result.getString(4), result.getString(5), result.getString(6), result.getString(7));
                basketList.add(basket);
            }
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.orders_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BasketAdapter(this, basketList, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        sum = (TextView) findViewById(R.id.sum);
        sum.setTypeface(typeface);

        mSum = 0;

        for (Basket basket : basketList) {
            int p = Integer.parseInt(basket.getPrice());
            int n = Integer.parseInt(basket.getNumber());
            if (n > 1) {
                p = p * n;
            }
            mSum = mSum + p;
        }

        if (mSum != 0) {
            sum.setText(" المجموع الكلي = " + Integer.toString(mSum) + " د.ع ");
        } else {
            sum.setText(" المجموع الكلي = 0 د.ع ");
        }

        Button finalSend = (Button) findViewById(R.id.final_send);
        finalSend.setTypeface(typeface);
        finalSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BasketActivity.this, FinalActivity.class);
                for (Basket basket : basketList) {
                    requestItemsDatabase.insert(basket.getName(), basket.getPrice(), basket.getLogo(), basket.getColor(), basket.getSize(), basket.getNumber(),
                            basket.getSection());
                }
                intent.putExtra("sum", Integer.toString(mSum));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.basket_menu, menu);
        trash = menu.findItem(R.id.action_delete);
        trash.setVisible(false);
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
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_delete);
        if (basketList.get(position).isSelected()) {

        } else {
            trash.setVisible(false);
            checkBox.setChecked(false);
        }
    }

    class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ViewHolder> {
        private Context context;
        private List<Basket> basketList;
        private AdapterView.OnItemClickListener onItemClickListener;

        BasketAdapter(Context context, List<Basket> basketList, AdapterView.OnItemClickListener onItemClickListener) {
            this.context = context;
            this.basketList = basketList;
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.basket_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Basket basket = basketList.get(position);
            holder.orderName.setText(String.format("الاسم : %s", basketList.get(position).getName()));
            holder.orderPrice.setText(String.format("السعر : %s", basketList.get(position).getPrice()));
            holder.orderNumber.setText(basketList.get(position).getNumber());
            Picasso.with(context).load(basketList.get(position).getImage()).resize(600, 300).onlyScaleDown().error(R.drawable.app_icon).into(holder.orderImage);
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.checkBox.isChecked()) {
                        basket.setSelected(true);
                        holder.checkBox.setChecked(true);
                        numOfChecks++;
                    } else {
                        basket.setSelected(false);
                        holder.checkBox.setChecked(false);
                        numOfChecks--;
                    }

                    if (numOfChecks > 0 && holder.checkBox.isSelected()) {
                        title.setText("");
                        title.setText(" حذف " + Integer.toString(numOfChecks) + " مواد من السلة ");
                        trash.setVisible(true);
                        trash.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                database.delete(basketList.get(pos).getName());
                                basketList.remove(pos);
                                adapter.notifyDataSetChanged();
                                numOfChecks--;
                                mSum = mSum - Integer.parseInt(basket.getPrice());
                                sum.setText(" المجموع الكلي = " + Integer.toString(mSum) + " د.ع ");
                                title.setText("");
                                title.setText(R.string.basket);
                                return true;
                            }
                        });
                    } else {
                        title.setText("");
                        title.setText(R.string.basket);
                        trash.setVisible(false);
                        numOfChecks = 0;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return basketList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
            TextView orderName, orderPrice, orderNumber;
            ImageView orderImage;
            CheckBox checkBox;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnLongClickListener(this);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
                orderName = (TextView) itemView.findViewById(R.id.order_name);
                orderName.setTypeface(typeface);
                orderPrice = (TextView) itemView.findViewById(R.id.order_price);
                orderPrice.setTypeface(typeface);
                orderNumber = (TextView) itemView.findViewById(R.id.order_number);
                orderNumber.setTypeface(typeface);
                orderImage = (ImageView) itemView.findViewById(R.id.order_image);
                checkBox = (CheckBox) itemView.findViewById(R.id.check_delete);
                checkBox.setVisibility(View.INVISIBLE);
            }

            @Override
            public boolean onLongClick(View v) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setSelected(true);
                notifyItemChanged(pos);
                pos = getLayoutPosition();
                notifyItemChanged(pos);
                onItemClickListener.onItemClick(null, v, getLayoutPosition(), getItemId());
                return false;
            }
        }
    }

    class Basket {
        private String name, price, number, section;
        private String image, logo, color, size;
        private boolean selected;

        Basket(String name, String price, String image, String logo, String color, String size, String number, String section) {
            setName(name);
            setPrice(price);
            setImage(image);
            setLogo(logo);
            setColor(color);
            setSize(size);
            setNumber(number);
            setSection(section);
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

        public String getLogo() {
            return logo;
        }

        private void setLogo(String logo) {
            this.logo = logo;
        }

        public String getColor() {
            return color;
        }

        private void setColor(String color) {
            this.color = color;
        }

        public String getSize() {
            return size;
        }

        private void setSize(String size) {
            this.size = size;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getNumber() {
            return number;
        }

        private void setNumber(String number) {
            this.number = number;
        }

        public String getSection() {
            return section;
        }

        private void setSection(String section) {
            this.section = section;
        }
    }
}
