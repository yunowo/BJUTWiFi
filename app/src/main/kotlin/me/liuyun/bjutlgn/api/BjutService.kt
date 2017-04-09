package me.liuyun.bjutlgn.api

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface BjutService {
    @FormUrlEncoded
    @POST("/")
    fun login(@Field("DDDDD") user: String, @Field("upass") pass: String, @Field("R6") r6: String, @Field("6MKKey") out: String): Observable<String>

    @FormUrlEncoded
    @POST("/")
    fun loginLocal(@Field("DDDDD") user: String, @Field("upass") pass: String, @Field("R6") r6: String, @Field("0MKKey") `in`: String): Observable<String>

    @GET("/F.htm")
    fun logout(): Observable<String>

    @GET("/1.htm")
    fun stats(): Observable<String>
}