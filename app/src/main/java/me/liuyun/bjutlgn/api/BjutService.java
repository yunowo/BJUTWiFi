package me.liuyun.bjutlgn.api;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BjutService {
    @FormUrlEncoded
    @POST("/")
    Observable<String> login(@Field("DDDDD") String user, @Field("upass") String pass, @Field("R6") String r6, @Field("6MKKey") String out);

    @FormUrlEncoded
    @POST("/")
    Observable<String> loginLocal(@Field("DDDDD") String user, @Field("upass") String pass, @Field("R6") String r6, @Field("0MKKey") String in);

    @GET("/F.htm")
    Observable<String> logout();

    @GET("/1.htm")
    Observable<String> stats();
}