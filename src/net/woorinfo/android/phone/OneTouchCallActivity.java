package net.woorinfo.android.phone;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class OneTouchCallActivity extends ListActivity {
    private final String        CONTACTS_LOOKUP_URI     = "content://com.android.contacts/contacts/lookup/";
    public static final String  ACTION_APPWIDGET_UPDATE = "net.woorinfo.android.phone.action.APPWIDGET_UPDATE";
    private static final int    MENU_ADD                = 0;
    private static final int    MENU_EDIT               = 1;
    private static final int    MENU_DELETE             = 2;
    private static final int    PICK_CONTRACT           = 0;
    
    // private FavoriteDB favoriteDB;
    private Cursor              cursor;
    private SimpleCursorAdapter adapter;
    private Resources res;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        registerForContextMenu(getListView());
        
        res = getResources();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        
        switch(item.getItemId()){
        case MENU_EDIT:
            LayoutInflater li = getLayoutInflater();
            final View v = li.inflate(R.layout.edit, null);
            final EditText name = (EditText)v.findViewById(R.id.edit_name);
            final EditText address = (EditText)v.findViewById(R.id.edit_address);
            
            Cursor c = managedQuery(
                  ContentUris.withAppendedId(Favorite.sites.CONTENT_URI, info.id)
                , null
                , null
                , null
                , null);
            String nameStr = c.getString(c.getColumnIndex(Favorite.sites.NAME));
            String addrStr = c.getString(c.getColumnIndex(Favorite.sites.ADDRESS));
            c.close();
            
            name.setText(nameStr);
            address.setText(addrStr);
            
            new AlertDialog.Builder(this)
                .setTitle(res.getText(R.string.editContactDescription))
                .setView(v)
                .setPositiveButton(res.getText(R.string.okDescription), new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues values = new ContentValues();
                        values.put(Favorite.sites.NAME, name.getText().toString());
                        values.put(Favorite.sites.ADDRESS, address.getText().toString());

                        getContentResolver().update(
                              ContentUris.withAppendedId(Favorite.sites.CONTENT_URI, info.id)
                            , values
                            , null
                            , null);
                        cursor.requery();
                        
                        updateWidget();
                    }
                    
                })
                .setNegativeButton(res.getText(R.string.cancelDescription), new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do Nothing                       
                    }
                    
                }).show();
            
            return true;
        case MENU_DELETE:
            getContentResolver().delete(ContentUris.withAppendedId(Favorite.sites.CONTENT_URI, info.id), null, null);
            cursor.requery();
            
            updateWidget();
            return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        
        menu.add(0, MENU_EDIT, Menu.NONE, res.getText(R.string.editContactDescription));
        menu.add(0, MENU_DELETE, Menu.NONE, res.getText(R.string.deleteContactDescription));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
        case MENU_ADD:
            
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                , PICK_CONTRACT);
            
            break;
        }
        return false;
    }
    
    private void updateWidget() {
        Intent intent = new Intent(ACTION_APPWIDGET_UPDATE);
        sendBroadcast(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        if (getListView().getCount() < OneTouchWidgetProvider4By2.CALLER_ID.length) {
            menu.add(Menu.NONE, MENU_ADD, Menu.NONE, res.getText(R.string.addContactDescription))
                    .setIcon(android.R.drawable.ic_menu_add).setEnabled(true);
        }
        else {
            menu.add(Menu.NONE, MENU_ADD, Menu.NONE, res.getText(R.string.addContactDescription))
                    .setIcon(android.R.drawable.ic_menu_add).setEnabled(false);
            Toast.makeText(OneTouchCallActivity.this, R.string.warningResistrationMessage, Toast.LENGTH_SHORT).show();
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        cursor = managedQuery(Favorite.sites.CONTENT_URI, null, null, null, null);
        
        String [] from = new String[]{ Favorite.sites.NAME, Favorite.sites.ADDRESS };
        int [] to = new int[] { R.id.row_name, R.id.row_address };
        
        adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);
        setListAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch ( requestCode ) {
        case PICK_CONTRACT :
            if ( resultCode == RESULT_OK ) {
                if ( data.getData().toString().startsWith(CONTACTS_LOOKUP_URI) ) {
                    Toast.makeText(this, R.string.searchResultNotSupport, Toast.LENGTH_SHORT).show();
                    break;
                }
                
                String [] projection = {
                        ContactsContract.Contacts._ID
                      , Data.DISPLAY_NAME
                      , Phone.NUMBER
                };
                String selection = null;
                String [] selectionArgs = null;
                String sortOrder = null;
                Cursor c = getContentResolver().query(data.getData(), projection, selection, selectionArgs, sortOrder);
                
                if (c != null && !c.isAfterLast()) {
                    c.moveToFirst();
                }
                String name = c.getString(c.getColumnIndex(Data.DISPLAY_NAME));
                String number = c.getString(c.getColumnIndex(Phone.NUMBER));
                
                ContentValues values = new ContentValues();
                values.put(Favorite.sites.NAME, name);
                values.put(Favorite.sites.ADDRESS, number);
                
                getContentResolver().insert(Favorite.sites.CONTENT_URI, values);
                cursor.requery();
                updateWidget();
                
            }
            break;
        }
    }

}