/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.product;

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
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.product.data.InventoryContract.InventoryEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private EditText mProductNameEditText;

    private EditText mPriceEditText;

    private EditText mQuantityEditText;

    private EditText mSupplierNameEditText;

    private EditText mPhoneNumberEditText;

    private boolean mProductHasChanged = false;

    private Uri mCurrentProductUri;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(R.string.editor_activity_title_new_product);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.editor_activity_title_edit_product);

            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }


        mProductNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name) ;
        mPhoneNumberEditText = (EditText) findViewById(R.id.edit_phone_number);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mPhoneNumberEditText.setOnTouchListener(mTouchListener);

    }

    private void saveProduct(){

        String productNameString = mProductNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String phoneNumberString = mPhoneNumberEditText.getText().toString().trim();

        if(mCurrentProductUri == null &&
                TextUtils.isEmpty(productNameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(phoneNumberString)) {
            return;
        }

        if (TextUtils.isEmpty(productNameString)){
            Toast.makeText(this, getString(R.string.no_name_save),
                    Toast.LENGTH_SHORT).show();
        } else {

            ContentValues values = new ContentValues();

            values.put(InventoryEntry.COLUMN_PRODUCT_NAME,productNameString);

            //
            values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
            values.put(InventoryEntry.COLUMN_PHONE_NUMBER, phoneNumberString);

            double price = 0.0;
            int quantity = 0;

            if(!TextUtils.isEmpty(priceString)){
                price = Double.parseDouble(priceString);
            }
            values.put(InventoryEntry.COLUMN_PRICE, price);

            if(!TextUtils.isEmpty(quantityString)) {
                quantity = Integer.parseInt(quantityString);
            }
            values.put(InventoryEntry.COLUMN_QUANTITY, quantity);

            if (mCurrentProductUri == null) {
                Uri newUri =getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

                if(newUri == null) {
                    Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_product_successful),
                            Toast.LENGTH_SHORT).show();
                }

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save Product to Database
                saveProduct();
                //Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PHONE_NUMBER
                 };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int phoneNumberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PHONE_NUMBER);


            String currentProductName = cursor.getString(productNameColumnIndex);
            double currentPrice = cursor.getDouble(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentSupplierName = cursor.getString(supplierNameColumnIndex);
            String currentPhoneNumber = cursor.getString(phoneNumberColumnIndex);


            mProductNameEditText.setText(currentProductName);
            mPriceEditText.setText(Double.toString(currentPrice));
            mQuantityEditText.setText(Integer.toString(currentQuantity));
            mSupplierNameEditText.setText(currentSupplierName);
            mPhoneNumberEditText.setText(currentPhoneNumber);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mPhoneNumberEditText.setText("");
    }


    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
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

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
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

        // Close the activity
        finish();
    }
}