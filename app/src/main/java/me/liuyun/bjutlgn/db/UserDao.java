package me.liuyun.bjutlgn.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.liuyun.bjutlgn.entity.User;

public class UserDao {

    private static final String TAG = "UserDao";
    private final String[] USER_COLUMNS = new String[]{"id", "account", "password", "package"};
    private DBHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean insertUser(String account, String password, int pack) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("account", account);
            contentValues.put("password", password);
            contentValues.put("package", pack);
            db.beginTransaction();
            db.insertOrThrow(DBHelper.TABLE_USERS, null, contentValues);
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    public boolean updateUser(int id, String account, String password, int pack) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("account", account);
            contentValues.put("password", password);
            contentValues.put("package", pack);
            db.update(DBHelper.TABLE_USERS, contentValues, "id = ?",
                    new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    public boolean deleteUser(int id) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(DBHelper.TABLE_USERS, "id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    public List<User> getAllUsers() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_USERS, USER_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<User> UserList = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    UserList.add(parseUser(cursor));
                }
                return UserList;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    private User parseUser(Cursor cursor) {
        return new User(cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("account")),
                cursor.getString(cursor.getColumnIndex("password")),
                cursor.getInt(cursor.getColumnIndex("package")));
    }
}