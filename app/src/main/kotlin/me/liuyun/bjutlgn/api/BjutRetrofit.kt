package me.liuyun.bjutlgn.api

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object BjutRetrofit {
    private val server = "https://wlgn.bjut.edu.cn"
    private val headers = Headers.Builder()
            .add("Origin", server)
            .add("User-Agent", "Mozilla/5.0 (Linux; Android 7.1.2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3131.0 Mobile Safari/537.36")
            .add("Accept", "application/json, text/javascript, */*; q=0.01")
            .add("Accept-Encoding", "gzip, deflate")
            .add("Accept-Language", "zh-CN,en-US;q=0.8")
            .add("X-Requested-With", "XMLHttpRequest")
            .build()
    private val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor { it.proceed(it.request().newBuilder().headers(headers).build()) }
            .cookieJar(object : CookieJar {
                private val cookieStore = HashMap<String, List<Cookie>>()

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url.host] = cookies
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore[url.host] ?: ArrayList<Cookie>()
                }
            }).build()
    val bjutService: BjutService by lazy {
        Retrofit.Builder()
                .baseUrl(server)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build().create<BjutService>(BjutService::class.java)
    }

    fun evictAll() {
        client.connectionPool.evictAll()
    }
}