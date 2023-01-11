package com.finnomena.testing.myconnection.cronet

import android.util.Log
import com.finnomena.testing.myconnection.data.ApiResponseInfo
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import org.json.JSONException
import org.json.JSONObject
import java.nio.ByteBuffer


private const val TAG = "MyUrlRequestCallback"

class MyUrlRequestCallback(private val callback: CallBackResponse) : UrlRequest.Callback() {
    private val apiResponseInfo = ApiResponseInfo()

    interface CallBackResponse {
        fun callback(apiResponseInfo: ApiResponseInfo)
    }

    override fun onRedirectReceived(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        newLocationUrl: String?
    ) {
        request?.followRedirect()
    }

    override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
        request?.read(ByteBuffer.allocateDirect(102400))
    }

    override fun onReadCompleted(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        byteBuffer: ByteBuffer?
    ) {
        try {
            byteBuffer?.clear()
            request?.read(byteBuffer)
            apiResponseInfo.statusCode = info?.httpStatusCode ?: 0
            val bytes: ByteArray
            if (byteBuffer?.hasArray() == true) {
                bytes = byteBuffer.array() ?: byteArrayOf()
            } else {
                bytes = ByteArray(byteBuffer?.remaining() ?: 0)
                byteBuffer?.get(bytes)
            }

            var responseBodyString = String(bytes) //Convert bytes to string
            responseBodyString = responseBodyString.trim { it <= ' ' }
                .replace("(\r\n|\n\r|\r|\n|\r0|\n0)".toRegex(), "")
            if (responseBodyString.endsWith("0")) {
                responseBodyString = responseBodyString.substring(0, responseBodyString.length - 1)
            }
            val headerString = StringBuilder()
            val responseString = StringBuilder()
            val header = info?.allHeaders ?: mutableMapOf()
            for (map in header) {
                headerString.append("${map.key}:${map.value}\n")
            }
            responseString.append(responseBodyString)
            apiResponseInfo.header = headerString.toString()
            apiResponseInfo.isSuccess = apiResponseInfo.statusCode == 200
            apiResponseInfo.data = responseString.toString()
        } catch (e: Exception) {
            apiResponseInfo.error = e
            apiResponseInfo.isSuccess = false
        }
        callback.callback(apiResponseInfo)
    }

    override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {}

    override fun onFailed(request: UrlRequest?, info: UrlResponseInfo?, error: CronetException?) {
        apiResponseInfo.error = error
        apiResponseInfo.isSuccess = false
        callback.callback(apiResponseInfo)
    }
}