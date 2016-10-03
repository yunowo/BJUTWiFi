package me.liuyun.bjutlgn;

import android.support.design.widget.Snackbar;
import android.view.View;

import me.drakeet.retrofit2.adapter.agera.AgeraCallAdapterFactory;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

class BjutRetrofit {
    //private static String SERVER = "https://wlgn.bjut.edu.cn";
    private static String SERVER = "http://www.bjut.edu.cn";
    private static Headers HEADERS = new Headers.Builder()
            .add("Origin", SERVER)
            .add("User-Agent", "Mozilla/5.0 (Linux; Android 7.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.16 Mobile Safari/537.36")
            .add("Accept", "application/json, text/javascript, */*; q=0.01")
            .add("Accept-Encoding", "gzip, deflate")
            .add("Accept-Language", "zh-CN,en-US;q=0.8")
            .add("X-Requested-With", "XMLHttpRequest")
            .build();

    private final BjutApi bjutService;

    BjutRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(chain.request().newBuilder().headers(HEADERS).build())).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER)
                .client(client)
                .addCallAdapterFactory(AgeraCallAdapterFactory.create())
                .build();
        bjutService = retrofit.create(BjutApi.class);
    }

    BjutApi getBjutService() {
        return bjutService;
    }

    static Callback<ResponseBody> okFailCallback(final View view) {
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Snackbar.make(view, "OK.", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(view, "Failed.", Snackbar.LENGTH_LONG).show();
            }
        };
    }
}