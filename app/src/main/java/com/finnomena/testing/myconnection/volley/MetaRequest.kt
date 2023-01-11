package com.finnomena.testing.myconnection.volley

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.finnomena.testing.myconnection.data.ApiResponseInfo


class MetaRequest(
    private val callbackResponse: CallbackResponse,
    method: Int, url: String?, errorListener: Response.ErrorListener?
) : Request<ApiResponseInfo>(method, url, errorListener) {
    private val apiResponseInfo = ApiResponseInfo()

    interface CallbackResponse {
        fun callback(apiResponseInfo: ApiResponseInfo)
    }

    override fun parseNetworkError(volleyError: VolleyError): VolleyError {
        try {
            val responseBody = String(volleyError.networkResponse.data)
            apiResponseInfo.apply {
                isSuccess = false
                data = responseBody
                statusCode = volleyError.networkResponse.statusCode
                this@apply.header = "-"
            }
            callbackResponse.callback(apiResponseInfo)
        } catch (e: Exception) {
            apiResponseInfo.apply {
                isSuccess = false
                data = e.localizedMessage ?: "-"
                statusCode = 0
                this@apply.header = "-"
            }
            callbackResponse.callback(apiResponseInfo)

        }
        return super.parseNetworkError(volleyError)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<ApiResponseInfo>? {
        try {
            val responseHeaders = response.headers
            val responseHeaderString = StringBuilder()
            for (map in responseHeaders ?: emptyMap()) {
                responseHeaderString.append("${map.key}:${map.value}\n")
            }
            val responseBodyString = String(response.data) //Convert bytes to string
            apiResponseInfo.apply {
                header = responseHeaderString.toString()
                statusCode = response.statusCode
                isSuccess = response.statusCode == 200
                data = responseBodyString
                error = null
            }
        } catch (e: Exception) {
            apiResponseInfo.apply {
                header = ""
                statusCode = response.statusCode
                isSuccess = response.statusCode == 200
                data = e.localizedMessage
                error = null
            }
        }
        return Response.success(apiResponseInfo, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: ApiResponseInfo?) {
        response?.let { callbackResponse.callback(response) }
            ?: kotlin.run { callbackResponse.callback(this@MetaRequest.apiResponseInfo) }
    }
}