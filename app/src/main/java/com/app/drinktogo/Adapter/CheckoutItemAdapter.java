package com.app.drinktogo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.drinktogo.Entity.CheckoutItem;
import com.app.drinktogo.R;

import java.util.ArrayList;

/**
 * Created by Victor Rafols on 1/31/2017.
 */

public class CheckoutItemAdapter extends BaseAdapter {
    ArrayList<CheckoutItem> checkoutItemArrayList = new ArrayList<CheckoutItem>();
    private LayoutInflater mInflater;

    public CheckoutItemAdapter(Activity act) {
        mInflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return checkoutItemArrayList.size();
    }

    @Override
    public CheckoutItem getItem(int position) {
        return checkoutItemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView item_name;
        public TextView amount;
        public CheckoutItem checkoutItem;
    }

    public void addItem(final CheckoutItem friend) {
        checkoutItemArrayList.add(friend);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.checkout_row, null);
            holder.item_name = (TextView)convertView.findViewById(R.id.checkout_item_name);
            holder.amount = (TextView)convertView.findViewById(R.id.checkout_subtotal);
            holder.checkoutItem = null;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        CheckoutItem c = checkoutItemArrayList.get(position);
        holder.checkoutItem = c;
        holder.item_name.setText(c.item_name);
        holder.amount.setText("" + c.amount);
        return convertView;
    }

}
