package net.woorinfo.android.phone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

public class OneTouchWidgetProvider4By2 extends AppWidgetProvider {

    public static final int [] CALLER_ID = {
        R.id.btn1,
        R.id.btn2,
        R.id.btn3,
        R.id.btn4,
        R.id.btn6,
        R.id.btn7,
        R.id.btn8,
        R.id.btn9
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        
        if ( intent.getAction().equals(OneTouchCallActivity.ACTION_APPWIDGET_UPDATE) ) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, OneTouchWidgetProvider4By2.class)));
        }        
    }
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        FavoriteDB favoriteDB = new FavoriteDB(context);
        favoriteDB.open();
        Cursor cursor = favoriteDB.query(Favorite.sites.CONTENT_URI, null, null, null, null);
        
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_favorite_call);
        int index = 0;
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(cursor.getColumnIndex(Favorite.sites.NAME));
            String addr = cursor.getString(cursor.getColumnIndex(Favorite.sites.ADDRESS));
            
            if ( (name != null && 0 < name.trim().length()) &&
                 (addr != null && 0 < addr.trim().length()) ) {
                remoteViews.setTextViewText(CALLER_ID[index], name);
                remoteViews.setViewVisibility(CALLER_ID[index], View.VISIBLE);
                
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + addr));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                remoteViews.setOnClickPendingIntent(CALLER_ID[index], pendingIntent);

            }
            
            index++;
            cursor.moveToNext();
        }
        cursor.close();

        for (; index < CALLER_ID.length; index++) {
            remoteViews.setTextViewText(CALLER_ID[index], "Who?");
            if ( index == 0 ) {
                Intent intent = new Intent(context, OneTouchCallActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                remoteViews.setOnClickPendingIntent(CALLER_ID[index], pendingIntent);

                continue;
            }

            remoteViews.setViewVisibility(CALLER_ID[index], View.GONE);
                        
        }
        
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        
        favoriteDB.close();
    }


}
