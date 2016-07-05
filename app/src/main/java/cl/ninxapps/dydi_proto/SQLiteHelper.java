package cl.ninxapps.dydi_proto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jose on 5/7/16.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "text";

    private static final String DATABASE_NAME = "dydi.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CATEGORIES + "( "
            + COLUMN_ID + " integer primary key, "
            + COLUMN_TEXT + " text not null, "
            + ");";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }
}
