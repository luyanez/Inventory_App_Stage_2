package com.example.android.product;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.product.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c){
        super(context,c,0);
    }

    Button button_decrease;

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view,final Context context, Cursor cursor) {

        TextView name = (TextView) view.findViewById(R.id.product_name);
        final TextView quantity = (TextView) view.findViewById(R.id.quantity);
        TextView price = (TextView) view.findViewById(R.id.price);

        ImageView image = (ImageView) view.findViewById(R.id.product_image);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE);

        final int productId = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry._ID));

        String productName = cursor.getString(nameColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);
        double productPrice = cursor.getDouble(priceColumnIndex);
        byte[] img = cursor.getBlob(imageColumnIndex);

        if (img == null){
            Log.v("Lucero Tag","NULL IMG");
        } else {
            String printImg = new String(img);

            if (printImg.trim().equals("0x")) {
                image.setImageResource(R.drawable.ic_empty_box);
            }
            else {
                Bitmap bitmap = getImage(img);
                image.setImageBitmap(bitmap);
            }
        }

        name.setText(productName);
        quantity.setText(Integer.toString(productQuantity)+" pcs");
        price.setText("$"+Double.toString(productPrice)+" PPU");


        button_decrease = (Button) view.findViewById(R.id.sale_button);

        button_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] quant = quantity.getText().toString().split(" ");
                int quantityInt = Integer.parseInt(quant[0]);
                if(quantityInt > 0){
                    quantityInt -= 1;
                    quantity.setText(Integer.toString(quantityInt)+" pcs");

                    //Update quantity
                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_QUANTITY, quantityInt);

                    Uri newUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, productId);
                    int rowsUpdated = context.getContentResolver().update(
                            newUri,
                            values,
                            null,
                            null);

                    if (rowsUpdated == 1) {
                        Toast.makeText(context, R.string.sold_successfully, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.sold_unsuccessfully, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.not_enough_products, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
