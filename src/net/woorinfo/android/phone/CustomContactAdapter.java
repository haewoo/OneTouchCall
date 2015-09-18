package net.woorinfo.android.phone;

import java.io.InputStream;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomContactAdapter extends SimpleCursorAdapter {
    private Context context;

    public CustomContactAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.context = context;
    }

    @Override
    public void setViewImage(ImageView v, String value) {
        Log.d("KANG", "ID : " + value);
        
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(value));
        InputStream input = ContactsContract.Contacts
                .openContactPhotoInputStream(v.getContext().getContentResolver(), uri);
        Bitmap bitmap = null;
        if (input != null) {
            bitmap = BitmapFactory.decodeStream(input);
        }
        else {
            bitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.ic_contact_list_picture);
        }
        
        v.setImageBitmap(bitmap);

    }

    @Override
    public void setViewText(TextView v, String text) {
        super.setViewText(v, text);
        
        switch (v.getId()) {
        case R.id.row_id :
        case R.id.row_name :
        case R.id.row_phone :
        case R.id.row_contactId :
            v.setText(text);
            break;
        }
    }



}
