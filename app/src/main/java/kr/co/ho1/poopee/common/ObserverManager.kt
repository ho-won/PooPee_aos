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
    lateinit var kakaoMap: KakaoMap
    var my_position: Label? = null // 내위치마커
    var my_position_rotation: Float = 0f

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

    /**
     * 카카오지도 아이템 추가
     */
    fun addPOIItem(toilet: Toilet) {
        var imageResourceId = R.drawable.ic_position
        if (toilet.toilet_id < 0) {
            imageResourceId = R.drawable.ic_position_up
        }
        kakaoMap.labelManager!!.layer!!.addLabel(LabelOptions.from("${toilet.toilet_id}", LatLng.from(toilet.latitude, toilet.longitude)).setStyles(LabelStyle.from(imageResourceId).setApplyDpScale(false)))
    }

    /**
     * 카카오지도 나의위치 추가
     */
    fun addMyPosition(latitude: Double, longitude: Double) {
        if (my_position != null) {
            kakaoMap.labelManager!!.layer!!.remove(my_position!!)
        }
        my_position = kakaoMap.labelManager!!.layer!!.addLabel(LabelOptions.from("0", LatLng.from(latitude, longitude)).setStyles(LabelStyle.from(R.drawable.ic_marker).setApplyDpScale(false)))
        //my_position!!.rotation = my_position_rotation
    }

}