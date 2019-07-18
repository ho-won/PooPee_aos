package kr.ho1.poopee.common.base

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager

abstract class BaseActivity : AppCompatActivity() {
    private var progressView: RelativeLayout? = null

    var parentRoot: BaseActivity? = null; // 직전 Activity

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

    override fun finish() {
        super.finish()
    }

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

    /**
     * Fcm message 가 왔을 경우 호출.
     */
    open fun onFcm() {

    }

    /**
     * Network 가 변경됐을 경우 호출.
     */
    open fun onNetworkChanged() {

    }

    open fun setToolbar() {

    }

}