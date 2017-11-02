package yiyou.heedu.com.yiyou.model.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import yiyou.heedu.com.yiyou.model.dao.UserAccountTab;

/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class UserAccountDB extends SQLiteOpenHelper {


    public UserAccountDB(Context context) {
        super(context,"yezi.db",null,1);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserAccountTab.CREATE_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
