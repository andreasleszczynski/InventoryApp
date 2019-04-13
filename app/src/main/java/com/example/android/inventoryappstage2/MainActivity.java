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

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.inventoryappstage2.data.InvContract.InvEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentInvUri;

    // Adapter for ListView
    private InvCursorAdapter mCursorAdapter;

    // Identifier for data loader
    private static final int INV_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the ListView with items
        ListView listView = findViewById(R.id.list);

        // Find and set the empty list
        View emptyView = findViewById(R.id.linear_layout_empty_view);
        listView.setEmptyView(emptyView);

        // Find the add button and set onClickListener, proceeds to detailed product view
        Button addButton = findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        // Setup an adapter to create list items
        mCursorAdapter = new InvCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        // Setup item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                mCurrentInvUri = ContentUris.withAppendedId(InvEntry.CONTENT_URI, id);

                intent.setData(mCurrentInvUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(INV_LOADER, null, this);
    }

    // inserts dummy data into the database
    private void insertProduct() {

        ContentValues values = new ContentValues();
        values.put(InvEntry.COLUMN_PRODUCT_NAME, "Book");
        values.put(InvEntry.COLUMN_PRODUCT_PRICE, createInteger());
        values.put(InvEntry.COLUMN_PRODUCT_QUANTITY, createInteger());
        values.put(InvEntry.COLUMN_PRODUCT_SUPPLIER, "Udacity");
        values.put(InvEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, "555555");

        getContentResolver().insert(InvEntry.CONTENT_URI, values);
    }

    // Creates random integer for price and quantity
    private int createInteger() {
        return (int) Math.floor(Math.random() * 1001);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Defines a projection for the columns we need
        String[] projection = {
                InvEntry._ID,
                InvEntry.COLUMN_PRODUCT_NAME,
                InvEntry.COLUMN_PRODUCT_PRICE,
                InvEntry.COLUMN_PRODUCT_QUANTITY,
        };

        // Executes query method on a background thread
        return new CursorLoader(this,
                InvEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    // Inflates menu options and adds menu items to app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Determines what happens when user clicks on one of the menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_fake_data:
                insertProduct();
                return true;
            case R.id.delete_table_data:
                deleteAllInventoryObjects();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Deletes all data in the database
    private void deleteAllInventoryObjects() {
        int rowsDeleted = getContentResolver().delete(InvEntry.CONTENT_URI, null, null);
        Log.i("LOG_TAG_MainActivity", rowsDeleted + " rows deleted from database");
    }
}
