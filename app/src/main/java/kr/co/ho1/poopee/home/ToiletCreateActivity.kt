package kr.co.ho1.poopee.home

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_toilet_create.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.util.LocationManager
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.home.model.KaKaoKeyword
import kr.co.ho1.poopee.home.view.ToiletCreateDialog
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class ToiletCreateActivity : BaseActivity() {

    companion object {
        const val KAKAO_KEYWORD = "KaKaoKeyword"
    }

    private var mKeyword: KaKaoKeyword? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toilet_create)
        setToolbar()

        ad_view.loadAd(AdRequest.Builder().build())

        init()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        LocationManager.setLocationListener() // 현재위치 리스너 추가
    }

    override fun onPause() {
        super.onPause()
        LocationManager.removeLocationUpdate()
    }

    private fun init() {
        mKeyword = intent.getSerializableExtra(KAKAO_KEYWORD) as? KaKaoKeyword

        ObserverManager.mapView = MapView(this)
        map_view.addView(ObserverManager.mapView)

        if (mKeyword != null) {
            // 카카오검색기준으로 중심점변경
            ObserverManager.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mKeyword!!.latitude, mKeyword!!.longitude), false)
        } else if (SharedManager.getLatitude() > 0) {
            // 현재위치기준으로 중심점변경
            ObserverManager.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(SharedManager.getLatitude(), SharedManager.getLongitude()), false)
        }

        ObserverManager.mapView.setZoomLevel(3, true)

    }

    private fun refresh() {

    }

    private fun setListener() {
        btn_my_position.setOnClickListener {
            if (SharedManager.getLatitude() > 0) {
                ObserverManager.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(SharedManager.getLatitude(), SharedManager.getLongitude()), false)
                ObserverManager.mapView.setZoomLevel(3, false)
            }
        }
        btn_bottom.setOnClickListener {
            val dialog = ToiletCreateDialog(
                    ObserverManager.mapView.mapCenterPoint.mapPointGeoCoord.latitude,
                    ObserverManager.mapView.mapCenterPoint.mapPointGeoCoord.longitude,
                    onCreate = {
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
            )
            dialog.show(supportFragmentManager, "ToiletCreateDialog")
        }
    }

    override fun onLocationChanged(location: Location) {

    }

    override fun setToolbar() {
        toolbar.setTitle(MyUtil.getString(R.string.toilet_create_text_03))
        toolbar.setImageLeftOne(MyUtil.getDrawable(R.drawable.ic_navigationbar_back))
        toolbar.setSelectedListener(
                onBtnLeftOne = {
                    finish()
                },
                onBtnLeftTwo = {

                },
                onBtnRightOne = {

                },
                onBtnRightTwo = {

                }
        )
    }

    override fun finish() {
        map_view.removeAllViews()
        super.finish()
    }

}