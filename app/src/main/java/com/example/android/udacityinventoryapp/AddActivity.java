package com.example.android.udacityinventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.udacityinventoryapp.data.ProductContract;

import java.io.ByteArrayOutputStream;


public class AddActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnTouchListener {

    private static final int EXISTING_PET_LOADER = 1;
    private static final int PRODUCT_LOADER = 1;
    private static final int FILE_SELECT_CODE = 2;
    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;

    private Uri mCurrentProductUri;
    private Button btnImageUpload;
    private Button btnEmail;
    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle("Add a pet");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit a pet");
            getSupportLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);

        }



        mNameEditText = (EditText) findViewById(R.id.et_addActivity_name);
        mQuantityEditText = (EditText) findViewById(R.id.et_addActivity_quantity);
        mPriceEditText = (EditText) findViewById(R.id.et_addActivity_price);
        btnImageUpload=(Button)findViewById(R.id.btn_addActivity_imageUpload);
        btnEmail=(Button)findViewById(R.id.btn_addActivity_email);
        imageView=(ImageView)findViewById(R.id.iv_addActivity_picture);

        mNameEditText.setOnTouchListener(this);
        mQuantityEditText.setOnTouchListener(this);
        mPriceEditText.setOnTouchListener(this);

        btnImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonImageClick();
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderItem();
            }
        });


    }

    private void orderItem() {
        String subject = "Product Order ";
        String message = "Product Name: " + mNameEditText.getText() +
                "\nProduct Price: " + mPriceEditText.getText() +
                "\nNumber of stocks To be ordered: " + mQuantityEditText.getText();
        String[] emails = {"info@amazon.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void buttonImageClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri imageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                    imageView.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
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

        if (id == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }
        if (id == R.id.action_save) {
            saveProduct();
            finish();
            return true;
        }

        if (id == R.id.action_sell) {
            sellItem();
            finish();
            return true;
        }

        if (id == R.id.action_receive) {
            receiveItem();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE
        };

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String price = cursor.getString(priceColumnIndex);

            mNameEditText.setText(name);
            mQuantityEditText.setText(quantity);
            mPriceEditText.setText(price);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(priceString)) {
            Toast.makeText(this,"Please fill out all values", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.valueOf(quantityString);
        double price = Double.valueOf(priceString);

        if(quantity < 0) {
            Toast.makeText(this,"You must input a real number for the count field.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(price < 0.0) {
            Toast.makeText(this,"You must input a real price.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(imageView.getDrawable() == null) {
            Toast.makeText(this,"You must upload an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap imageBitMap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imageBitMap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] imageByteArray = bos.toByteArray();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, imageByteArray);


        if (mCurrentProductUri == null){
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, "successfully added", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            //Show a toast message depending on whether or not the update was successful
            if (rowsAffected == 0) {
                Toast.makeText(this, "update failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "update successful", Toast.LENGTH_SHORT).show();
                //id = ProductContract.ProductEntry.getIdFromUri(mCurrentItemUri);
            }
        }

    }

    private void sellItem() {

        ContentValues values = new ContentValues();

        int sell = Integer.valueOf(mQuantityEditText.getText().toString());


        if (sell == 0) {
            return;
        } else {
            sell = sell - 1;
        }

        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, sell);

        getContentResolver().update(mCurrentProductUri, values, null, null);

        Toast.makeText(this, "successfully sold", Toast.LENGTH_SHORT).show();
    }

    private void receiveItem() {

        ContentValues values = new ContentValues();

        int receive = Integer.valueOf(mQuantityEditText.getText().toString());

        receive = receive + 1;

        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, receive);

        getContentResolver().update(mCurrentProductUri, values, null, null);

        Toast.makeText(this, "Item successfully received", Toast.LENGTH_SHORT).show();


    }
}
