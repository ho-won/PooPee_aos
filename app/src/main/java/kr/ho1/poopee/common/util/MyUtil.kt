@file:Suppress("DEPRECATION")

package kr.ho1.poopee.common.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import kr.ho1.poopee.common.ObserverManager
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.security.MessageDigest

object MyUtil {

    fun getDeviceHeight(): Int {
        val display = ObserverManager.root!!.windowManager.defaultDisplay
        return display.height - getStatusBarHeight()
    }

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), ObserverManager.context!!.resources.displayMetrics).toInt()
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = ObserverManager.context!!.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = ObserverManager.context!!.resources.getDimensionPixelSize(resourceId)
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

    @SuppressLint("PackageManagerGetSignatures")
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

    @Throws(IOException::class)
    fun copyFile(fromFile: FileInputStream, toFile: FileOutputStream) {
        var fromChannel: FileChannel? = null
        var toChannel: FileChannel? = null
        try {
            fromChannel = fromFile.channel
            toChannel = toFile.channel
            fromChannel!!.transferTo(0, fromChannel.size(), toChannel)
        } finally {
            try {
                fromChannel?.close()
            } finally {
                toChannel?.close()
            }
        }
    }

}