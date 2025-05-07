package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.databinding.DialogFinishBinding

@SuppressLint("ValidFragment")
class FinishDialog() : BaseDialog() {
    private var _binding: DialogFinishBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogFinishBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        //binding.webView.webViewClient = WebViewClient()
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                if (url.startsWith("http")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    return true
                }
                return false
            }
        }

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
        binding.btnNo.setOnClickListener {
            dismiss()
        }
        binding.btnYes.setOnClickListener {
            ObserverManager.root!!.finish()
        }
    }

}