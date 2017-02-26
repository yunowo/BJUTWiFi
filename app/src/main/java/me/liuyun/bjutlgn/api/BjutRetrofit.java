package me.liuyun.bjutlgn.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BjutRetrofit {
    private static String server = "https://wlgn.bjut.edu.cn";
    private static Headers headers = new Headers.Builder()
            .add("Origin", server)
            .add("User-Agent", "Mozilla/5.0 (Linux; Android 7.1.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3014.0 Mobile Safari/537.36")
            .add("Accept", "application/json, text/javascript, */*; q=0.01")
            .add("Accept-Encoding", "gzip, deflate")
            .add("Accept-Language", "zh-CN,en-US;q=0.8")
            .add("X-Requested-With", "XMLHttpRequest")
            .build();
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(chain -> chain.proceed(chain.request().newBuilder().headers(headers).build()))
            .cookieJar(new CookieJar() {
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<>();
                }
            }).build();
    private static BjutService bjutService = new Retrofit.Builder()
            .baseUrl(server)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build().create(BjutService.class);

    public static BjutService getBjutService() {
        return bjutService;
    }

    private BjutRetrofit() {
    }
}