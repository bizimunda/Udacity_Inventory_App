package com.example.android.udacityinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by temp on 4/10/2016.
 */
public class ProductProvider extends ContentProvider {


    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }


    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Check that the name is not null
        String name = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the count is non-negative
        Integer count = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (count == null || count < 0) {
            throw new IllegalArgumentException("Product requires a count greater than or equal to 0");
        }

        // Check that the price is non-negative
        Double price = contentValues.getAsDouble(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price < 0.00) {
            throw new IllegalArgumentException("Product requires price not negative");
        }

        // Check that the image is not null
        byte[] image = contentValues.getAsByteArray(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Product requires image to be set");
        }

        long id = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, contentValues);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:

                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer gender = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);

        }


        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of database rows affected by the update statement
        int rowsUpdated= database.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if(rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted= database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted =database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
