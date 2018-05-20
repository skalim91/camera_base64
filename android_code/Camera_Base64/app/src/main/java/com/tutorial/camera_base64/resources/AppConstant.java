package com.tutorial.camera_base64.resources;

import com.tutorial.camera_base64.resources.retrofit.RetrofitWebService;

import retrofit.RestAdapter;

public class AppConstant {


    public static String DOMAIN = "http://www.domain.com/api/"; /** Used in Live Server Online **/

    public static String DOMAIN_CHK = "http://192.168.0.104:80/proj_base64/"; /** Used in XAMPP Or Any other Local server **/


    public static RetrofitWebService RetrofitInitilizer(){

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(DOMAIN) //Setting the Root URL
                .build(); //Finally building the adapter

        RetrofitWebService api = adapter.create(RetrofitWebService.class);

        return api;
    }

    public static RetrofitWebService RetrofitDummy(){

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(DOMAIN_CHK) //Setting the Root URL
                .build(); //Finally building the adapter

        RetrofitWebService api = adapter.create(RetrofitWebService.class);

        return api;
    }



}
