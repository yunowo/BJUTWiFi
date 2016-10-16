package me.liuyun.bjutlgn.util;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import me.liuyun.bjutlgn.BjutApi;
import me.liuyun.bjutlgn.BjutRetrofit;
import me.liuyun.bjutlgn.entity.Stat;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

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

    public Stat GetStats(final View view, final TextView flowView, final TextView timeView, final TextView feeView, final RingProgressBar progress) {
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
                    flowView.setText(flow + " MB");
                    timeView.setText(time + " min");
                    feeView.setText(fee + " RMB");
                    int percent = Math.round(flow / 1024 / 8 * 100);
                    progress.setProgress(percent);
                    Snackbar.make(view, "Refresh OK.", Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(view, "Refresh failed.", Snackbar.LENGTH_LONG).show();
            }
        });
        return null;
    }




}