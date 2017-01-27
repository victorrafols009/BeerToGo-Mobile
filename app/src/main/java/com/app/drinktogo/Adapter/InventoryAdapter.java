package com.app.drinktogo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.drinktogo.Entity.Inventory;
import com.app.drinktogo.R;

import java.util.ArrayList;

/**
 * Created by Ken on 14/01/2017.
 */

public class InventoryAdapter extends BaseAdapter {

    ArrayList<Inventory> inventories = new ArrayList<Inventory>();
    private LayoutInflater mInflater;

    public InventoryAdapter(Activity act) {
        mInflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return inventories.size();
    }

    @Override
    public Inventory getItem(int position) {
        return inventories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView brand;
        public TextView store;
        public TextView qty;
        public ImageView logo;
        public Inventory inventory;
    }

    public void addItem(final Inventory inventory) {
        inventories.add(inventory);
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
            holder.store = (TextView)convertView.findViewById(R.id.store);
            holder.qty = (TextView)convertView.findViewById(R.id.item_qty);
            holder.logo = (ImageView) convertView.findViewById(R.id.item_logo);
            holder.inventory = null;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        Inventory i = inventories.get(position);
        holder.inventory = i;
        holder.name.setText(i.name);
        holder.brand.setText(i.brand);
        holder.store.setText(i.store_name + " @ " + i.store_address);
        holder.qty.setText("");
        // TODO: set logo
        // holder.logo ??
        return convertView;
    }
}
