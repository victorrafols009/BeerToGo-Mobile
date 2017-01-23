package com.app.drinktogo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.drinktogo.Entity.Store;
import com.app.drinktogo.R;

import java.util.ArrayList;

/**
 * Created by Ken on 14/01/2017.
 */

public class StoreAdapter extends BaseAdapter {

    ArrayList<Store> stores = new ArrayList<Store>();
    private LayoutInflater mInflater;

    public StoreAdapter(Activity act) {
        mInflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return stores.size();
    }

    @Override
    public Store getItem(int position) {
        return stores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView address;
        public TextView contact;
        public ImageView logo;
        public Store store;
    }

    public void addItem(final Store item) {
        stores.add(item);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.store_row, null);
            holder.name = (TextView)convertView.findViewById(R.id.store_name);
            holder.address = (TextView)convertView.findViewById(R.id.store_address);
            holder.contact = (TextView)convertView.findViewById(R.id.store_contact);
            holder.logo = (ImageView) convertView.findViewById(R.id.store_logo);
            holder.store = null;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        Store s = stores.get(position);
        holder.store = s;
        holder.name.setText(s.name);
        holder.address.setText(s.address);
        holder.contact.setText(s.contact);
        // TODO: set logo
        // holder.logo ??
        return convertView;
    }
}
