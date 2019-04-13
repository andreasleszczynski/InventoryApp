package com.example.android.inventoryappstage2.data;

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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryappstage2.data.InvContract.InvEntry;


/**
 * ContentProvider for Inventory app
 */
public class InvProvider extends ContentProvider {

    // Helper object
    private InvDbHelper mInvDbHelper;

    // URI matcher code for the whole inventory table
    private static final int INV = 10;

    // URI matcher code for one inventory item in the inventory table
    private static final int INV_ID = 11;

    // To match content URI and corresponding code
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer
    static {
        mUriMatcher.addURI(InvContract.CONTENT_AUTHORITY, InvContract.PATH_INV, INV);
        mUriMatcher.addURI(InvContract.CONTENT_AUTHORITY, InvContract.PATH_INV + "/#", INV_ID);
    }

    // Initialize provider and helper object
    @Override
    public boolean onCreate() {
        mInvDbHelper = new InvDbHelper(getContext());
        return true;
    }

    // Perform query for given URI
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase database = mInvDbHelper.getReadableDatabase();
        Cursor cursor;

        // Find out if URI matcher matches a specific code
        int match = mUriMatcher.match(uri);
        switch (match) {
            case INV:
                // query the complete inventory table
                cursor = database.query(InvEntry.TABLE_NAME, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INV_ID:
                // set selection to a specific ID
                selection = InvEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // query the specific inventory object
                cursor = database.query(InvEntry.TABLE_NAME, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Can't query unknown URI " + uri);
        }
        // Set notification URI on cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    // Returns MIME type of data
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case INV:
                return InvEntry.CONTENT_LIST_TYPE;
            case INV_ID:
                return InvEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Match: " + match + "has unknown URI" + uri);
        }
    }

    // Insert new data into the provider
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case INV:
                return insertInv(uri, values);
            default:
                throw new IllegalArgumentException("Insert is not supported for " + uri);
        }
    }

    // Insert single inventory object, return URI with specific row in the db
    private Uri insertInv(Uri uri, ContentValues values) {

        String productName = values.getAsString(InvEntry.COLUMN_PRODUCT_NAME);
        String productPrice = values.getAsString(InvEntry.COLUMN_PRODUCT_PRICE);
        String productQuantity = values.getAsString(InvEntry.COLUMN_PRODUCT_QUANTITY);
        String productSupplier = values.getAsString(InvEntry.COLUMN_PRODUCT_SUPPLIER);
        String productSupplierPhone = values.getAsString(InvEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

        // Check if values are valid
        if (productName == null) {
            throw new IllegalArgumentException("Product needs a name");
        } else if (productPrice == null) {
            throw new IllegalArgumentException("Product needs a price");
        } else if (productQuantity == null) {
            throw new IllegalArgumentException("Product needs a quantity");
        } else if (productSupplier == null) {
            throw new IllegalArgumentException("Product needs a supplier");
        } else if (productSupplierPhone == null) {
            throw new IllegalArgumentException("Supplier needs a phone number");
        }

        SQLiteDatabase database = mInvDbHelper.getWritableDatabase();

        // Insert new inventory object values
        long rowID = database.insert(InvEntry.TABLE_NAME, null, values);

        // Check if insertion was successful
        if (rowID == -1) {
            Log.e("InvProvider: ", "Error when inserting row for " + uri);
            return null;
        }

        // Notify all listeners that data changed
        getContext().getContentResolver().notifyChange(uri, null);

        // Return new URI with appended row ID
        return ContentUris.withAppendedId(uri, rowID);
    }

    // deletes one or all objects
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mInvDbHelper.getWritableDatabase();

        int rowsDeleted;
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case INV:
                rowsDeleted = database.delete(InvEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INV_ID:
                selection = InvEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InvEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }

        // If rows were deleted notify all listeners that URI changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    // Updates the data
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case INV:
                return updateInv(uri, values, selection, selectionArgs);
            case INV_ID:
                selection = InvEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInv(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // Updates the Inventory
    private int updateInv(Uri uri, ContentValues values,
                          String selection, String[] selectionArgs) {
        if (values.containsKey(InvEntry.COLUMN_PRODUCT_NAME)) {
            String productName = values.getAsString(InvEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Product needs a name");
            }
        }

        if (values.containsKey(InvEntry.COLUMN_PRODUCT_PRICE)) {
            String productPrice = values.getAsString(InvEntry.COLUMN_PRODUCT_PRICE);
            if (productPrice == null) {
                throw new IllegalArgumentException("Product needs a price");
            }
        }

        if (values.containsKey(InvEntry.COLUMN_PRODUCT_QUANTITY)) {
            String productQuantity = values.getAsString(InvEntry.COLUMN_PRODUCT_QUANTITY);
            if (productQuantity == null) {
                throw new IllegalArgumentException("Product needs a quantity");
            }
        }

        if (values.containsKey(InvEntry.COLUMN_PRODUCT_SUPPLIER)) {
            String productSupplier = values.getAsString(InvEntry.COLUMN_PRODUCT_SUPPLIER);
            if (productSupplier == null) {
                throw new IllegalArgumentException("Product needs a supplier");
            }
        }

        if (values.containsKey(InvEntry.COLUMN_PRODUCT_SUPPLIER_PHONE)) {
            String productSupplierPhone = values.getAsString(InvEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
            if (productSupplierPhone == null) {
                throw new IllegalArgumentException("Supplier needs a phone number");
            }
        }

        // check if database needs to be updated
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mInvDbHelper.getWritableDatabase();

        // Perform update
        int rowsUpdated = database.update(InvEntry.TABLE_NAME, values, selection, selectionArgs);

        // If rows were updated, notify all listeners that URI changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}

