package me.liuyun.bjutlgn;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

interface BjutApi {
    @FormUrlEncoded
    @POST("/")
    Call<ResponseBody> login(@Field("DDDDD") String user, @Field("upass") String pass, @Field("R6") String r6, @Field("6MKKey") String out);

    @FormUrlEncoded
    @POST("/")
    Call<ResponseBody> loginLocal(@Field("DDDDD") String user, @Field("upass") String pass, @Field("R6") String r6, @Field("0MKKey") String in);

    @GET("/F.htm")
    Call<ResponseBody> logout();

    @GET("/1.htm")
    Call<ResponseBody> stats();
}