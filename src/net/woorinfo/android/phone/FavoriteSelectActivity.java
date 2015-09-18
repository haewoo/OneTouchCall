package net.woorinfo.android.phone;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FavoriteSelectActivity extends ListActivity {
    public static final String  ACTION_APPWIDGET_UPDATE_1BY_1 = "net.woorinfo.android.phone.action.APPWIDGET_UPDATE_1_BY_1";
    public static final String  ACTION_APPWIDGET_UPDATE_2BY_2 = "net.woorinfo.android.phone.action.APPWIDGET_UPDATE_2_BY_2";
    public static final String  ACTION_APPWIDGET = "ACTION_APPWIDGET";
    private static final String[] PROJECTION                  = new String[] {
            ContactsContract.Contacts._ID
          , Data.DISPLAY_NAME
          , Phone.NUMBER
          , Phone.CONTACT_ID
          , Phone.CONTACT_ID
    };
    
    private int appWidgetId;
    private String actionType;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main_onebyone);
        registerForContextMenu(getListView());

        appWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        actionType = getIntent().getExtras().getString(ACTION_APPWIDGET);
        
        String selection = Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'";
//        String selection = Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE
//        		+ "' AND " + Phone.TYPE + "=" + Phone.TYPE_MOBILE;
        String[] selectionArgs = null;
        String sortOrder = Data.DISPLAY_NAME + " ASC";

        // Get a cursor with all people
        Cursor c = managedQuery(Data.CONTENT_URI, PROJECTION, selection,
                selectionArgs, sortOrder);

        ListAdapter adapter = new CustomContactAdapter(this, R.layout.row_2, c,
                PROJECTION, new int[] { R.id.row_id, R.id.row_name, R.id.row_phone,
                        R.id.row_contactId, R.id.row_photo });

        
        setListAdapter(adapter);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        String dispName = ((TextView)v.findViewById(R.id.row_name)).getText().toString();
        String phoneNumber = ((TextView)v.findViewById(R.id.row_phone)).getText().toString();
        String contactId = ((TextView)v.findViewById(R.id.row_contactId)).getText().toString();
        
        Intent intent = new Intent(actionType);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(Phone.CONTACT_ID, contactId);
        intent.putExtra(Data.DISPLAY_NAME, dispName);
        intent.putExtra(Phone.NUMBER, phoneNumber);
              
        sendBroadcast(intent);

        finish();
    }


}
