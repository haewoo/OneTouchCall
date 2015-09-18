package net.woorinfo.android.phone;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;


public class FavoriteDB {
    private static final String DATABASE_NAME    = "favorites.db";
    private static final int    DATABASE_VERSION = 1;
    private SQLiteDatabase      mDB;
    private DatabaseHelper      mDBHelper;
    private Context             mCtx;

    private static final int    SITES            = 0;
    private static final int    SITE_ID          = 1;

    private static UriMatcher   matcher;
    
    static{
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(Favorite.AUTHORITY, "sites", SITES);
        matcher.addURI(Favorite.AUTHORITY, "sites/#", SITE_ID);
    }

    private class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context, String name,
                CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Favorite.sites._CREATE);
            
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Favorite.sites._TABLENAME);
            onCreate(db);
        }
        
    }
    
    public FavoriteDB(Context context){
        this.mCtx = context;
    }
    
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = null;
        String id = null;
        String whereSegment = null;
        int count;
        
        switch(matcher.match(uri)){
        case SITES:
            tableName = Favorite.sites._TABLENAME;
            whereSegment = selection;
            break;
        case SITE_ID:
            tableName = Favorite.sites._TABLENAME;
            id = uri.getPathSegments().get(1);
            whereSegment = "_id = " + id +
            (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : "");
            break;
        default:
            throw new IllegalArgumentException();
        }
        
        count = mDB.delete(tableName, whereSegment, selectionArgs);
        
        return count;
    }

    public String getType(Uri uri) {
        switch(matcher.match(uri)){
        case SITES:
            return "vnd.woorinfo.cursor.dir/vnd.woorinfo.directCalll.favorite.sites";
        case SITE_ID:
            return "vnd.woorinfo.cursor.item/vnd.woorinfo.directCalll.favorite.sites";
        }
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        String tableName;
        Uri contentUri;
        long id;
        
        switch(matcher.match(uri)){
        case SITES:
            tableName = Favorite.sites._TABLENAME;
            contentUri = Favorite.sites.CONTENT_URI;
            break;
        default:
            throw new IllegalArgumentException();
        }
        
        id = mDB.insert(tableName, null, values);
        if(id > 0){
            Uri itemUri = ContentUris.withAppendedId(contentUri, id);
            return itemUri;
        }else
            throw new SQLException();
    }

    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch(matcher.match(uri)){
        case SITES:
            qb.setTables(Favorite.sites._TABLENAME);
            break;
        case SITE_ID:
            qb.setTables(Favorite.sites._TABLENAME);
            qb.appendWhere("_id="+uri.getPathSegments().get(1));
            break;
        default:
            throw new IllegalArgumentException();
        }
        
        Cursor c = qb.query(mDB, projection, selection, selectionArgs, 
                null, null, sortOrder);
        if ( c != null && c.getCount() != 0 ) {
            c.moveToFirst();
        }
        
        return c;
    }

    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        String tableName;
        String id;
        String whereSegment;
        int count;
        
        switch(matcher.match(uri)){
        case SITES:
            tableName = Favorite.sites._TABLENAME;
            whereSegment = selection;
            break;
        case SITE_ID:
            tableName = Favorite.sites._TABLENAME;
            id = uri.getPathSegments().get(1);
            whereSegment = "_id = " + id +
            (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : "");
            break;
        default:
            throw new IllegalArgumentException();
        }
        
        count = mDB.update(tableName, values, whereSegment, selectionArgs);
        return count;
    }
    
    public FavoriteDB open() throws SQLException{
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }
    
    public void close(){
        mDB.close();
    }

    public Cursor getFavorite(long id) {
        Cursor c = mDB.query(Favorite.sites._TABLENAME, null, "_id="+id, null, null, null, null);
        if(c!=null && c.getCount() != 0) {
            c.moveToFirst();
        }
        return c;
    }

    public int updateFavorite(long id, String name, String address) {
        ContentValues values = new ContentValues();
        values.put(Favorite.sites.NAME, name);
        values.put(Favorite.sites.ADDRESS, address);
        
        return mDB.update(Favorite.sites._TABLENAME, values, "_id=" + id, null);
    }

    public int deleteBookmark(long id) {
        return mDB.delete(Favorite.sites._TABLENAME, "_id=" + id, null);
    }
}
