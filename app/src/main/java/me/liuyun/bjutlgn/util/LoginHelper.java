package me.liuyun.bjutlgn.util;

import android.view.View;

import me.liuyun.bjutlgn.BjutApi;
import me.liuyun.bjutlgn.BjutRetrofit;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class LoginHelper {
    private final String LOG_TAG = "LoginHelper";
    private BjutApi api;

    public LoginHelper() {
        api = new BjutRetrofit().getBjutService();
    }

    public Boolean Login(String account, String password, Boolean outside, View view) {
        Call<ResponseBody> call = outside ? api.login(account, password, "1", "123") : api.loginLocal(account, password, "1", "123");
        call.enqueue(BjutRetrofit.okFailCallback(view));
        return true;
    }

    public Boolean Logout(View view) {
        Call<ResponseBody> call = api.logout();
        call.enqueue(BjutRetrofit.okFailCallback(view));
        return true;
    }

}