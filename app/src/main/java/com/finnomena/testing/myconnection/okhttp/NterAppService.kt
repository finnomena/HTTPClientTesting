package com.finnomena.testing.myconnection.okhttp

import com.finnomena.testing.myconnection.Constant
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import rx.Observable

interface NterAppService {
    @Headers("Cache-Control: no-cache")
    @GET(Constant.PATH_GET_VERSION)
    fun getVersionInfo(): Observable<JsonObject>

    @Headers("Cache-Control: no-cache")
    @GET(Constant.PATH_GET_VERSION)
    fun getVersionInfoWithCall(): Call<JsonObject>

}