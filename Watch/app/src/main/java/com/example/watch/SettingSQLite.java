package com.example.watch;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SettingSQLite extends SQLiteOpenHelper {

    public SettingSQLite(@Nullable Context context, @Nullable String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE Setting( " +
                "seqno INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "color INTEGER, " +
                "textSize INTEGER" +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS Setting; ";

        db.execSQL(query);

        onCreate(db);
    }
}

class SettingSQLiteQuery {

    SettingSQLite setting;

    public SettingSQLiteQuery(Context context) {
        setting = new SettingSQLite(context, "setting");
    }

    public int totalCount() {
        SQLiteDatabase DB = null;
        int count = 0;

        try {
            DB = setting.getReadableDatabase();
            String query = "SELECT count(seqno) FROM Setting";

            Cursor cursor = DB.rawQuery(query, null);

            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
           }

            DB.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (DB != null)
                DB.close();
        }

        return count;
    }

    public SettingDTO settingSelect() {
        SQLiteDatabase DB = null;
        SettingDTO data = new SettingDTO(0, 0, 0);

        try {
            DB = setting.getReadableDatabase();
            String query = "SELECT * FROM Setting ORDER BY seqno DESC";

            Cursor cursor = DB.rawQuery(query, null);

            if (cursor.moveToNext()) {
                data.setSeqno(cursor.getInt(0));
                data.setColor(cursor.getInt(1));
                data.setTextSize(cursor.getInt(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (DB != null)
                DB.close();
        }

        return data;
    }

    public void settingCloes() {
        setting.close();
    }
}
