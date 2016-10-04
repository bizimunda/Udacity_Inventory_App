package com.example.android.udacityinventoryapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.udacityinventoryapp.R;
import com.example.android.udacityinventoryapp.data.ProductContract;

/**
 * Created by temp on 4/10/2016.
 */
public class ProductCursorAdapter extends CursorAdapter {


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvName = (TextView) view.findViewById(R.id.product_name);
        TextView tvQuantity = (TextView) view.findViewById(R.id.product_quantity);
        // Extract properties from cursor

        int nameColumnIndex=cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex=cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);

        // Populate fields with extracted properties

        if (TextUtils.isEmpty(quantity)) {
            quantity = context.getString(R.string.unknown_product);
        }
        tvName.setText(name);
        tvQuantity.setText(quantity);

    }
}
