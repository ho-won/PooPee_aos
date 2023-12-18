package kr.co.ho1.poopee.common.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.widget.FrameLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager

class AdView : FrameLayout {
    private var context: Context
    private lateinit var adView: AdView
    private var initialLayoutComplete = false

    private val adSize: AdSize
        @SuppressLint("VisibleForTests")
        get() {
            val display = ObserverManager.root!!.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val widthPixels = outMetrics.widthPixels
            val density = outMetrics.density
            val adWidth = (widthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
        }

    constructor(context: Context) : super(context) {
        this.context = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        this.context = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.context = context
        init()
    }

    private fun init() {
        adView = AdView(context)
        this.addView(adView)
        this.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner(adView)
            }
        }
    }

    private fun loadBanner(adView: AdView) {
        adView.adUnitId = ObserverManager.context!!.getString(R.string.banner_ad_unit_id)
        adView.setAdSize(adSize)
        adView.loadAd(AdRequest.Builder().build())
    }

}
