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

class OkHttpDialogFragment : BaseCallApiDialogFragment() {
    companion object {
        const val TAG = "OkHttpDialogFragment"
    }

    override fun callApi() {
        showLoading()
        getNterAppService().getVersionInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { t -> Log.e("anchan", "error getVersionInfo : " + t?.message) }
            .subscribe(CustomSubscriber())
    }

    override fun setPageTitle() {
        binding.txtTitle.text = "OkHttp"
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

    inner class CustomSubscriber : Subscriber<JsonObject>() {
        override fun onCompleted() {}

        override fun onError(e: Throwable?) {
            hideLoading()
            if (e is HttpException) {
                val responseHeaderString = StringBuilder()
                val response = e.response()
                val error = e.response()?.errorBody()?.string() ?: ""
                for (map in response?.headers()?.toMultimap() ?: emptyMap()) {
                    responseHeaderString.append("${map.key}:${map.value}\n")
                }
                setResponseHeader(responseHeaderString.toString())
                showResponseErrorStatus(e.code().toString())
                setResponseError(error)
            }else{
                setResponseHeader("-")
                showResponseErrorStatus("-")
                setResponseError(e?.localizedMessage.orEmpty())
            }
        }

        override fun onNext(t: JsonObject) {
            hideLoading()
            setResponseHeader("-")
            showResponseSuccessStatus()
            setResponseBody(t.toString())
        }
    }
}