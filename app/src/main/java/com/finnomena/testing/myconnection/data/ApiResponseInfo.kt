package com.finnomena.testing.myconnection.data

class ApiResponseInfo {
    var header: String? = null
    var isSuccess: Boolean = false
    var statusCode: Int = 0
    var data: String? = null
    var error: Throwable? = null
}