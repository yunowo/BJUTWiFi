package me.liuyun.bjutlgn;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

class LoginHelper {
    private BjutApi api;

    LoginHelper() {
        api = new BjutRetrofit().getBjutService();
    }

    Boolean Login(String account, String password, Boolean outside, View view) {
        Call<ResponseBody> call = outside ? api.login(account, password, "1", "123") : api.loginLocal(account, password, "1", "123");
        call.enqueue(okFailCallback(view));
        return true;
    }

    Boolean Logout(View view) {
        Call<ResponseBody> call = api.logout();
        call.enqueue(okFailCallback(view));
        return true;
    }

    Boolean GetStats(final View view, final TextView textView) {
        Call<ResponseBody> call = api.stats();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    int time = 0;
                    float flow = 0, fee = 0f;
                    String html = response.body().string().replace(" ", "");
                    Pattern p = Pattern.compile("time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'");
                    Matcher m = p.matcher(html);
                    while (m.find()) {
                        time = Integer.parseInt(m.group(1));
                        flow = Float.parseFloat(m.group(2)) / 1024;
                        fee = Float.parseFloat(m.group(3)) / 100;
                    }
                    String text = flow + " MB\n" + time + " min\n" + fee + " RMB";
                    textView.setText(text);
                    Snackbar.make(view, "Refresh OK.", Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(view, "Refresh failed.", Snackbar.LENGTH_LONG).show();
            }
        });
        return true;
    }

    private static Callback<ResponseBody> okFailCallback(final View view) {
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Snackbar.make(view, "OK", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(view, "Failed", Snackbar.LENGTH_LONG).show();
            }
        };
    }
}