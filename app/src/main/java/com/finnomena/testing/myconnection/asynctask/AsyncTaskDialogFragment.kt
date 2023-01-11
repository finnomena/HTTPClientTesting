package com.finnomena.testing.myconnection.asynctask

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import com.finnomena.testing.myconnection.Constant
import com.finnomena.testing.myconnection.data.ApiResponseInfo
import com.finnomena.testing.myconnection.ui.BaseCallApiDialogFragment
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class AsyncTaskDialogFragment : BaseCallApiDialogFragment() {
    companion object {
        const val TAG = "AsyncTaskDialogFragment"
    }

    override fun callApi() {
        showLoading()
        CallApiAsyncTask().execute()
    }

    override fun setPageTitle() {
        binding.txtTitle.text = "AsyncTask"
    }

    @SuppressLint("StaticFieldLeak")
    inner class CallApiAsyncTask : AsyncTask<Void, Void, ApiResponseInfo>() {
        override fun doInBackground(vararg params: Void): ApiResponseInfo {
            val apiResponseInfo = ApiResponseInfo()
            try {
                val dtaHeader = StringBuilder()
                val url = URL(Constant.BASE_URL + Constant.PATH_GET_VERSION)
                val urlConnection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.requestMethod = "GET"
                urlConnection.connect()
                val statusCode: Int = urlConnection.responseCode
                for (map in urlConnection.headerFields) {
                    dtaHeader.append("${map.key} : ${map.value}\n")
                }
                apiResponseInfo.header = dtaHeader.toString()
                apiResponseInfo.statusCode = statusCode
                if (statusCode == 200) {
                    val dtaResponse = StringBuilder()
                    val it: InputStream = BufferedInputStream(urlConnection.inputStream)
                    val read = InputStreamReader(it)
                    val buff = BufferedReader(read)
                    var chunks: String
                    var tempChunk: String? = ""
                    while (tempChunk != null) {
                        tempChunk = buff.readLine()
                        if (tempChunk != null) {
                            chunks = tempChunk
                            dtaResponse.append(chunks + "\n")
                        }
                    }
                    apiResponseInfo.apply {
                        isSuccess = true
                        data = dtaResponse.toString()
                        error = null
                    }
                } else {
                    val dtaResponse = StringBuilder()
                    val errorInputSteam = BufferedInputStream(urlConnection.errorStream)
                    val read = InputStreamReader(errorInputSteam)
                    val buff = BufferedReader(read)
                    var chunks: String
                    var tempChunk: String? = ""
                    while (tempChunk != null) {
                        tempChunk = buff.readLine()
                        if (tempChunk != null) {
                            chunks = tempChunk
                            dtaResponse.append(chunks + "\n")
                        }
                    }
                    apiResponseInfo.apply {
                        isSuccess = false
                        data = dtaResponse.toString()
                        error = null
                    }
                }
            } catch (e: Exception) {
                apiResponseInfo.apply {
                    isSuccess = false
                    error = e
                    data = null
                }
            }

            return apiResponseInfo
        }

        override fun onPostExecute(result: ApiResponseInfo) {
            super.onPostExecute(result)
            hideLoading()
            setResponseHeader(result.header?:"-")
            if (result.isSuccess) {
                showResponseSuccessStatus()
                setResponseBody(result.data ?: "-")
            } else {
                if(result.statusCode == 0){
                    showResponseErrorStatus("-")
                }else{
                    showResponseErrorStatus(result.statusCode.toString())
                }
                setResponseError(
                    result.data ?: result.error?.localizedMessage ?: "-"
                )
            }
        }
    }
}