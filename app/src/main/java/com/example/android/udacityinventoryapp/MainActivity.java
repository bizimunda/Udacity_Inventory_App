package com.example.android.udacityinventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.udacityinventoryapp.adapter.ProductCursorAdapter;
import com.example.android.udacityinventoryapp.data.ProductContract;
import com.example.android.udacityinventoryapp.data.ProductDbHelper;
import com.shamanland.fab.FloatingActionButton;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ProductDbHelper mDbHelper;
    private ListView productListView;
    private ProductCursorAdapter adapter;
    private static final int PRODUCT_LOADER =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        productListView= (ListView) findViewById(R.id.list);
        adapter= new ProductCursorAdapter(this,null);
        productListView.setAdapter(adapter);




        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(MainActivity.this, AddActivity.class);
                Uri currentUri= ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });


        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_insert_dummy_data) {
            insertProduct();
            //Intent intent= new Intent(MainActivity.this, AddActivity.class);
            //startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void insertProduct() {
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, "iPhone");
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY ,"45");
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,"700");


        Uri newUri=getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "failed",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "success",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection={
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY
        };
        return new CursorLoader(this,
                ProductContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        adapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);

    }


}
