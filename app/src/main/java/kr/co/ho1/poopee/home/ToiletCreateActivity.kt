package kr.co.ho1.poopee.home

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import kotlinx.android.synthetic.main.activity_toilet_create.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.util.LocationManager
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.home.model.KaKaoKeyword
import kr.co.ho1.poopee.home.view.ToiletCreateDialog

class ToiletCreateActivity : BaseActivity() {

    companion object {
        const val KAKAO_KEYWORD = "KaKaoKeyword"
    }

    private var keyword: KaKaoKeyword? = null

    var kakaoMap: KakaoMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toilet_create)
        setToolbar()

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
        keyword = intent.getSerializableExtra(KAKAO_KEYWORD) as? KaKaoKeyword

        map_view.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
                kakaoMap = map

                if (keyword != null) {
                    // 카카오검색기준으로 중심점변경
                    kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(keyword!!.latitude, keyword!!.longitude)))
                } else if (SharedManager.getLatitude() > 0) {
                    // 현재위치기준으로 중심점변경
                    kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(SharedManager.getLatitude(), SharedManager.getLongitude())))
                }

                kakaoMap?.moveCamera(CameraUpdateFactory.zoomTo(ObserverManager.BASE_ZOOM_LEVEL))
            }
        })
    }

    private fun setListener() {
        btn_my_position.setOnClickListener {
            if (SharedManager.getLatitude() > 0) {
                kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(SharedManager.getLatitude(), SharedManager.getLongitude())))
                kakaoMap?.moveCamera(CameraUpdateFactory.zoomTo(ObserverManager.BASE_ZOOM_LEVEL))
            }
        }
        btn_bottom.setOnClickListener {
            val dialog = ToiletCreateDialog(
                kakaoMap?.cameraPosition?.position?.latitude!!,
                kakaoMap?.cameraPosition?.position?.longitude!!,
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