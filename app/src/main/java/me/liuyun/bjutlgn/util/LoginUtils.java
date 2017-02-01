package me.liuyun.bjutlgn.util;

import android.util.Log;

import me.liuyun.bjutlgn.BjutApi;
import me.liuyun.bjutlgn.BjutRetrofit;
import me.liuyun.bjutlgn.entity.Stat;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class LoginUtils {
    private final String TAG = "LoginUtils";
    private BjutApi api;

    public LoginUtils() {
        api = new BjutRetrofit().getBjutService();
    }

    public void login(String account, String password, Boolean outside) {
        try {
            Call<ResponseBody> call = outside ? api.login(account, password, "1", "123") : api.loginLocal(account, password, "1", "123");
            call.execute();
        } catch (Exception e) {
            Log.d(TAG, "Login error");
        }
    }

    public void logout() {
        try {
            Call<ResponseBody> call = api.logout();
            call.execute();
        } catch (Exception e) {
            Log.d(TAG, "Logout error");
        }
    }

    public Stat stat() {
        try {
            Call<ResponseBody> call = api.stats();
            return Utils.parseStat(call.execute().body().string());
        } catch (Exception e) {
            Log.d(TAG, "Get stat error");
        }
        return new Stat(0, 0, 0, false);
    }
}