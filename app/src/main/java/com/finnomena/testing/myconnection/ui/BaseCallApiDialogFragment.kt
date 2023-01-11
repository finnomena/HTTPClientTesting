package com.finnomena.testing.myconnection.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.finnomena.testing.myconnection.databinding.FragmentResultBinding
import java.text.NumberFormat

abstract class BaseCallApiDialogFragment : BaseDialogFragment() {
    protected lateinit var binding: FragmentResultBinding
    private var startTimeLoading = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { dismiss() }
    }

    fun showLoading() {
        binding.scrollView.isVisible = false
        binding.loading.isVisible = true
        startTimeLoading = System.currentTimeMillis()
    }

    fun hideLoading() {
        binding.scrollView.isVisible = true
        binding.loading.isVisible = false
        binding.txtCountTimer.text = displayTotalTime(System.currentTimeMillis() - startTimeLoading)
    }

    fun showResponseSuccessStatus() {
        binding.apply {
            txtResponse.text = SpannableStringBuilder("Response status: ").append(
                getSpanColorText(
                    isSuccess = true,
                    "200 OK"
                )
            )
            txtResponseBody.isVisible = true
            txtResponseError.isVisible = false
            lineBody.isVisible = true
        }
    }

    fun showResponseErrorStatus(errorCode: String) {
        binding.apply {
            txtResponse.text = SpannableStringBuilder("Response status: ").append(
                getSpanColorText(
                    isSuccess = false,
                    errorCode
                )
            )
            txtResponseBody.isVisible = false
            txtResponseError.isVisible = true
            lineBody.isVisible = false
        }
    }

    fun setResponseHeader(text: String) {
        if (text == "-") {
            binding.txtResponseHeader.isVisible = false
            binding.lineHeader.isVisible = false
        } else {
            binding.txtResponseHeader.isVisible = true
            binding.lineHeader.isVisible = true
            val title = "Response Header"
            val spannableStringBuilder = SpannableStringBuilder(title)
            spannableStringBuilder.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                title.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableStringBuilder.append("\n\n$text")
            binding.txtResponseHeader.text = spannableStringBuilder
        }
    }

    fun setResponseBody(text: String) {
        val title = "Response Body"
        val spannableStringBuilder = SpannableStringBuilder(title)
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            title.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.append("\n\n$text")
        binding.txtResponseBody.text = spannableStringBuilder
    }

    fun setResponseError(text: String) {
        val title = "Response Error"
        val spannableStringBuilder = SpannableStringBuilder(title)
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            title.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.append("\n\n$text")
        binding.txtResponseError.text = spannableStringBuilder
    }

    private fun getSpanColorText(isSuccess: Boolean, text: String): SpannableStringBuilder {
        val colorSpan = if (isSuccess) {
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_green_dark
                )
            )
        } else {
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_red_dark
                )
            )
        }
        return SpannableStringBuilder(text).apply {
            setSpan(
                colorSpan,
                0,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun displayTotalTime(durationInMilliSec: Long): String {
        return "total time: ${NumberFormat.getInstance().format(durationInMilliSec)} ms"
    }
}