package com.example.android.product;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.product.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c){
        super(context,c,0);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.product_name);
        TextView quantity = (TextView) view.findViewById(R.id.quantity);
        TextView price = (TextView) view.findViewById(R.id.price);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);

        String productName = cursor.getString(nameColumnIndex);
        int productQuantity = cursor.getInt(quantityColumnIndex);
        double productPrice = cursor.getDouble(priceColumnIndex);

        name.setText(productName);
        quantity.setText(Integer.toString(productQuantity)+" pcs");
        price.setText("$"+Double.toString(productPrice)+" PPU");
    }
}
