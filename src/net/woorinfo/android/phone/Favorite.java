package net.woorinfo.android.phone;

import android.net.Uri;
import android.provider.BaseColumns;

public class Favorite {
    public static final String AUTHORITY = "net.woorinfo.android.phone.OneTouchCall";

    public static final class sites implements BaseColumns {
        // 추가됨
        public static final Uri    CONTENT_URI = Uri.parse("content://"
                                                       + AUTHORITY + "/sites");
        public static final String NAME        = "name";
        public static final String ADDRESS     = "address";
        public static final String _TABLENAME  = "favorite";
        public static final String _CREATE     = "create table "
                                                       + _TABLENAME
                                                       + "("
                                                       + _ID
                                                       + " integer primary key autoincrement, "
                                                       + NAME
                                                       + " text not null, "
                                                       + ADDRESS
                                                       + " text not null);";
    }
}
