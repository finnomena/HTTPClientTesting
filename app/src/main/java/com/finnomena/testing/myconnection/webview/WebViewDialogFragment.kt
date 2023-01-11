package com.finnomena.testing.myconnection.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.finnomena.testing.myconnection.Constant
import com.finnomena.testing.myconnection.databinding.FragmentWebviewBinding
import com.finnomena.testing.myconnection.ui.BaseDialogFragment

class WebViewDialogFragment : BaseDialogFragment() {
    companion object {
        const val TAG = "WebViewDialogFragment"
    }

    override fun setPageTitle() {
        binding.txtTitle.text = "WebView"
    }

    lateinit var binding: FragmentWebviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            WebView.setWebContentsDebuggingEnabled(true)
            webview.apply {
                settings.apply {
                    domStorageEnabled = true
                    saveFormData = false
                    javaScriptEnabled = true
                }
                webChromeClient = object : WebChromeClient() {}
            }
            btnBack.setOnClickListener { dismiss() }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun callApi() {
        binding.webview.loadUrl(Constant.BASE_URL + Constant.PATH_GET_VERSION)
    }
}