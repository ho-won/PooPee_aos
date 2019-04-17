package kr.ho1.poopee.common.util

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import kr.ho1.poopee.common.ObserverManager

object MyUtil {

    fun getDeviceHeight(): Int {
        val display = ObserverManager.root!!.getWindowManager().getDefaultDisplay()
        return display.getHeight() - getStatusBarHeight()
    }

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), ObserverManager.context!!.getResources().getDisplayMetrics()).toInt()
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = ObserverManager.context!!.getResources().getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = ObserverManager.context!!.getResources().getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun keyboardShow(view: View) {
        val imm = ObserverManager.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    fun keyboardHide(view: View) {
        val imm = ObserverManager.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}