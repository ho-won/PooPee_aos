@file:Suppress("DEPRECATION")

package kr.co.ho1.poopee.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.data.SharedManager
import java.io.File

@SuppressLint("StaticFieldLeak")
object ObserverManager {
    val BASE_ZOOM_LEVEL = 14
    var testServer = false // 테스트용인지 체크
    var isShowLog = true // Log 노출여부 체크
    var serverException = false // Exception 통합관리 및 서버로 보낼지 체크

    var root: BaseActivity? = null  // 현재 Activity
    var context: Context? = null

    fun getPath(): String {
        return Environment.getExternalStorageDirectory().toString() + File.separator + "PooPee" + File.separator
    }

    fun logout() {
        SharedManager.isLoginCheck = false
        SharedManager.memberId = ""
        SharedManager.memberUsername = ""
        SharedManager.memberPassword = ""
        SharedManager.memberName = ""
        SharedManager.memberGender = "1"
    }

    fun restart() {
        context!!.startActivity(
            Intent(context, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        )
        root!!.finish()
    }

    /**
     * 앱의 플레이스토어로 이동.
     */
    fun updateInPlayMarket() {
        val appPackageName = context!!.packageName
        try {
            root!!.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        } catch (e: android.content.ActivityNotFoundException) {
            root!!.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
    }

}