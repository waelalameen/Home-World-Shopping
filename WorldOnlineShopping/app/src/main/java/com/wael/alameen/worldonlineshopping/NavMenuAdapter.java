package com.wael.alameen.worldonlineshopping;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NavMenuAdapter extends RecyclerView.Adapter<NavMenuAdapter.ViewHolder> {
    private List<NavItems> items = new ArrayList<>();
    private AdapterView.OnItemClickListener onItemClickListener;
    private Context context;
    private static int selectPos = 0;

    NavMenuAdapter(Context context, List<NavItems> items, AdapterView.OnItemClickListener onItemClickListener) {
        LayoutInflater.from(context);
        this.context = context;
        this.items = items;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_menu_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemName.setText(items.get(position).getName());
        holder.itemIcon.setImageResource(items.get(position).getIcon());

        if (selectPos == position) {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.itemView.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemName;
        ImageView itemIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Jannal.ttf");
            itemName.setTypeface(typeface);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(null, v, getLayoutPosition(), getItemId());
            notifyItemChanged(selectPos);
            selectPos = getLayoutPosition();
            itemView.setBackgroundColor(Color.GRAY);
            notifyItemChanged(selectPos);
        }
    }
}
