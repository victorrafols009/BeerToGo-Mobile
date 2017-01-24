package com.app.drinktogo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.drinktogo.Entity.Friend;
import com.app.drinktogo.R;

import java.util.ArrayList;

/**
 * Created by Ken on 14/01/2017.
 */

public class FriendAdapter extends BaseAdapter {

    ArrayList<Friend> friends = new ArrayList<Friend>();
    private LayoutInflater mInflater;

    public FriendAdapter(Activity act) {
        mInflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Friend getItem(int position) {
        return friends.get(position);
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
        public Friend friend;
    }

    public void addItem(final Friend friend) {
        friends.add(friend);
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
            holder.friend = null;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        Friend f = friends.get(position);
        holder.friend = f;
        holder.name.setText(f.full_name);
        holder.email.setText(f.email);
        holder.date_joined.setText(f.date_created);
        // TODO: set logo
        // holder.logo ??
        return convertView;
    }
}
