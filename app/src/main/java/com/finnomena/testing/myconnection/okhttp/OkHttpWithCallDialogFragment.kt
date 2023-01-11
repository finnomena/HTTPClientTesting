package com.finnomena.testing.myconnection.okhttp

import android.util.Log
import com.finnomena.testing.myconnection.Constant
import com.finnomena.testing.myconnection.ui.BaseCallApiDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

class OkHttpWithCallDialogFragment : BaseCallApiDialogFragment() {
    companion object {
        const val TAG = "OkHttpWithCallDialogFragment"
    }

    override fun callApi() {
        showLoading()
        getVersionInfoWithCall()
    }

    override fun setPageTitle() {
        binding.txtTitle.text = "OkHttp With Call"
    }

    private fun getVersionInfoWithCall() {
        getNterAppService().getVersionInfoWithCall().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                hideLoading()
                val responseBodyString = StringBuilder()
                val responseHeaderString = StringBuilder()
                for (map in response.headers().toMultimap()) {
                    responseHeaderString.append("${map.key}:${map.value}\n")
                }
                responseBodyString.append("${response.body().toString()}\n")
                setResponseHeader(responseHeaderString.toString())
                if (response.isSuccessful) {
                    showResponseSuccessStatus()
                    setResponseBody(responseBodyString.toString())
                } else {
                    try {
                        val error = response.errorBody()?.string()?:"-"
                        setResponseError(error)
                    } catch (e: Exception) {
                        setResponseError(e.localizedMessage ?: "-")
                    }
                    showResponseErrorStatus(response.code().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                hideLoading()
                if (t is HttpException) {
                    showResponseErrorStatus(t.code().toString())
                    try {
                        val responseHeaderString = StringBuilder()
                        for (map in t.response()?.headers()?.toMultimap() ?: emptyMap()) {
                            responseHeaderString.append("${map.key}:${map.value}\n")
                        }
                        val error = t.response()?.errorBody()?.string() ?: "-"
                        setResponseHeader(responseHeaderString.toString())
                        setResponseError(error)
                    } catch (e: Exception) {
                        setResponseHeader("-")
                        setResponseError(t.localizedMessage ?: "-")
                    }
                } else {
                    showResponseErrorStatus("-")
                    setResponseError(t.localizedMessage ?: "-")
                }
            }

        })
    }

    private fun getNterAppService(): NterAppService {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .baseUrl(Constant.BASE_URL)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create<NterAppService>(NterAppService::class.java)
    }

    private fun createClient(): OkHttpClient {
        val basic = HttpLoggingInterceptor()
        basic.setLevel(HttpLoggingInterceptor.Level.BASIC)
        val header = HttpLoggingInterceptor()
        header.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        val body = HttpLoggingInterceptor()
        body.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager().apply {
                setCookiePolicy(CookiePolicy.ACCEPT_ALL)
            }))
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
        return client.build()
    }
}