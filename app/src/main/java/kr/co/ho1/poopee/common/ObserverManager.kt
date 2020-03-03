@file:Suppress("DEPRECATION")

package kr.co.ho1.poopee.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Environment
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.home.model.Toilet
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.io.File


@SuppressLint("StaticFieldLeak")
object ObserverManager {
    var testServer = false // 테스트용인지 체크
    var isShowLog = true // Log 노출여부 체크
    var serverException = false // Exception 통합관리 및 서버로 보낼지 체크

    var root: BaseActivity? = null  // 현재 Activity
    var context: Context? = null
    lateinit var mapView: MapView
    var my_position: MapPOIItem? = null // 내위치마커

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
        context!!.startActivity(Intent(context, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        )
        root!!.finish()
    }

    /**
     * 카카오지도 아이템 추가
     */
    fun addPOIItem(toilet: Toilet) {
        val marker = MapPOIItem()
        marker.itemName = toilet.name
        marker.tag = toilet.toilet_id
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(toilet.latitude, toilet.longitude)
        marker.markerType = MapPOIItem.MarkerType.CustomImage // 마커타입을 커스텀 마커로 지정.
        marker.customImageResourceId = R.drawable.ic_position // 마커 이미지.
        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.customSelectedImageResourceId = R.drawable.ic_position // 마커 이미지.
        marker.isShowCalloutBalloonOnTouch = false
        marker.showAnimationType = MapPOIItem.ShowAnimationType.SpringFromGround
        mapView.addPOIItem(marker)
    }

    /**
     * 카카오지도 나의위치 추가
     */
    fun addMyPosition(latitude: Double, longitude: Double) {
        if (my_position != null) {
            mapView.removePOIItem(my_position)
        }
        my_position = MapPOIItem()
        my_position!!.itemName = "내위치"
        my_position!!.tag = -1
        my_position!!.mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
        my_position!!.markerType = MapPOIItem.MarkerType.CustomImage // 마커타입을 커스텀 마커로 지정.
        my_position!!.customImageResourceId = R.drawable.ic_marker // 마커 이미지.
        my_position!!.selectedMarkerType = MapPOIItem.MarkerType.CustomImage // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        my_position!!.customSelectedImageResourceId = R.drawable.ic_marker // 마커 이미지.
        my_position!!.isShowCalloutBalloonOnTouch = false
        my_position!!.setCustomImageAnchor(0.5f, 0.5f)
        mapView.addPOIItem(my_position!!)
    }

}