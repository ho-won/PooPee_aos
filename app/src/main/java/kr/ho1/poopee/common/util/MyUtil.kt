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
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationManager
import android.app.NotificationChannel
import androidx.core.content.ContextCompat.getSystemService
import android.os.Build
import android.util.DisplayMetrics


object MyUtil {

    fun getDeviceWidth(): Int {
        val display = ObserverManager.root!!.windowManager.defaultDisplay
        return display.width
    }

    fun getDeviceHeight(): Int {
        val display = ObserverManager.root!!.windowManager.defaultDisplay
        return display.height - getStatusBarHeight()
    }

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), ObserverManager.context!!.resources.displayMetrics).toInt()
    }

    fun pxToDp(px: Int): Int {
        val resources = ObserverManager.context!!.resources
        val metrics = resources.displayMetrics
        val dp = px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        return dp.toInt()
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
    fun getHashKey(): String {
        var hashKey = ""
        try {
            val info = ObserverManager.context!!.packageManager.getPackageInfo(ObserverManager.context!!.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val key = String(Base64.encode(md.digest(), 0))
                Log.e("Hash key:", key)
                hashKey = key
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return hashKey
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

    /**
     * device 의 앱 알림상태확인
     */
    fun areNotificationsEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = ObserverManager.context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!manager.areNotificationsEnabled()) {
                return false
            }
            val channels = manager.notificationChannels
            for (channel in channels) {
                if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                    return false
                }
            }
            return true
        } else {
            return NotificationManagerCompat.from(ObserverManager.context!!).areNotificationsEnabled()
        }
    }

}