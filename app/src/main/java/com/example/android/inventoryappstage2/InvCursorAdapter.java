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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryappstage2.data.InvContract.InvEntry;

import java.text.NumberFormat;

// Adapter for a list view
public class InvCursorAdapter extends CursorAdapter {

    private Context mContext;

    // Constructor
    public InvCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
    }

    // Makes a new empty list item view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // Binds the product data to the list item layout
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        // Find views we want to use in listitem layout
        final TextView productNameTextView = (TextView) view.findViewById(R.id.text_view_product_name);
        final TextView productPriceTextView = (TextView) view.findViewById(R.id.text_view_price);
        final TextView productQuantityTextView = (TextView) view.findViewById(R.id.text_view_quantity);
        final Button sales = (Button) view.findViewById(R.id.listview_button_sale);

        // Find the columns of attributes
        int productIDColumnIndex = cursor.getColumnIndex(InvEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_QUANTITY);

        // Get attributes for the current product
        String productName = cursor.getString(productNameColumnIndex);
        String productPrice = cursor.getString(productPriceColumnIndex);
        final String productQuantity = cursor.getString(productQuantityColumnIndex);
        final String productID = cursor.getString(productIDColumnIndex);

        // Formats the displayed product price
        String formattedProductPrice = setDisplayPriceFormat(productPrice);

        // Update the TextViews
        productNameTextView.setText(productName);
        productPriceTextView.setText(formattedProductPrice);
        productQuantityTextView.setText(productQuantity);

        sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reduceQuantity(productQuantity, productID);
            }
        });
    }

    // Reduces the quantity of the products
    private void reduceQuantity(String productQuantity, String id) {
        int intProductQuantity = Integer.parseInt(productQuantity);

        if (intProductQuantity >= 1) {
            intProductQuantity--;

            ContentValues contentValues = new ContentValues();
            contentValues.put(InvEntry.COLUMN_PRODUCT_QUANTITY, intProductQuantity);

            Uri currentInvUri = ContentUris.withAppendedId(InvEntry.CONTENT_URI, Integer.parseInt(id));

            int rowsUpdated = mContext.getContentResolver().update(currentInvUri, contentValues, null, null);

            if (rowsUpdated == 0) {
                Log.i("LOG_TAG_InvCursorAdapt", "Error updating product");
            } else {
                Log.i("LOG_TAG_InvCursorAdapt", "product updated");
            }
        } else {
            Log.i("LOG_TAG_InvCursorAdapt", "Quantity is already 0");
        }
    }

    // Sets the displayed product
    private String setDisplayPriceFormat(String amount) {
        try {
            NumberFormat localCurrency = NumberFormat.getCurrencyInstance();
            return localCurrency.format(Double.parseDouble(amount) / 100);
        } catch (NumberFormatException e) {
            Log.e("LOG_TAG_InvCursorAdapt", "setDisplayPriceFormat: ", e);
        }
        return "";
    }
}
