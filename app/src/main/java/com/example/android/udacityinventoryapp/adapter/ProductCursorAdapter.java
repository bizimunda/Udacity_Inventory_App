package com.example.android.udacityinventoryapp.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    public void bindView(View view, final Context context, final Cursor cursor) {

        final TextView tvName = (TextView) view.findViewById(R.id.product_name);
        final TextView tvQuantity = (TextView) view.findViewById(R.id.product_quantity);
        Button btnSale = (Button) view.findViewById(R.id.btn_listItem_sale);



        int idColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);

        int rowId = cursor.getInt(idColumnIndex);
        final String name = cursor.getString(nameColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);

        // Populate fields with extracted properties

        if (TextUtils.isEmpty(quantity)) {
            //quantity = context.getString(R.string.unknown_product);
        }
        tvName.setText(name);
        tvName.setTag(rowId);
        tvQuantity.setText(quantity);

        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                int sale = Integer.valueOf(tvQuantity.getText().toString());

                if (sale == 0) {
                    return;
                } else {
                    sale = --sale;
                }
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, sale);
                int rowid = (Integer) tvName.getTag();

                Uri currentItemUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, rowid);

                int rowsAffected = context.getContentResolver().update(currentItemUri, values, null, null);
            }
        });

    }


}
