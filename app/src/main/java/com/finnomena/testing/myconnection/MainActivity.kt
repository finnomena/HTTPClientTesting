package com.finnomena.testing.myconnection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.finnomena.testing.myconnection.asynctask.AsyncTaskDialogFragment
import com.finnomena.testing.myconnection.cronet.CronetDialogFragment
import com.finnomena.testing.myconnection.databinding.ActivityMainBinding
import com.finnomena.testing.myconnection.okhttp.OkHttpDialogFragment
import com.finnomena.testing.myconnection.okhttp.OkHttpWithCallDialogFragment
import com.finnomena.testing.myconnection.volley.VolleyDialogFragment
import com.finnomena.testing.myconnection.webview.WebViewDialogFragment

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            btnAsyncTask.setOnClickListener {
                AsyncTaskDialogFragment().show(supportFragmentManager, AsyncTaskDialogFragment.TAG)
            }
            btnOkHttp.setOnClickListener {
                OkHttpDialogFragment().show(supportFragmentManager, OkHttpDialogFragment.TAG)
            }
            btnOkHttpWithCall.setOnClickListener {
                OkHttpWithCallDialogFragment().show(supportFragmentManager, OkHttpWithCallDialogFragment.TAG)
            }
            btnCronet.setOnClickListener {
                CronetDialogFragment().show(supportFragmentManager, CronetDialogFragment.TAG)
            }
            btnVolley.setOnClickListener {
                VolleyDialogFragment().show(supportFragmentManager, VolleyDialogFragment.TAG)
            }
            btnWebview.setOnClickListener {
                WebViewDialogFragment().show(supportFragmentManager, WebViewDialogFragment.TAG)
            }
        }
    }
}