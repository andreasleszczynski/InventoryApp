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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryappstage2.data.InvContract.InvEntry;

public class InvDbHelper extends SQLiteOpenHelper {

    // Name of database file
    private static final String DATABASE_NAME = "store.db";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // constructor of class
    public InvDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create DB for first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        // String that contains CREATE TABLE SQL statement
        String SQL_CREATE_INV_TABLE = "CREATE TABLE " + InvEntry.TABLE_NAME +
                "(" + InvEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InvEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                InvEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
                InvEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                InvEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL, " +
                InvEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " TEXT NOT NULL);";

        // execute sql statement
        db.execSQL(SQL_CREATE_INV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
