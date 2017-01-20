package com.app.beertogo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.beertogo.Entity.Item;
import com.app.beertogo.R;

import java.util.ArrayList;

/**
 * Created by Ken on 14/01/2017.
 */

public class ItemAdapter extends BaseAdapter {

    ArrayList<Item> items = new ArrayList<Item>();
    private LayoutInflater mInflater;

    public ItemAdapter(Activity act) {
        mInflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView brand;
        public TextView qty;
        public ImageView logo;
        public Item item;
    }

    public void addItem(final Item item) {
        items.add(item);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_row, null);
            holder.name = (TextView)convertView.findViewById(R.id.item_name);
            holder.brand = (TextView)convertView.findViewById(R.id.item_brand);
            holder.qty = (TextView)convertView.findViewById(R.id.item_qty);
            holder.logo = (ImageView) convertView.findViewById(R.id.item_logo);
            holder.item = null;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        Item i = items.get(position);
        holder.item = i;
        holder.name.setText(i.name);
        holder.brand.setText(i.brand);
        holder.qty.setText("Quantity: " + i.qty);
        // TODO: set logo
        // holder.logo ??
        return convertView;
    }
}
