package com.finnomena.testing.myconnection.cronet

import androidx.lifecycle.lifecycleScope
import com.finnomena.testing.myconnection.Constant
import com.finnomena.testing.myconnection.data.ApiResponseInfo
import com.finnomena.testing.myconnection.ui.BaseCallApiDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.chromium.net.CronetEngine
import org.chromium.net.UrlRequest
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CronetDialogFragment : BaseCallApiDialogFragment() {
    companion object {
        const val TAG = "CronetDialogFragment"
    }

    override fun callApi() {
        showLoading()
        val myBuilder = CronetEngine.Builder(context)
        val croNetEngine: CronetEngine = myBuilder.build()
        val executor: Executor = Executors.newSingleThreadExecutor()
        val requestBuilder = croNetEngine.newUrlRequestBuilder(
            Constant.BASE_URL + Constant.PATH_GET_VERSION,
            MyUrlRequestCallback(object : MyUrlRequestCallback.CallBackResponse {
                override fun callback(apiResponseInfo: ApiResponseInfo) {
                    lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
                            hideLoading()
                            setResponseHeader(apiResponseInfo.header ?: "-")
                            if (apiResponseInfo.isSuccess) {
                                showResponseSuccessStatus()
                                setResponseBody(apiResponseInfo.data ?: "-")
                            } else {
                                showResponseErrorStatus(apiResponseInfo.statusCode.toString())
                                setResponseError(
                                    apiResponseInfo.data ?: apiResponseInfo.error?.localizedMessage
                                    ?: "-"
                                )
                            }
                        }
                    }
                }
            }),
            executor
        )
        requestBuilder.setHttpMethod("GET")
        val request: UrlRequest = requestBuilder.build()
        request.start()
    }

    override fun setPageTitle() {
        binding.txtTitle.text = "Cronet"
    }
}