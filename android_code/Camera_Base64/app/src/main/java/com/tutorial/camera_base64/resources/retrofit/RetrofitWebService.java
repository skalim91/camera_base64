package com.tutorial.camera_base64.resources.retrofit;


import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;

public interface RetrofitWebService {

    @GET("/get_example")
    public void getData(
            @QueryMap Map<String, String> params,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/get_image.php")
    public void sendImageOnline(
            @Field("image_str") String ImageStr,
            Callback<Response> callback);


}
