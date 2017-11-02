package yiyou.heedu.com.yiyou.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import yiyou.heedu.com.yiyou.model.db.UserAccountDB;
import yiyou.heedu.com.yiyou.model.domain.UserInfo;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class UserAccountDAO {
    private final UserAccountDB mHelper;

    public UserAccountDAO(Context context) {
        mHelper = new UserAccountDB(context);
    }

    public void addAccount(UserInfo user) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserAccountTab.COL_HXID, user.getHxid());
        values.put(UserAccountTab.COL_NAME, user.getName());
        values.put(UserAccountTab.COL_NICK, user.getNick());
        values.put(UserAccountTab.COL_PHOTO, user.getPhoto());

        db.replace(UserAccountTab.TAB_NAME, null, values);


    }

    public UserInfo getAccount(String hxID) {
        //h获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();
        // 执行查询语句
        String sql = "select * from " + UserAccountTab.TAB_NAME + " where " + UserAccountTab.COL_HXID + "=?";
        Log.i(TAG, "inite:  String sql = \"select * from \" + UserA");
        Cursor cursor = db.rawQuery(sql, new String[]{hxID});
        Log.i(TAG, "inite: cursor"+cursor.toString());
        UserInfo userinfo = null;
        if (cursor.moveToNext()) {
            userinfo = new UserInfo();
            userinfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTab.COL_NAME)));
            userinfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTab.COL_HXID)));
            userinfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTab.COL_NICK)));
            userinfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTab.COL_PHOTO)));
        }

        cursor.close();
        //返回数据对象
        return userinfo;
    }
}
