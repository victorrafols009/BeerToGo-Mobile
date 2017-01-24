package com.app.drinktogo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.drinktogo.Entity.Inventory;
import com.app.drinktogo.Entity.Notification;
import com.app.drinktogo.R;

import java.util.ArrayList;

/**
 * Created by Victor Rafols on 1/24/2017.
 */

public class NotificationAdapter extends BaseAdapter {
    ArrayList<Notification> notifications = new ArrayList<Notification>();
    private LayoutInflater mInflater;

    public NotificationAdapter(Activity act) {
        mInflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Notification getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView request_description;
        public TextView request_date;
        public Notification notification;
    }

    public void addItem(final Notification item) {
        notifications.add(item);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.requests, null);
            holder.request_description = (TextView)convertView.findViewById(R.id.request_description);
            holder.request_date = (TextView)convertView.findViewById(R.id.request_date);
            holder.notification = null;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        Notification n = notifications.get(position);
        holder.notification = n;
        holder.request_description.setText(n.request_description);
        holder.request_date.setText(n.request_date);
        return convertView;
    }
}
