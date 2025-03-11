package kr.co.ho1.poopee.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.webkit.WebSettings
import android.webkit.WebViewClient
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.databinding.ActivityCoupangAdBinding

class CoupangAdActivity : BaseActivity() {
    private lateinit var binding: ActivityCoupangAdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoupangAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.hide(WindowInsets.Type.navigationBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        init()
        setListener()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.domStorageEnabled = true

        binding.webView.webViewClient = WebViewClient()

        val htmlData = """
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <script src="https://ads-partners.coupang.com/g.js"></script>
            </head>
            <body style="margin:0;padding:0;display:flex;justify-content:center;align-items:center;height:100vh;">
                <script>
                    new PartnersCoupang.G({
                        "id":846353,
                        "template":"carousel",
                        "trackingCode":"AF3689916",
                        "width":"340",
                        "height":"340",
                        "tsource":""
                    });
                </script>
            </body>
            </html>
        """.trimIndent()

        binding.webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
    }

    private fun setListener() {
        binding.btnClose.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}