package kr.ho1.poopee.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_terms.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.http.RetrofitService
import java.util.*

@Suppress("DEPRECATION")
class TermsActivity : BaseActivity() {
    companion object {
        const val ACTION_TERMS_01 = "ACTION_TERMS_01" // 개인정보 처리방침
        const val ACTION_TERMS_02 = "ACTION_TERMS_02" // 서비스 이용약관
        const val ACTION_TERMS_03 = "ACTION_TERMS_03" // 위치정보기반 서비스 이용약관
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
        setToolbar()

        init()
        setListener()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        wb_terms.settings.javaScriptEnabled = true
        wb_terms.webViewClient = HelloWebViewClient()

        when {
            Objects.requireNonNull(intent.action) == ACTION_TERMS_01 -> wb_terms.loadUrl(RetrofitService.BASE_APP + RetrofitService.TERMS_01)
            Objects.requireNonNull(intent.action) == ACTION_TERMS_02 -> wb_terms.loadUrl(RetrofitService.BASE_APP + RetrofitService.TERMS_02)
            Objects.requireNonNull(intent.action) == ACTION_TERMS_03 -> wb_terms.loadUrl(RetrofitService.BASE_APP + RetrofitService.TERMS_03)
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
        toolbar.setImageLeftOne(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_navigationbar_back))
        toolbar.setSelectedListener(
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