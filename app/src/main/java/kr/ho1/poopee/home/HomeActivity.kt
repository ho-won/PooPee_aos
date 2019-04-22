package kr.ho1.poopee.home

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import kotlinx.android.synthetic.main.activity_home.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.util.MyUtil
import kr.ho1.poopee.home.model.Toilet
import kr.ho1.poopee.home.view.ToiletDialog
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.security.MessageDigest

@Suppress("DEPRECATION")
class HomeActivity : BaseActivity(), MapView.POIItemEventListener, MapView.MapViewEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setToolbar()

        init()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        nav_view.refresh()
    }

    private fun init() {
        // java code
        val mapView = MapView(this)

        map_view.addView(mapView)

        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true)

        // 줌 레벨 변경
//        mapView.setZoomLevel(7, true);

        // 중심점 변경 + 줌 레벨 변경
//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);

        // 줌 인
//        mapView.zoomIn(true);

        // 줌 아웃
//        mapView.zoomOut(true);

        val marker = MapPOIItem()
        marker.itemName = "Default Marker"
        marker.tag = 1001
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633)
//        marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
        marker.markerType = MapPOIItem.MarkerType.CustomImage // 마커타입을 커스텀 마커로 지정.
        marker.customImageResourceId = R.drawable.ic_marker // 마커 이미지.
        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.customSelectedImageResourceId = R.drawable.ic_marker // 마커 이미지.
        marker.isShowCalloutBalloonOnTouch = false


        mapView.addPOIItem(marker)

        mapView.setPOIItemEventListener(this)
        mapView.setMapViewEventListener(this)
    }

    private fun setListener() {
        root_view.setOnClickListener {
            MyUtil.keyboardHide(edt_search)
        }
        btn_search_delete.setOnClickListener {
            edt_search.setText("")
        }
        btn_current_location.setOnClickListener {

        }
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        val latitude = p0!!.mapCenterPoint.mapPointGeoCoord.latitude
        val longitude = p0.mapCenterPoint.mapPointGeoCoord.longitude
        Log.e("MapView_MoveFinished", "latitude: $latitude longitude: $longitude")
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        Log.e("onPOIItemSelected", "tag: ${p1!!.tag}")
        val toilet = Toilet()
        toilet.title = "공중화장실"
        toilet.content = "화장실 상세정보"

        val dialog = ToiletDialog()
        dialog.setToilet(toilet)
        dialog.show(supportFragmentManager, "ToiletDialog")
    }

    override fun setToolbar() {
        toolbar.setImageLeftOne(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_bar_menu))
        toolbar.setSelectedListener(
                onBtnLeftOne = {
                    drawer_layout.openDrawer(Gravity.START)
                },
                onBtnLeftTwo = {

                },
                onBtnRightOne = {

                },
                onBtnRightTwo = {

                }
        )
    }

    override fun onBackPressed() {
        if (isShowLoading()) {
            return
        }

        if (drawer_layout.isDrawerOpen(Gravity.START)) {
            drawer_layout.closeDrawer(Gravity.START)
            return
        }

        finish()
    }

}