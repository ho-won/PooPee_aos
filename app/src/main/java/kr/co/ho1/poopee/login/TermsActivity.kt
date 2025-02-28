package kr.co.ho1.poopee.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.databinding.ActivityTermsBinding
import java.util.Objects

@Suppress("DEPRECATION")
class TermsActivity : BaseActivity() {
    private lateinit var binding: ActivityTermsBinding

    companion object {
        const val ACTION_TERMS_01 = "ACTION_TERMS_01" // 개인정보 처리방침
        const val ACTION_TERMS_02 = "ACTION_TERMS_02" // 서비스 이용약관
        const val ACTION_TERMS_03 = "ACTION_TERMS_03" // 위치정보기반 서비스 이용약관
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        init()
        setListener()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        binding.wbTerms.settings.javaScriptEnabled = true
        binding.wbTerms.webViewClient = HelloWebViewClient()

        when {
            Objects.requireNonNull(intent.action) == ACTION_TERMS_01 -> binding.wbTerms.loadUrl(RetrofitService.BASE_APP + RetrofitService.TERMS_01)
            Objects.requireNonNull(intent.action) == ACTION_TERMS_02 -> binding.wbTerms.loadUrl(RetrofitService.BASE_APP + RetrofitService.TERMS_02)
            Objects.requireNonNull(intent.action) == ACTION_TERMS_03 -> binding.wbTerms.loadUrl(RetrofitService.BASE_APP + RetrofitService.TERMS_03)
        }
    }

    private fun setListener() {

    }

    private inner class HelloWebViewClient : WebViewClient() { //주소창 없앰
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    override fun setToolbar() {
        binding.toolbar.setImageLeftOne(MyUtil.getDrawable(R.drawable.ic_navigationbar_back))
        binding.toolbar.setSelectedListener(
            onBtnLeftOne = {
                finish()
            },
            onBtnLeftTwo = {

            },
            onBtnRightOne = {

            },
            onBtnRightTwo = {

            }
        )
    }

}