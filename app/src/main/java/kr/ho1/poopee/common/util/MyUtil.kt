package kr.ho1.poopee.common.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import kr.ho1.poopee.common.ObserverManager
import java.security.MessageDigest

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

    fun getHashKey() {
        try {
            val info = ObserverManager.context!!.packageManager.getPackageInfo(ObserverManager.context!!.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val key = String(Base64.encode(md.digest(), 0))
                Log.d("Hash key:", key)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}