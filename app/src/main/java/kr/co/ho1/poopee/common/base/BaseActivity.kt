package kr.co.ho1.poopee.common.base

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.util.LogManager
import kr.co.ho1.poopee.menu.SettingActivity

abstract class BaseActivity : AppCompatActivity() {
    private var progressView: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogManager.e("Activity_onCreate", javaClass.name)
        handleIntent(intent)
        init()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        LogManager.e("Activity_onNewIntent", javaClass.name)
        handleIntent(intent)
    }

    private fun init() {
        ObserverManager.root = this
    }

    override fun onResume() {
        super.onResume()
        ObserverManager.root = this
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW && intent.data?.scheme == "poopee") {
            val host = intent.data?.host
            val path = intent.data?.path
            val appLinkData = intent.data
            LogManager.e("handleIntent : $host, $path")
            when {
                host == "settings" -> {
                    startActivity(Intent(this, SettingActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    )
                }
            }
        }
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