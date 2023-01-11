package com.finnomena.testing.myconnection.volley

import androidx.lifecycle.lifecycleScope
import com.android.volley.Request.Method
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.finnomena.testing.myconnection.Constant
import com.finnomena.testing.myconnection.data.ApiResponseInfo
import com.finnomena.testing.myconnection.ui.BaseCallApiDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VolleyDialogFragment : BaseCallApiDialogFragment() {

    companion object {
        const val TAG = "VolleyDialogFragment"
    }

    override fun callApi() {
        showLoading()
        val queue = Volley.newRequestQueue(requireContext())
        val url = Constant.BASE_URL + Constant.PATH_GET_VERSION
        val stringRequest = MetaRequest(
            object : MetaRequest.CallbackResponse {
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
            },
            Method.GET,
            url,
            {},
        )
        queue.add(stringRequest)
    }

    override fun setPageTitle() {
        binding.txtTitle.text = "Volley"
    }
}