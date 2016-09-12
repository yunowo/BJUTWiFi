package me.liuyun.bjutlgn;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Interceptor;
import okhttp3.Headers;
import okhttp3.Response;
import retrofit2.Retrofit;

class BjutRetrofit {
    private static String SERVER = "https://wlgn.bjut.edu.cn";
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
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        return chain.proceed(chain.request().newBuilder().headers(HEADERS).build());
                    }
                }).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER)
                .client(client)
                .build();
        bjutService = retrofit.create(BjutApi.class);
    }

    BjutApi getBjutService() {
        return bjutService;
    }
}