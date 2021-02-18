package kr.co.ho1.poopee.common.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import kr.co.ho1.poopee.R

abstract class BaseDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.MyDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) // 상태바 알파 백그라운드
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isCancelable) {
            view.setOnClickListener { dismiss() }
        }
    }
    private var progressView: RelativeLayout? = null

    @SuppressLint("InflateParams")
    fun showLoading() {
        dialog?.let {
            if (progressView == null) {
                progressView = layoutInflater.inflate(R.layout.view_progress, null) as RelativeLayout
                it.addContentView(progressView!!, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
            }
            progressView!!.visibility = View.VISIBLE
        }
    }

    fun hideLoading() {
        if (progressView != null) {
            progressView!!.visibility = View.GONE
        }
    }

}