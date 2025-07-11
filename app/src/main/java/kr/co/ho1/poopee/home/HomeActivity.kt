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
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.ArrayMap
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.android.play.core.review.ReviewManagerFactory
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.TransformMethod
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
import kr.co.ho1.poopee.databinding.ActivityHomeBinding
import kr.co.ho1.poopee.databinding.ItemKakaoKeywordBinding
import kr.co.ho1.poopee.home.model.KaKaoKeyword
import kr.co.ho1.poopee.home.model.Toilet
import kr.co.ho1.poopee.home.view.FinishDialog
import kr.co.ho1.poopee.home.view.PopupDialog
import kr.co.ho1.poopee.home.view.RewardDialog
import kr.co.ho1.poopee.home.view.ToiletDialog
import kr.co.ho1.poopee.manager.view.Toilet2ListDialog
import org.json.JSONException

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding

    companion object {
        const val RESULT_COUPANG = 1001
    }

    private var keywordAdapter: ListAdapter = ListAdapter()
    private var keywordList: ArrayList<KaKaoKeyword> = ArrayList()

    private var isKeyboardShow = false // 키보드 노출 상태
    private val rvHeight = MyUtil.dpToPx(240)

    private var isMyPositionMove = true // 내위치기준으로 맵중심이동여부
    private var isFirstOnCreate = true // onCreate 체크 (내위치기준으로 맵중심을 이동할지 확인하기위해)
    private var lastLatitude: Double = 0.0 // 마지막 중심 latitude
    private var lastLongitude: Double = 0.0 // 마지막 중심 longitude

    private var mToilet: Toilet = Toilet()
    private var mToiletList: ArrayMap<Int, Toilet> = ArrayMap()

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magnetometer: Sensor

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    var kakaoMap: KakaoMap? = null
    var my_position: Label? = null // 내위치마커
    var my_position_rotation: Float = 0f

    private var interstitialAd: InterstitialAd? = null // 전면광고

    private var rewardedAd: RewardedAd? = null // 리워드
    private var rewardEarned = false // 리워드 광고 시청 체크

    private var rewardedInterstitialAd: RewardedInterstitialAd? = null // 보상형 전면광고

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        loadAdMobInterstitial() // 전면광고
        loadAdMobRewardedAd() // 리워드
        loadAdMobRewardedInterstitialAd() // 보상형 전면광고

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER) &&
            packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)
        ) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!

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
        binding.mapView.resume()
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
        binding.mapView.pause()
        LocationManager.removeLocationUpdate()
        sensorManager.unregisterListener(sensorEventListener)
    }

    private fun init() {
        binding.rvSearch.layoutManager = LinearLayoutManager(this)
        keywordAdapter = ListAdapter()
        binding.rvSearch.adapter = keywordAdapter

        checkPopup()
    }

    private fun refresh() {
        if (SharedManager.memberUsername == "master") {
            binding.btnManager.visibility = View.VISIBLE
        } else {
            binding.btnManager.visibility = View.GONE
        }

        binding.navView.refresh()

        binding.mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, object : KakaoMapReadyCallback() {
            @SuppressLint("DefaultLocale")
            override fun onMapReady(map: KakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
                kakaoMap = map

                kakaoMap?.let {
                    // 현재위치기준으로 중심점변경
                    if (isFirstOnCreate) {
                        isFirstOnCreate = false
                        if (SharedManager.latitude > 0) {
                            it.moveCamera(
                                CameraUpdateFactory.newCenterPosition(
                                    LatLng.from(
                                        SharedManager.latitude,
                                        SharedManager.longitude
                                    )
                                )
                            )
                            addMyPosition(SharedManager.latitude, SharedManager.longitude)
                            setMyPosition(View.VISIBLE)
                        }
                    } else {
                        if (lastLatitude == 0.0) {
                            lastLatitude = it.cameraPosition?.position?.latitude!!
                            lastLongitude = it.cameraPosition?.position?.longitude!!
                        }
                        it.moveCamera(
                            CameraUpdateFactory.newCenterPosition(
                                LatLng.from(
                                    lastLatitude,
                                    lastLongitude
                                )
                            )
                        )
                    }

                    it.moveCamera(CameraUpdateFactory.zoomTo(ObserverManager.BASE_ZOOM_LEVEL))

                    it.setOnCameraMoveStartListener { kakaoMap, gestureType ->
                        isMyPositionMove = false
                        setMyPosition(View.GONE)
                    }
                    it.setOnCameraMoveEndListener { kakaoMap, cameraPosition, gestureType ->
                        lastLatitude = kakaoMap.cameraPosition?.position?.latitude!!
                        lastLongitude = kakaoMap.cameraPosition?.position?.longitude!!

                        it.labelManager!!.removeAllLabelLayer()
                        val toiletList = ToiletSQLiteManager.getInstance()
                            .getToiletList(lastLatitude, lastLongitude)
                        for (toilet in toiletList) {
                            addPOIItem(toilet)
                        }
                        taskToiletList(lastLatitude, lastLongitude)

                        if (SharedManager.latitude > 0) {
                            addMyPosition(SharedManager.latitude, SharedManager.longitude)
                        }

                        LogManager.e("${kakaoMap.cameraPosition?.position?.latitude!!}, ${SharedManager.latitude}, ${kakaoMap.cameraPosition?.position?.longitude!!}, ${SharedManager.longitude}")
                        if (String.format(
                                "%.3f",
                                kakaoMap.cameraPosition?.position?.latitude!!
                            ) == String.format("%.3f", SharedManager.latitude)
                            && String.format(
                                "%.3f",
                                kakaoMap.cameraPosition?.position?.longitude!!
                            ) == String.format("%.3f", SharedManager.longitude)
                        ) {
                            setMyPosition(View.VISIBLE)
                        } else {
                            setMyPosition(View.GONE)
                        }
                    }
                    it.setOnLabelClickListener { kakaoMap, labelLayer, label ->
                        var toilet: Toilet? = null
                        if (label.labelId.toInt() > 0) {
                            toilet =
                                ToiletSQLiteManager.getInstance().getToilet(label.labelId.toInt())
                        } else if (label.labelId.toInt() < 0) {
                            mToiletList[label.labelId.toInt()]?.let { toilet1 ->
                                toilet = toilet1
                            }
                        }

                        toilet?.let { toilet1 ->
                            val dialog = ToiletDialog(
                                onDetail = { toilet2 ->
                                    mToilet = toilet2
                                    if (!isAdRemoved()) {
                                        showAdRemovalPopup() // 광고 제거 유도 팝업 띄우기
                                    } else {
                                        openToiletDetail() // 광고 제거된 경우 바로 상세보기로 이동
                                    }
//                                    ObserverManager.root!!.startActivityForResult(
//                                        Intent(ObserverManager.context!!, CoupangAdActivity::class.java)
//                                            .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION), RESULT_COUPANG
//                                    )
                                }
                            )
                            dialog.setToilet(toilet1)
                            dialog.show(supportFragmentManager, "ToiletDialog")
                        }
                    }
                }
            }
        })

        if (SharedManager.reviewCount == ToiletActivity.REVIEW_COUNT) {
            SharedManager.reviewCount = SharedManager.reviewCount + 1
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

    @SuppressLint("SetTextI18n")
    private fun setListener() {
        binding.rootView.viewTreeObserver.addOnGlobalLayoutListener {
            LogManager.e("${MyUtil.getDeviceHeight()} : ${binding.rootView.height}")
            if (MyUtil.getDeviceHeight() - MyUtil.dpToPx(100) > binding.rootView.height) {
                // keyboard show
                isKeyboardShow = true
                binding.layoutBottomBg.visibility = View.VISIBLE
            } else {
                // keyboard hide
                isKeyboardShow = false
                if (binding.rvSearch.visibility == View.GONE) {
                    binding.layoutBottomBg.visibility = View.GONE
                } else {
                    if (binding.rvSearch.height > rvHeight) {
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            rvHeight
                        )
                        binding.rvSearch.layoutParams = layoutParams
                    }
                }
                binding.layoutBottomBg.visibility = View.GONE
            }
        }
        binding.rootView.setOnClickListener {
            MyUtil.keyboardHide(binding.edtSearch)
        }
        binding.layoutBottomBg.setOnClickListener {
            MyUtil.keyboardHide(binding.edtSearch)
        }
        binding.btnSearchDelete.setOnClickListener {
            binding.edtSearch.setText("")
            binding.rvSearch.visibility = View.GONE
        }
        binding.edtSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && keywordList.size > 0) {
                setKakaoLocal(keywordList[0])
                return@setOnKeyListener true
            }
            false
        }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) {
                    binding.rvSearch.visibility = View.GONE
                } else {
                    binding.rvSearch.visibility = View.VISIBLE
                    taskKakaoLocalSearch(p0.toString())
                }
            }
        })
        binding.layoutMyPosition.setOnClickListener {
            kakaoMap?.let {
                if (SharedManager.latitude > 0) {
                    isMyPositionMove = true
                    addMyPosition(SharedManager.latitude, SharedManager.longitude)
                    it.moveCamera(
                        CameraUpdateFactory.newCenterPosition(
                            LatLng.from(
                                SharedManager.latitude,
                                SharedManager.longitude
                            )
                        )
                    )
                    setMyPosition(View.VISIBLE)
                }
            }
        }
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.btnManager.setOnClickListener {
            val dialog = Toilet2ListDialog(onMove = { toilet ->
                kakaoMap?.let {
                    isMyPositionMove = false
                    it.moveCamera(
                        CameraUpdateFactory.newCenterPosition(
                            LatLng.from(
                                toilet.latitude,
                                toilet.longitude
                            )
                        )
                    )
                    setMyPosition(View.GONE)
                }
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

            val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            if (my_position_rotation.toInt() != azimuth.toInt()) {
                my_position_rotation = azimuth
            }
            if (my_position != null) {
                //my_position!!.rotateTo(my_position_rotation)
            }
        }
    }

    // 광고제거 팝업
    fun showAdRemovalPopup() {
        if (SharedManager.rewardPopupCount == 0 || SharedManager.rewardPopupCount >= 5) {
            SharedManager.rewardPopupCount = 1
            val dialog = RewardDialog(
                onRewardedAd = {
                    showAdMobRewardedAd()
                },
                onRewardedInterstitialAd = {
                    showAdMobInterstitial()
                }
            )
            dialog.show(supportFragmentManager, "BasicDialog")
        } else {
            SharedManager.rewardPopupCount += 1
            showAdMobInterstitial()
        }
    }

    // 전면광고 준비
    @SuppressLint("VisibleForTests")
    private fun loadAdMobInterstitial() {
        InterstitialAd.load(
            this,
            MyUtil.getString(R.string.interstitial_ad_unit_id),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
            },
        )
    }

    // 전면광고 표시
    private fun showAdMobInterstitial() {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // 광고가 닫혔을 때 화장실 상세보기 화면으로 이동
                    openToiletDetail()

                    // 광고 객체 초기화 (다음 광고 로드를 위해)
                    interstitialAd = null
                    loadAdMobInterstitial() // 다음 광고 미리 로드
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // 광고 표시 실패 시 바로 상세보기 화면으로 이동
                    openToiletDetail()
                }
            }

            interstitialAd?.show(this)
        } else {
            // 광고가 없을 경우 바로 상세보기 화면으로 이동
            openToiletDetail()
        }
    }

    // 리워드 준비
    private fun loadAdMobRewardedAd() {
        RewardedAd.load(
            this,
            MyUtil.getString(R.string.rewarded_interstitial_id),
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    LogManager.e("$adError")
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    LogManager.e("Ad was loaded.")
                    rewardedAd = ad
                    rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            // Called when a click is recorded for an ad.
                            LogManager.e("Ad was clicked.")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            LogManager.e("Ad dismissed fullscreen content.")
                            if (rewardEarned) {
                                openToiletDetail()
                            }

                            rewardedAd = null
                            loadAdMobRewardedAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            LogManager.e("Ad failed to show fullscreen content.")
                            rewardedAd = null
                            loadAdMobRewardedAd()
                        }

                        override fun onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            LogManager.e("Ad recorded an impression.")
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            LogManager.e("Ad showed fullscreen content.")
                        }
                    }
                }
            })
    }

    // 리워드 표시
    private fun showAdMobRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd?.show(this) { rewardItem ->
                // Handle the reward.
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                LogManager.e("User earned the reward.")

                // 광고 제거 적용 (24시간 유지)
                rewardEarned = true
                SharedManager.rewardEarnedTime = System.currentTimeMillis()
            }
        } else {
            Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.home_text_32), Toast.LENGTH_SHORT).show()
            loadAdMobRewardedAd()
        }
    }

    // 보상형 전면광고 준비
    private fun loadAdMobRewardedInterstitialAd() {
        RewardedInterstitialAd.load(
            this, MyUtil.getString(R.string.rewarded_interstitial_ad_unit_id),
            AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            // Called when a click is recorded for an ad.
                            LogManager.e("Ad was clicked.")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            LogManager.e("Ad dismissed fullscreen content.")
                            openToiletDetail()
                            rewardedInterstitialAd = null
                            loadAdMobRewardedInterstitialAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when ad fails to show.
                            LogManager.e("Ad failed to show fullscreen content.")
                            rewardedInterstitialAd = null
                            loadAdMobRewardedInterstitialAd()
                        }

                        override fun onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            LogManager.e("Ad recorded an impression.")
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            LogManager.e("Ad showed fullscreen content.")
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    LogManager.e(adError.toString())
                    rewardedInterstitialAd = null
                }
            })
    }

    // 보상형 전면광고 표시
    private fun showAdMobRewardedInterstitialAd() {
        if (rewardedInterstitialAd != null) {
            rewardedInterstitialAd?.show(this) { rewardItem ->

            }
        }
    }

    private fun openToiletDetail() {
        ObserverManager.root!!.startActivity(
            Intent(ObserverManager.context!!, ToiletActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                .putExtra(ToiletActivity.TOILET, mToilet)
        )
    }

    fun openCoupangAd() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://link.coupang.com/a/cspD9C"))
        startActivity(intent)

        // 광고 제거 적용 (24시간 유지)
        SharedManager.rewardEarnedTime = System.currentTimeMillis()
    }

    fun isAdRemoved(): Boolean {
        // 쿠팡 광고 클릭 후 24시간이 지나지 않았으면 광고 제거
        return System.currentTimeMillis() < SharedManager.rewardEarnedTime + 24 * 60 * 60 * 1000
    }

    private fun checkPopup() {
        if (SharedManager.noticeImage.isNotEmpty()) {
            val dialog = PopupDialog()
            dialog.show(supportFragmentManager, "PopupDialog")
        }
    }

    private fun setMyPosition(visibility: Int) {
        LogManager.e("visibility : $visibility")
        binding.lottieMyPosition.visibility = visibility
        if (visibility == View.VISIBLE) {
            binding.lottieMyPosition.playAnimation()
        } else {
            binding.lottieMyPosition.cancelAnimation()
        }
    }

    private fun setKakaoLocal(kaKaoKeyword: KaKaoKeyword) {
        isMyPositionMove = false
        binding.edtSearch.setText(kaKaoKeyword.place_name)
        binding.edtSearch.setSelection(kaKaoKeyword.place_name.count())
        kakaoMap?.moveCamera(
            CameraUpdateFactory.newCenterPosition(
                LatLng.from(
                    kaKaoKeyword.latitude,
                    kaKaoKeyword.longitude
                )
            ), CameraAnimation.from(500, true, true)
        )
        MyUtil.keyboardHide(binding.edtSearch)
        binding.rvSearch.visibility = View.GONE
        setMyPosition(View.GONE)
    }

    /**
     * 카카오지도 아이템 추가
     */
    fun addPOIItem(toilet: Toilet) {
        kakaoMap?.let {
            var imageResourceId = R.drawable.ic_position
            if (toilet.toilet_id < 0) {
                imageResourceId = R.drawable.ic_position_up
            }
            it.labelManager!!.layer!!.addLabel(
                LabelOptions.from(
                    "${toilet.toilet_id}",
                    LatLng.from(toilet.latitude, toilet.longitude)
                ).setStyles(LabelStyle.from(imageResourceId).setApplyDpScale(false))
            )
        }
    }

    /**
     * 카카오지도 나의위치 추가
     */
    fun addMyPosition(latitude: Double, longitude: Double) {
        kakaoMap?.let {
            if (my_position != null) {
                it.labelManager!!.layer!!.remove(my_position!!)
            }

            my_position = it.labelManager!!.layer!!.addLabel(
                LabelOptions.from(
                    "0",
                    LatLng.from(latitude, longitude)
                ).setTransform(TransformMethod.AbsoluteRotation).setStyles(
                    LabelStyle.from(R.drawable.ic_marker).setApplyDpScale(false)
                        .setAnchorPoint(0.5f, 0.5f)
                )
            )
            //my_position!!.rotateTo(my_position_rotation)
        }
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

        RetrofitJSONObject(
            request,
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
                            addPOIItem(toilet)
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

        RetrofitJSONObject(
            request,
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
            val binding =
                ItemKakaoKeywordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
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

        inner class ViewHolder(private val binding: ItemKakaoKeywordBinding) :
            RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun update(position: Int) {
                binding.tvTitle.text = keywordList[position].place_name
                binding.tvSub.text = keywordList[position].address_name

                itemView.setOnClickListener {
                    setKakaoLocal(keywordList[position])
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        // 현재위치기준으로 중심점변경
        if (isMyPositionMove && SharedManager.latitude > 0) {
            kakaoMap?.moveCamera(
                CameraUpdateFactory.newCenterPosition(
                    LatLng.from(
                        SharedManager.latitude,
                        SharedManager.longitude
                    )
                )
            )
            addMyPosition(SharedManager.latitude, SharedManager.longitude)
            setMyPosition(View.VISIBLE)
        }
    }

    override fun onBack() {
        if (isShowLoading()) {
            return
        }

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        if (binding.layoutBottomBg.visibility == View.VISIBLE || binding.rvSearch.visibility == View.VISIBLE) {
            binding.layoutBottomBg.visibility = View.GONE
            binding.rvSearch.visibility = View.GONE
            return
        }
        val dialog = FinishDialog()
        dialog.show(supportFragmentManager, "FinishDialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == RESULT_COUPANG) {
            openToiletDetail()
        }
    }

}