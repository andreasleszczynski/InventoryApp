package com.example.android.inventoryappstage2;

/**
 * Copyright 2018 Andreas Leszczynski
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappstage2.data.InvContract.InvEntry;

import java.text.NumberFormat;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    // Identifier for inventory data loader
    private static final int EXISTING_PRODUCT_LOADER = 0;

    // Content URI
    private Uri mCurrentInvUri;

    // EditTexts
    private EditText mProductNameEditText;
    private EditText mProductPriceEditText;
    private EditText mProductQuantityEditText;
    private EditText mChangeProductQuantityEditText;
    private EditText mProductSupplierEditText;
    private EditText mProductSupplierPhoneEditText;
    private TextView mCurrencyTextView;

    // Keeps track if current inventory item changed
    private boolean mProductChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intentOnCreate = getIntent();
        mCurrentInvUri = intentOnCreate.getData();

        mProductNameEditText = findViewById(R.id.edit_text_product_name);
        mProductPriceEditText = findViewById(R.id.edit_text_product_price);
        mProductQuantityEditText = findViewById(R.id.edit_text_product_quantity);
        mChangeProductQuantityEditText = findViewById(R.id.edit_text_change_product_quantity);
        mProductSupplierEditText = findViewById(R.id.edit_text_product_supplier_name);
        mProductSupplierPhoneEditText = findViewById(R.id.edit_text_supplier_phone);
        mCurrencyTextView = findViewById(R.id.text_view_currency);

        Button mPlusButton = findViewById(R.id.button_plus);
        mPlusButton.setOnClickListener(this);
        Button mMinusButton = findViewById(R.id.button_minus);
        mMinusButton.setOnClickListener(this);
        Button mOrderButton = findViewById(R.id.button_order);
        mOrderButton.setOnClickListener(this);
        Button mDeleteButton = findViewById(R.id.button_delete);
        mDeleteButton.setOnClickListener(this);

        // sets the locale Currency in the TextView
        setDisplayCurrencyFormat();

        // Checks if it is a new or existing item
        if (mCurrentInvUri == null) {
            // change app bar to "Add a new Product"
            setTitle(getString(R.string.detail_activity_title_new_item));
            invalidateOptionsMenu();
            mDeleteButton.setVisibility(View.INVISIBLE);
        } else {
            // Change app bar to "Edit Product"
            setTitle(getString(R.string.detail_activity_title_edit_item));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mProductNameEditText.setOnTouchListener(mOnTouchListener);
        mProductPriceEditText.setOnTouchListener(mOnTouchListener);
        mProductQuantityEditText.setOnTouchListener(mOnTouchListener);
        mProductSupplierEditText.setOnTouchListener(mOnTouchListener);
        mProductSupplierPhoneEditText.setOnTouchListener(mOnTouchListener);
        mChangeProductQuantityEditText.setText("1");
    }

    // Sets the locale currency in the TextView
    private void setDisplayCurrencyFormat() {
        NumberFormat localCurrency = NumberFormat.getCurrencyInstance();
        mCurrencyTextView.setText(localCurrency.getCurrency().getSymbol());
    }

    // Sets the price for being displayed
    private String setDisplayPriceFormat(String amount) {
        return String.valueOf(Double.parseDouble(amount) / 100);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Defines the projection
        String[] projection = {
                InvEntry._ID,
                InvEntry.COLUMN_PRODUCT_NAME,
                InvEntry.COLUMN_PRODUCT_PRICE,
                InvEntry.COLUMN_PRODUCT_QUANTITY,
                InvEntry.COLUMN_PRODUCT_SUPPLIER,
                InvEntry.COLUMN_PRODUCT_SUPPLIER_PHONE};

        // Query method executed in a background thread
        return new CursorLoader(this,
                mCurrentInvUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Check if the cursor is null or less than 1 row
        if (data == null || data.getCount() < 1) {
            return;
        }

        // Reading data from the first row
        if (data.moveToFirst()) {
            // Find the columns of attributes
            int productNameColumnIndex = data.getColumnIndex(InvEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = data.getColumnIndex(InvEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = data.getColumnIndex(InvEntry.COLUMN_PRODUCT_QUANTITY);
            int productSupplierColumnIndex = data.getColumnIndex(InvEntry.COLUMN_PRODUCT_SUPPLIER);
            int productSupplierPhoneColumnIndex = data.getColumnIndex(InvEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

            // Get values
            String productName = data.getString(productNameColumnIndex);
            String productPrice = data.getString(productPriceColumnIndex);
            String productQuantity = data.getString(productQuantityColumnIndex);
            String productSupplier = data.getString(productSupplierColumnIndex);
            String productSupplierPhone = data.getString(productSupplierPhoneColumnIndex);

            // Update the views
            mProductNameEditText.setText(productName);
            mProductPriceEditText.setText(setDisplayPriceFormat(productPrice));
            mProductQuantityEditText.setText(productQuantity);
            mProductSupplierEditText.setText(productSupplier);
            mProductSupplierPhoneEditText.setText(productSupplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Clear all the data
        mProductNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mProductSupplierEditText.setText("");
        mProductSupplierPhoneEditText.setText("");
    }

    // Listens for user touches on a view
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductChanged = true;
            return false;
        }
    };

    // Save product in the database
    private void saveProduct() {
        // Get data from the EditText
        String productNameEditText = mProductNameEditText.getText().toString().trim();
        String productPriceEditText = mProductPriceEditText.getText().toString().trim();
        String productPrice = setDatabasePriceFormat(productPriceEditText);
        String productQuantityEditText = mProductQuantityEditText.getText().toString().trim();
        String productSupplierNameEditText = mProductSupplierEditText.getText().toString().trim();
        String productSupplierPhoneEditText = mProductSupplierPhoneEditText.getText().toString().trim();

        // checks if the fields are are blank or not digits only
        if ((TextUtils.isEmpty(productNameEditText) ||
                TextUtils.isEmpty(productPrice) || !TextUtils.isDigitsOnly(productPrice) ||
                TextUtils.isEmpty(productQuantityEditText) || !TextUtils.isDigitsOnly(productQuantityEditText) ||
                TextUtils.isEmpty(productSupplierNameEditText) ||
                TextUtils.isEmpty(productSupplierPhoneEditText) || !TextUtils.isDigitsOnly(productSupplierPhoneEditText))) {
            Toast.makeText(this, R.string.provide_information, Toast.LENGTH_LONG).show();
        } else {
            // Create a ContentValues object to save the values
            ContentValues contentValues = new ContentValues();
            contentValues.put(InvEntry.COLUMN_PRODUCT_NAME, productNameEditText);
            contentValues.put(InvEntry.COLUMN_PRODUCT_PRICE, productPrice);
            contentValues.put(InvEntry.COLUMN_PRODUCT_QUANTITY, productQuantityEditText);
            contentValues.put(InvEntry.COLUMN_PRODUCT_SUPPLIER, productSupplierNameEditText);
            contentValues.put(InvEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, productSupplierPhoneEditText);

            // Check if is a new or existing item
            if (mCurrentInvUri == null) {
                Uri uri = getContentResolver().insert(InvEntry.CONTENT_URI, contentValues);

                if (uri == null) {
                    Toast.makeText(this, getString(R.string.detail_insert_product_error),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.detail_insert_product_success,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsInvolved = getContentResolver().update(mCurrentInvUri, contentValues,
                        null, null);

                if (rowsInvolved == 0) {
                    Toast.makeText(this, R.string.detail_update_product_error,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.detail_product_updated, Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    // Converts the price to integer for the database
    private String setDatabasePriceFormat(String amount) {

        if (!TextUtils.isEmpty(amount)) {
            int databasePriceInt = (int) (Double.parseDouble(amount) * 100);
            return String.valueOf(databasePriceInt);
        } else {
            return "";
        }
    }

    // Inflates menu options, adds menu items to app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    // Updates the menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentInvUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    // User clicks on a menu option in the app bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveProduct();
                return true;
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // User clicks on the back button
    @Override
    public void onBackPressed() {
        if (!mProductChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Shows a dialog that there are unsaved changes
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Sets reaction for clicking on the positive or negative button of the dialog
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // deletes the product in the database
    private void deleteProduct() {
        if (mCurrentInvUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInvUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.detail_delete_product_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.detail_delete_product_success, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    // Determines what happens when user clicks on one of the buttons
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_plus:
                int mProductQuantityInt;
                int mChangeProductQuantityInt = 1;
                try {
                    if (TextUtils.isDigitsOnly(mProductQuantityEditText.getText().toString())
                            && !TextUtils.isEmpty(mProductQuantityEditText.getText().toString())) {
                        mProductQuantityInt = Integer.parseInt(mProductQuantityEditText.getText().toString());
                    } else {
                        mProductQuantityInt = 0;
                    }

                    mChangeProductQuantityInt = Integer.parseInt(mChangeProductQuantityEditText.getText().toString());

                    if (mChangeProductQuantityInt >= 0) {
                        mProductQuantityInt += mChangeProductQuantityInt;

                        mProductQuantityEditText.setText(String.valueOf(mProductQuantityInt));
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(DetailActivity.this, R.string.valid_number, Toast.LENGTH_SHORT).show();
                } finally {
                    break;
                }

            case R.id.button_minus:
                try {
                    if (TextUtils.isDigitsOnly(mProductQuantityEditText.getText().toString())
                            && !TextUtils.isEmpty(mProductQuantityEditText.getText().toString())) {
                        mProductQuantityInt = Integer.parseInt(mProductQuantityEditText.getText().toString());
                    } else {
                        mProductQuantityInt = 0;
                    }

                    mChangeProductQuantityInt = Integer.parseInt(mChangeProductQuantityEditText.getText().toString());

                    if (mChangeProductQuantityInt >= 0 && ((mProductQuantityInt - mChangeProductQuantityInt) >= 0)) {
                        mProductQuantityInt -= mChangeProductQuantityInt;

                        mProductQuantityEditText.setText(String.valueOf(mProductQuantityInt));
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(DetailActivity.this, R.string.valid_number, Toast.LENGTH_SHORT).show();
                } finally {
                    break;
                }

            case R.id.button_order:
                String phoneNumber = getString(R.string.tel) + mProductSupplierPhoneEditText.getText();
                Intent intentOrder = new Intent(Intent.ACTION_DIAL);
                intentOrder.setData(Uri.parse(phoneNumber));

                startActivity(intentOrder);
                break;

            case R.id.button_delete:
                showDeleteConfirmationDialog();

            default:
                break;
        }
    }
}
