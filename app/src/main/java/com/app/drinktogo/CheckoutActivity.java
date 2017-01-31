package com.app.drinktogo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.drinktogo.Adapter.CheckoutItemAdapter;
import com.app.drinktogo.Entity.CheckoutItem;
import com.app.drinktogo.helper.AppConfig;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;

/**
 * Created by Victor Rafols on 1/31/2017.
 */

public class CheckoutActivity extends AppCompatActivity {
    CheckoutItemAdapter checkoutItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_page);

        checkoutItemAdapter = new CheckoutItemAdapter(CheckoutActivity.this);

        final ArrayList<Integer> cart_id = (ArrayList<Integer>) getIntent().getSerializableExtra("cart_id");
        ArrayList<String> cart_name = (ArrayList<String>) getIntent().getSerializableExtra("cart_name");
        ArrayList<Integer> cart_amount = (ArrayList<Integer>) getIntent().getSerializableExtra("cart_amount");

        int total_amount = 0;
        for(int i=0; i < cart_id.size(); i++) {
            CheckoutItem checkoutItem = new CheckoutItem();
            checkoutItem.id = cart_id.get(i);
            checkoutItem.item_name = cart_name.get(i);
            checkoutItem.amount = cart_amount.get(i);
            total_amount += cart_amount.get(i);
            checkoutItemAdapter.addItem(checkoutItem);
        }

        ListView lv = (ListView) findViewById(R.id.checkout_list);

        lv.addHeaderView(View.inflate(getApplicationContext(), R.layout.checkout_header, null));

        View footer = View.inflate(getApplicationContext(), R.layout.checkout_footer, null);

        TextView total = (TextView) footer.findViewById(R.id.checkout_total);
        total.setText("" + total_amount);

        Button checkout = (Button) footer.findViewById(R.id.checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent intent = new Intent(CheckoutActivity.this, PurchaseActivity.class);
                                intent.putExtra("user_id", getIntent().getStringExtra("user_id"));
                                intent.putExtra("items_id", cart_id);
                                intent.putExtra("store_id", getIntent().getStringExtra("store_id"));
                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
                builder.setTitle("Puchase Confirmation")
                        .setMessage("Is your purchase final?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();

            }
        });

        lv.addFooterView(footer);

        lv.setAdapter(checkoutItemAdapter);
    }
}
