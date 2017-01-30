package com.app.drinktogo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.drinktogo.Entity.Customer;
import com.app.drinktogo.R;

import java.util.ArrayList;

/**
 * Created by Victor Rafols on 1/31/2017.
 */

public class CustomerAdapter extends BaseAdapter {

    ArrayList<Customer> customers = new ArrayList<Customer>();
    private LayoutInflater mInflater;

    public CustomerAdapter(Activity act) {
        mInflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return customers.size();
    }

    @Override
    public Customer getItem(int position) {
        return customers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView email;
        public TextView date_joined;
        public ImageView logo;
        public Customer customer;
    }

    public void addItem(final Customer friend) {
        customers.add(friend);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.friend_list_row, null);
            holder.name = (TextView)convertView.findViewById(R.id.request_description);
            holder.email = (TextView)convertView.findViewById(R.id.friend_email);
            holder.date_joined = (TextView)convertView.findViewById(R.id.date_joined);
            holder.logo = (ImageView) convertView.findViewById(R.id.friend_logo);
            holder.customer = null;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        Customer c = customers.get(position);
        holder.customer = c;
        holder.name.setText(c.full_name);
        holder.email.setText(c.email);
        holder.date_joined.setText(c.date_created);
        // TODO: set logo
        // holder.logo ??
        return convertView;
    }

}
