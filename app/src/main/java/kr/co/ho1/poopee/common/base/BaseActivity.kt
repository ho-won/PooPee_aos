package kr.co.ho1.poopee.common.base

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager

abstract class BaseActivity : AppCompatActivity() {
    private var progressView: RelativeLayout? = null

    private var parentRoot: BaseActivity? = null // 직전 Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        parentRoot = ObserverManager.root
        ObserverManager.root = this
    }

    override fun onResume() {
        super.onResume()
        ObserverManager.root = this
    }

    @SuppressLint("InflateParams")
    fun showLoading() {
        if (progressView == null) {
            progressView = layoutInflater.inflate(R.layout.view_progress, null) as RelativeLayout
            addContentView(progressView, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
        }
        progressView!!.visibility = View.VISIBLE
    }

    fun hideLoading() {
        if (progressView != null) {
            progressView!!.visibility = View.GONE
        }
    }

    fun isShowLoading(): Boolean {
        return progressView != null && progressView!!.visibility == View.VISIBLE
    }

    open fun setToolbar() {

    }

    open fun onLocationChanged(location: Location) {
        kr.co.ho1.poopee.common.util.LogManager.e(location.bearing)
    }

}