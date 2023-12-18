package kr.co.ho1.poopee.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.ArrayMap
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_kakao_keyword.view.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.LocationManager
import kr.co.ho1.poopee.common.util.LogManager
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.database.ToiletSQLiteManager
import kr.co.ho1.poopee.home.model.KaKaoKeyword
import kr.co.ho1.poopee.home.model.Toilet
import kr.co.ho1.poopee.home.view.FinishDialog
import kr.co.ho1.poopee.home.view.PopupDialog
import kr.co.ho1.poopee.home.view.ToiletDialog
import kr.co.ho1.poopee.manager.view.Toilet2ListDialog
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.json.JSONException

@Suppress("DEPRECATION")
class HomeActivity : BaseActivity(), MapView.POIItemEventListener, MapView.MapViewEventListener {

    private var keywordAdapter: ListAdapter = ListAdapter()
    private var keywordList: ArrayList<KaKaoKeyword> = ArrayList()

    private var isKeyboardShow = false // 키보드 노출 상태
    private val rvHeight = MyUtil.dpToPx(240)

    private var isMyPositionMove = true // 내위치기준으로 맵중심이동여부
    private var isFirstOnCreate = true // onCreate 체크 (내위치기준으로 맵중심을 이동할지 확인하기위해)
    private var lastLatitude: Double = 0.0 // 마지막 중심 latitude
    private var lastLongitude: Double = 0.0 // 마지막 중심 longitude

    private var interstitialAd1: InterstitialAd? = null

    private var mToilet: Toilet = Toilet()
    private var mToiletList: ArrayMap<Int, Toilet> = ArrayMap()

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magnetometer: Sensor

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        MobileAds.initialize(this) {}

        // 전면광고
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            MyUtil.getString(R.string.interstitial_ad_unit_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd1 = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitialAd1 = interstitialAd

                    interstitialAd1?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                ObserverManager.root!!.startActivity(
                                    Intent(ObserverManager.context!!, ToiletActivity::class.java)
                                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                                        .putExtra(ToiletActivity.TOILET, mToilet)
                                )
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                ObserverManager.root!!.startActivity(
                                    Intent(ObserverManager.context!!, ToiletActivity::class.java)
                                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                                        .putExtra(ToiletActivity.TOILET, mToilet)
                                )
                            }

                            override fun onAdShowedFullScreenContent() {
                                interstitialAd1 = null
                            }
                        }
                }
            })

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER) &&
            packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)
        ) {

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

            sensorManager.registerListener(
                sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            sensorManager.registerListener(
                sensorEventListener,
                magnetometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        init()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        LocationManager.setLocationListener() // 현재위치 리스너 추가

        sensorManager.registerListener(
            sensorEventListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            sensorEventListener,
            magnetometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        refresh()
    }

    override fun onPause() {
        super.onPause()
        LocationManager.removeLocationUpdate()
        map_view.removeAllViews()
        sensorManager.unregisterListener(sensorEventListener)
    }

    private fun init() {
        rv_search.layoutManager = LinearLayoutManager(this)
        keywordAdapter = ListAdapter()
        rv_search.adapter = keywordAdapter

        checkPopup()
    }

    private fun refresh() {
        if (SharedManager.getMemberUsername() == "master") {
            btn_manager.visibility = View.VISIBLE
        } else {
            btn_manager.visibility = View.GONE
        }

        nav_view.refresh()
        ObserverManager.mapView = MapView(this)
        map_view.addView(ObserverManager.mapView)

        // 현재위치기준으로 중심점변경
        if (isFirstOnCreate) {
            isFirstOnCreate = false
            if (SharedManager.getLatitude() > 0) {
                ObserverManager.mapView.setMapCenterPoint(
                    MapPoint.mapPointWithGeoCoord(
                        SharedManager.getLatitude(),
                        SharedManager.getLongitude()
                    ), false
                )
                ObserverManager.addMyPosition(
                    SharedManager.getLatitude(),
                    SharedManager.getLongitude()
                )
                setMyPosition(View.VISIBLE)
            }
        } else {
            if (lastLatitude == 0.0) {
                lastLatitude = ObserverManager.mapView.mapCenterPoint.mapPointGeoCoord.latitude
                lastLongitude = ObserverManager.mapView.mapCenterPoint.mapPointGeoCoord.longitude
            }
            ObserverManager.mapView.setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    lastLatitude,
                    lastLongitude
                ), false
            )
        }

        ObserverManager.mapView.setZoomLevel(3, true)
        ObserverManager.mapView.setPOIItemEventListener(this)
        ObserverManager.mapView.setMapViewEventListener(this)

        if (SharedManager.getReviewCount() == ToiletActivity.REVIEW_COUNT) {
            SharedManager.setReviewCount(SharedManager.getReviewCount() + 1)
            val manager = ReviewManagerFactory.create(ObserverManager.context!!)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener { }
                }
            }
        }
    }

    private fun setListener() {
        root_view.viewTreeObserver.addOnGlobalLayoutListener {
            LogManager.e("${MyUtil.getDeviceHeight()} : ${root_view.height}")
            if (MyUtil.getDeviceHeight() - MyUtil.dpToPx(100) > root_view.height) {
                // keyboard show
                isKeyboardShow = true
                layout_bottom_bg.visibility = View.VISIBLE
            } else {
                // keyboard hide
                isKeyboardShow = false
                if (rv_search.visibility == View.GONE) {
                    layout_bottom_bg.visibility = View.GONE
                } else {
                    if (rv_search.height > rvHeight) {
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            rvHeight
                        )
                        rv_search.layoutParams = layoutParams
                    }
                }
                layout_bottom_bg.visibility = View.GONE
            }
        }
        root_view.setOnClickListener {
            MyUtil.keyboardHide(edt_search)
        }
        layout_bottom_bg.setOnClickListener {
            MyUtil.keyboardHide(edt_search)
        }
        btn_search_delete.setOnClickListener {
            edt_search.setText("")
            rv_search.visibility = View.GONE
        }
        edt_search.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && keywordList.size > 0) {
                setKakaoLocal(keywordList[0])
                return@setOnKeyListener true
            }
            false
        }
        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) {
                    rv_search.visibility = View.GONE
                } else {
                    rv_search.visibility = View.VISIBLE
                    taskKakaoLocalSearch(p0.toString())
                }
            }
        })
        layout_my_position.setOnClickListener {
            if (SharedManager.getLatitude() > 0) {
                isMyPositionMove = true
                ObserverManager.addMyPosition(
                    SharedManager.getLatitude(),
                    SharedManager.getLongitude()
                )
                ObserverManager.mapView.setMapCenterPoint(
                    MapPoint.mapPointWithGeoCoord(
                        SharedManager.getLatitude(),
                        SharedManager.getLongitude()
                    ), false
                )
                setMyPosition(View.VISIBLE)
            }
        }
        btn_menu.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }
        btn_manager.setOnClickListener {
            val dialog = Toilet2ListDialog(onMove = {
                isMyPositionMove = false
                ObserverManager.mapView.setMapCenterPoint(
                    MapPoint.mapPointWithGeoCoord(
                        it.latitude,
                        it.longitude
                    ), true
                )
                setMyPosition(View.GONE)
            })
            dialog.show(supportFragmentManager, "Toilet2ListDialog")
        }
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // 센서 정확도 변경 처리
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor == null) return

            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> System.arraycopy(
                    event.values,
                    0,
                    accelerometerReading,
                    0,
                    accelerometerReading.size
                )

                Sensor.TYPE_MAGNETIC_FIELD -> System.arraycopy(
                    event.values,
                    0,
                    magnetometerReading,
                    0,
                    magnetometerReading.size
                )
            }

            SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading
            )
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            // 방향 각도는 orientationAngles 배열에 저장됩니다.
            // val pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
            // val roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
            val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            ObserverManager.my_position_rotation = azimuth
            if (ObserverManager.my_position != null) {
                ObserverManager.my_position!!.rotation = ObserverManager.my_position_rotation
            }
        }
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        isMyPositionMove = false
    }

    /**
     * 카카오지도 이동완료 콜백
     */
    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        lastLatitude = p0!!.mapCenterPoint.mapPointGeoCoord.latitude
        lastLongitude = p0.mapCenterPoint.mapPointGeoCoord.longitude

        ObserverManager.mapView.removeAllPOIItems()
        val toiletList =
            ToiletSQLiteManager.getInstance().getToiletList(lastLatitude, lastLongitude)
        for (toilet in toiletList) {
            ObserverManager.addPOIItem(toilet)
        }
        taskToiletList(lastLatitude, lastLongitude)

        if (SharedManager.getLatitude() > 0) {
            ObserverManager.addMyPosition(SharedManager.getLatitude(), SharedManager.getLongitude())
        }
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        setMyPosition(View.GONE)
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

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }

    /**
     * 카카오지도 아이템 클릭
     */
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        if (p1!!.tag > 0) {
            val toilet = ToiletSQLiteManager.getInstance().getToilet(p1.tag)

            val dialog = ToiletDialog(
                onDetail = {
                    mToilet = it
                    if (interstitialAd1 != null) {
                        interstitialAd1?.show(this)
                    } else {
                        map_view.removeAllViews()
                        ObserverManager.root!!.startActivity(
                            Intent(ObserverManager.context!!, ToiletActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                                .putExtra(ToiletActivity.TOILET, mToilet)
                        )
                    }
                }
            )
            dialog.setToilet(toilet)
            dialog.show(supportFragmentManager, "ToiletDialog")
        } else if (p1.tag < 0) {
            mToiletList[p1.tag]?.let { toilet ->
                val dialog = ToiletDialog(
                    onDetail = {
                        mToilet = it
                        if (interstitialAd1 != null) {
                            interstitialAd1?.show(this)
                        } else {
                            map_view.removeAllViews()
                            ObserverManager.root!!.startActivity(
                                Intent(ObserverManager.context!!, ToiletActivity::class.java)
                                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                                    .putExtra(ToiletActivity.TOILET, mToilet)
                            )
                        }
                    }
                )
                dialog.setToilet(toilet)
                dialog.show(supportFragmentManager, "ToiletDialog")
            }
        }
    }

    private fun checkPopup() {
        if (SharedManager.getNoticeImage().count() > 0) {
            val dialog = PopupDialog()
            dialog.show(supportFragmentManager, "PopupDialog")
        }
    }

    private fun setMyPosition(visibility: Int) {
        lottie_my_position.visibility = visibility
        if (visibility == View.VISIBLE) {
            lottie_my_position.playAnimation()
        } else {
            lottie_my_position.cancelAnimation()
        }
    }

    private fun setKakaoLocal(kaKaoKeyword: KaKaoKeyword) {
        isMyPositionMove = false
        edt_search.setText(kaKaoKeyword.place_name)
        edt_search.setSelection(kaKaoKeyword.place_name.count())
        ObserverManager.mapView.setMapCenterPoint(
            MapPoint.mapPointWithGeoCoord(
                kaKaoKeyword.latitude,
                kaKaoKeyword.longitude
            ), true
        )
        MyUtil.keyboardHide(edt_search)
        rv_search.visibility = View.GONE
        setMyPosition(View.GONE)
    }

    /**
     * [GET] 화장실목록
     */
    private fun taskToiletList(latitude: Double, longitude: Double) {
        val params = RetrofitParams()
        params.put("latitude", latitude)
        params.put("longitude", longitude)

        val request =
            RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java)
                .toiletList(params.getParams())

        RetrofitJSONObject(request,
            onSuccess = {
                try {
                    if (it.getInt("rst_code") == 0) {
                        val jsonArray = it.getJSONArray("toilets")
                        mToiletList = ArrayMap()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val toilet = Toilet()
                            toilet.toilet_id = jsonObject.getInt("toilet_id")
                            toilet.member_id = jsonObject.getString("member_id")
                            toilet.type = "유저"
                            toilet.m_name = jsonObject.getString("m_name")
                            toilet.name = jsonObject.getString("name")
                            toilet.content = jsonObject.getString("content")
                            toilet.address_new = jsonObject.getString("address_new")
                            toilet.address_old = jsonObject.getString("address_old")
                            toilet.unisex = if (jsonObject.getInt("unisex") == 1) "Y" else "N"
                            toilet.m_poo = jsonObject.getString("man")
                            toilet.w_poo = jsonObject.getString("woman")
                            toilet.latitude = jsonObject.getDouble("latitude")
                            toilet.longitude = jsonObject.getDouble("longitude")
                            mToiletList[toilet.toilet_id] = toilet
                            ObserverManager.addPOIItem(toilet)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            onFailed = {
            }
        )
    }

    /**
     * [GET] 카카오지도 키워드 검색
     */
    private fun taskKakaoLocalSearch(query: String) {
        val params = RetrofitParams()
        params.put("query", query) // 검색을 원하는 질의어

        val request = RetrofitClient.getClientKaKao(RetrofitService.KAKAO_LOCAL)
            .create(RetrofitService::class.java).kakaoLocalSearch(params.getParams())

        RetrofitJSONObject(request,
            onSuccess = {
                try {
                    keywordList = ArrayList()
                    val jsonArray = it.getJSONArray("documents")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val keyword = KaKaoKeyword()
                        keyword.address_name = jsonObject.getString("address_name")
                        keyword.place_name = jsonObject.getString("place_name")
                        keyword.latitude = jsonObject.getDouble("y")
                        keyword.longitude = jsonObject.getDouble("x")

                        keywordList.add(keyword)
                    }

                    keywordAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            onFailed = {

            }
        )
    }

    /**
     * 주소검색 목록 adapter
     */
    inner class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_kakao_keyword, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ViewHolder).update(position)
        }

        override fun getItemCount(): Int {
            return keywordList.size
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            @SuppressLint("SetTextI18n")
            fun update(position: Int) {
                itemView.tv_title.text = keywordList[position].place_name
                itemView.tv_sub.text = keywordList[position].address_name

                itemView.setOnClickListener {
                    setKakaoLocal(keywordList[position])
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        // 현재위치기준으로 중심점변경
        if (isMyPositionMove && SharedManager.getLatitude() > 0) {
            ObserverManager.mapView.setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    SharedManager.getLatitude(),
                    SharedManager.getLongitude()
                ), false
            )
            ObserverManager.addMyPosition(SharedManager.getLatitude(), SharedManager.getLongitude())
            setMyPosition(View.VISIBLE)
        }
    }

    override fun onBackPressed() {
        if (isShowLoading()) {
            return
        }

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        }

        if (layout_bottom_bg.visibility == View.VISIBLE || rv_search.visibility == View.VISIBLE) {
            layout_bottom_bg.visibility = View.GONE
            rv_search.visibility = View.GONE
            return
        }
//        finish()
        val dialog = FinishDialog()
        dialog.show(supportFragmentManager, "FinishDialog")
    }

}