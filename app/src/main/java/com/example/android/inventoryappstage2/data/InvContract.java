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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InvContract {

    // Use of package name as content authority because package name of app is unique
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryappstage2";

    // Base of all URIs
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible Path
    public static final String PATH_INV = "inv";

    // Private and empty constructor so class cant be initiated
    private InvContract() {
    }

    // Inventory table
    public static class InvEntry implements BaseColumns {

        // Name of database
        public static final String TABLE_NAME = "books";

        // Unique ID number
        public static final String _ID = BaseColumns._ID;

        // Product name, type Text
        public static final String COLUMN_PRODUCT_NAME = "ProductName";

        // Price, type Integer
        public static final String COLUMN_PRODUCT_PRICE = "Price";

        // Quantity, type Integer
        public static final String COLUMN_PRODUCT_QUANTITY = "Quantity";

        // Supplier name, type Text
        public static final String COLUMN_PRODUCT_SUPPLIER = "SupplierName";

        // Supplier phone number, type Text
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE = "SupplierPhoneNumber";

        // To access inventory data in provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INV);

        // MIME Type for list of inventory
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_INV;

        // MIME Type for one inventory
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INV;
    }
}
