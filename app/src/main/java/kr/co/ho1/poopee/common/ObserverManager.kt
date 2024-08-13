@file:Suppress("DEPRECATION")

package kr.co.ho1.poopee.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.TransformMethod
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.home.model.Toilet
import java.io.File

@SuppressLint("StaticFieldLeak")
object ObserverManager {
    val BASE_ZOOM_LEVEL = 14
    var testServer = false // 테스트용인지 체크
    var isShowLog = true // Log 노출여부 체크
    var serverException = false // Exception 통합관리 및 서버로 보낼지 체크

    var root: BaseActivity? = null  // 현재 Activity
    var context: Context? = null
    //var kakaoMap: KakaoMap? = null

    fun getPath(): String {
        return Environment.getExternalStorageDirectory().toString() + File.separator + "PooPee" + File.separator
    }

    fun logout() {
        SharedManager.setLoginCheck(false)
        SharedManager.setMemberId("")
        SharedManager.setMemberUsername("")
        SharedManager.setMemberPassword("")
        SharedManager.setMemberName("")
        SharedManager.setMemberGender("1")
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