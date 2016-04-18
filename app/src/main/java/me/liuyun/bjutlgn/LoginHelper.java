package me.liuyun.bjutlgn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginHelper {
    private static Headers HEADERS = new Headers.Builder()
            .add("Content-Type", "application/x-www-form-urlencoded")
            .add("Origin", "https://wlgn.bjut.edu.cn")
            .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2660.3 Safari/537.36")
            .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .build();

    public static boolean DoLogin(String account, String password, Boolean outside) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        RequestBody formBody = new FormBody.Builder()
                .add("DDDDD", account)
                .add("upass", password)
                .add("R6", "1")
                .add(outside ? "6MKKey" : "0MKKEY", "123")
                .build();
        Request request = new Request.Builder()
                .url("https://wlgn.bjut.edu.cn/")
                .headers(HEADERS)
                .post(formBody)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static Boolean DoLogout() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("https://wlgn.bjut.edu.cn/F.htm")
                .headers(HEADERS)
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static Stats GetStats() {
        Stats stats = new Stats();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("https://wlgn.bjut.edu.cn/")
                .headers(HEADERS)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            String html = response.body().string();
            Pattern p = Pattern.compile("(?s)time='(.+?)'");
            Matcher m = p.matcher(html);
            while (m.find()) {
                stats.setTime(Integer.parseInt(m.group(1).replace(" ", "").replace("\"", "")));
            }
            p = Pattern.compile("(?s)flow='(.+?)'");
            m = p.matcher(html);
            while (m.find()) {
                stats.setFlow(Integer.parseInt(m.group(1).replace(" ", "").replace("\"", "")));
            }
            p = Pattern.compile("(?s)fee='(.+?)'");
            m = p.matcher(html);
            while (m.find()) {
                stats.setFee(Integer.parseInt(m.group(1).replace(" ", "").replace("\"", "")));
            }
        } catch (IOException e) {
            return null;
        }
        return stats;
    }
}
