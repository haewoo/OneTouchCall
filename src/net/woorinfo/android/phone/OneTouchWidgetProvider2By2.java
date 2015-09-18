package net.woorinfo.android.phone;

import java.io.InputStream;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.widget.RemoteViews;

public class OneTouchWidgetProvider2By2 extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        
        Log.d("KANG", "Action : " + intent.getAction());
        
        if ( FavoriteSelectActivity.ACTION_APPWIDGET_UPDATE_2BY_2.equals(intent.getAction()) ) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            
            int [] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, OneTouchWidgetProvider2By2.class));
            int widgetID = appIds[appIds.length-1];
            int receivedWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
            
            Log.d("KANG", "onreceived WidgetID : " + widgetID);
            Log.d("KANG", "onreceived Intent WidgetID : " + intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0));
            
            if ( widgetID == 0 ) {
                return;
            }
            
            if ( widgetID != receivedWidgetId ) {
                widgetID = receivedWidgetId;
            }
            
            String dispName = intent.getStringExtra(Data.DISPLAY_NAME);
            String phoneNumber = intent.getStringExtra(Phone.NUMBER);
            long contactId = Long.parseLong(intent.getStringExtra(Phone.CONTACT_ID));
            
            SharedPreferences pref = null;
            SharedPreferences.Editor prefEditor = null;
            
            pref = context.getSharedPreferences("OneTouchWidgetProvider1By1", Activity.MODE_PRIVATE);
            prefEditor = pref.edit();
            
            prefEditor.putString(Data.DISPLAY_NAME + "_" + widgetID, dispName);
            prefEditor.putString(Phone.NUMBER + "_" + widgetID, phoneNumber);
            prefEditor.putLong(Phone.CONTACT_ID + "_" + widgetID, contactId);
            prefEditor.commit();
            
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, OneTouchWidgetProvider2By2.class)));
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        
        SharedPreferences pref = null;
        SharedPreferences.Editor prefEditor = null;
        pref = context.getSharedPreferences("OneTouchWidgetProvider1By1", Activity.MODE_PRIVATE);
        prefEditor = pref.edit();
        
        prefEditor.remove(Data.DISPLAY_NAME + "_" + appWidgetIds[0]);
        prefEditor.remove(Phone.NUMBER + "_" + appWidgetIds[0]);
        prefEditor.remove(Phone.CONTACT_ID + "_" + appWidgetIds[0]);
        prefEditor.commit();
    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        
        
        for(int i = 0; i < appWidgetIds.length; i++ ) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.twobytwo);
            SharedPreferences pref = null;
            pref = context.getSharedPreferences("OneTouchWidgetProvider1By1", Activity.MODE_PRIVATE);
            long contactId = pref.getLong(Phone.CONTACT_ID + "_" + appWidgetId, 0L);
            String dispName = pref.getString(Data.DISPLAY_NAME + "_" + appWidgetId, "Who?");
            String phoneNumber = pref.getString(Phone.NUMBER + "_" + appWidgetId, "");
            Log.d("KANG", "Intent.putExtra : " + appWidgetId + ", " + contactId + ", " + dispName + ", " + phoneNumber);

            if ( dispName == null || "Who?".equals(dispName)) {
                Intent intent = new Intent(context, FavoriteSelectActivity.class);
                intent.putExtra(FavoriteSelectActivity.ACTION_APPWIDGET, FavoriteSelectActivity.ACTION_APPWIDGET_UPDATE_2BY_2);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.wdgImage, pendingIntent);
            }
            else {
                Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
                InputStream input = ContactsContract.Contacts
                        .openContactPhotoInputStream(context.getContentResolver(), uri);
                Bitmap bitmap = null;
                if (input != null) {
                    bitmap = BitmapFactory.decodeStream(input);
                }
                else {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_contact_list_picture);
                }

                remoteViews.setBitmap(R.id.wdgImage, "setImageBitmap", bitmap);
                remoteViews.setCharSequence(R.id.wdgName, "setText", dispName);
                
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                remoteViews.setOnClickPendingIntent(R.id.wdgImage, pendingIntent);
            }

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
