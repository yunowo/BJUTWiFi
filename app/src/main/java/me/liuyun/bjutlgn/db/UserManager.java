package me.liuyun.bjutlgn.db;

import android.content.Context;
import android.util.Log;

import java.util.List;

import me.liuyun.bjutlgn.entity.User;

public class UserManager {
    private static final String TAG = "UserManager";

    private DBHelper dbHelper;

    public UserManager(Context context) {
        this.dbHelper = new DBHelper(context);
    }


    public boolean insertUser(String account, String password, int pack) {
        try {
            int position = Integer.parseInt(dbHelper.getUserDao().queryRaw(dbHelper.getUserDao().queryBuilder().selectRaw("MAX(position)").prepareStatementString()).getFirstResult()[0]);
            dbHelper.getUserDao().createOrUpdate(new User(0, account, password, pack, position + 1));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "insertUser", e);
        }
        return false;
    }

    public boolean updateUser(User user) {
        try {
            dbHelper.getUserDao().update(user);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "updateUser", e);
        }
        return false;
    }

    public boolean deleteUser(int id) {
        try {
            dbHelper.getUserDao().deleteById(id);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "deleteUser", e);
        }
        return false;
    }

    public List<User> getAllUsers() {
        try {
            return dbHelper.getUserDao().queryBuilder().orderBy("position", true).query();
        } catch (Exception e) {
            Log.e(TAG, "getAllUsers", e);
        }
        return null;
    }

}
