package com.example.ventura;

import rx.Observable;
import okhttp3.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetroFitInterface {
    @POST("register")
    Observable<Response> register(@Body User user);

    @POST("authenticate")
    Observable<Response> login();

    @GET("users/{email}")
    Observable<User> getProfile(@Path("email") String email);

    //@PUT("users/{email}");
    //Observable<Response> changePassword(@Path("email") String email, @Body User user);
}

