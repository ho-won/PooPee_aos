package kr.co.ho1.poopee.home

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
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
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.database.ToiletSQLiteManager
import kr.co.ho1.poopee.home.model.KaKaoKeyword
import kr.co.ho1.poopee.home.model.Toilet
import kr.co.ho1.poopee.home.view.FinishDialog
import kr.co.ho1.poopee.home.view.PopupDialog
import kr.co.ho1.poopee.home.view.ToiletDialog
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.json.JSONException


@Suppress("DEPRECATION")
class HomeActivity : BaseActivity(), MapView.POIItemEventListener, MapView.MapViewEventListener {

    private var mKeywordAdapter: ListAdapter = ListAdapter()
    private var mKeywordList: ArrayList<KaKaoKeyword> = ArrayList()

    private var mRootViewHeight = 0 // 키보드 제외 높이
    private var mIsKeyboardShow = false // 키보드 노출 상태
    private val mRvHeight = MyUtil.dpToPx(240)

    private var mIsMyPositionMove = true // 내위치기준으로 맵중심이동여부
    private var mIsFirstOnCreate = true // onCreate 체크 (내위치기준으로 맵중심을 이동할지 확인하기위해)
    private var mLastLatitude: Double = 0.0 // 마지막 중심 latitude
    private var mLastLongitude: Double = 0.0 // 마지막 중심 longitude

    private lateinit var mInterstitialAd: InterstitialAd

    private var mToilet: Toilet = Toilet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        MobileAds.initialize(this, MyUtil.getString(R.string.admob_app_id))
        ad_view.loadAd(AdRequest.Builder().build())

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = MyUtil.getString(R.string.interstitial_ad_unit_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        init()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        LocationManager.setLocationListener() // 현재위치 리스너 추가
        refresh()
    }

    override fun onPause() {
        super.onPause()
        LocationManager.removeLocationUpdate()
        map_view.removeAllViews()
    }

    private fun init() {
        rv_search.layoutManager = LinearLayoutManager(this)
        mKeywordAdapter = ListAdapter()
        rv_search.adapter = mKeywordAdapter

        checkPopup()
    }

    private fun refresh() {
        nav_view.refresh()
        ObserverManager.mapView = MapView(this)
        map_view.addView(ObserverManager.mapView)

        // 현재위치기준으로 중심점변경
        if (mIsFirstOnCreate) {
            mIsFirstOnCreate = false
            if (SharedManager.getLatitude() > 0) {
                ObserverManager.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(SharedManager.getLatitude(), SharedManager.getLongitude()), false)
                ObserverManager.addMyPosition(SharedManager.getLatitude(), SharedManager.getLongitude())
                setMyPosition(View.VISIBLE)
            }
        } else {
            if (mLastLatitude == 0.0) {
                mLastLatitude = ObserverManager.mapView.mapCenterPoint.mapPointGeoCoord.latitude
                mLastLongitude = ObserverManager.mapView.mapCenterPoint.mapPointGeoCoord.longitude
            }
            ObserverManager.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mLastLatitude, mLastLongitude), false)
        }

        ObserverManager.mapView.setZoomLevel(2, true)
        ObserverManager.mapView.setPOIItemEventListener(this)
        ObserverManager.mapView.setMapViewEventListener(this)
    }

    private fun setListener() {
        root_view.viewTreeObserver.addOnGlobalLayoutListener {
            if (mRootViewHeight < root_view.height) {
                mRootViewHeight = root_view.height
            }
            if (mRootViewHeight > root_view.height) {
                // keyboard show
                mIsKeyboardShow = true
                layout_bottom_bg.visibility = View.VISIBLE
            } else {
                // keyboard hide
                mIsKeyboardShow = false
                if (rv_search.visibility == View.GONE) {
                    layout_bottom_bg.visibility = View.GONE
                } else {
                    if (rv_search.height > mRvHeight) {
                        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mRvHeight)
                        rv_search.layoutParams = layoutParams
                    }
                }
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
        edt_search.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && mKeywordList.size > 0) {
                setKakaoLocal(mKeywordList[0])
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
                mIsMyPositionMove = true
                ObserverManager.addMyPosition(SharedManager.getLatitude(), SharedManager.getLongitude())
                ObserverManager.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(SharedManager.getLatitude(), SharedManager.getLongitude()), false)
                ObserverManager.mapView.setZoomLevel(2, false)
                setMyPosition(View.VISIBLE)
            }
        }
        btn_menu.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {

            }

            override fun onAdFailedToLoad(errorCode: Int) {

            }

            override fun onAdClosed() {
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, ToiletActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                        .putExtra(ToiletActivity.TOILET, mToilet)
                )
            }
        }
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        mIsMyPositionMove = false
    }

    /**
     * 카카오지도 이동완료 콜백
     */
    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        mLastLatitude = p0!!.mapCenterPoint.mapPointGeoCoord.latitude
        mLastLongitude = p0.mapCenterPoint.mapPointGeoCoord.longitude

        ObserverManager.mapView.removeAllPOIItems()
        val toiletList = ToiletSQLiteManager.getInstance().getToiletList(mLastLatitude, mLastLongitude)
        for (toilet in toiletList) {
            ObserverManager.addPOIItem(toilet)
        }
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

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {
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
                        if (mInterstitialAd.isLoaded) {
                            mInterstitialAd.show()
                        } else {
                            ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, ToiletActivity::class.java)
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
        mIsMyPositionMove = false
        edt_search.setText(kaKaoKeyword.place_name)
        edt_search.setSelection(kaKaoKeyword.place_name.count())
        ObserverManager.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(kaKaoKeyword.latitude, kaKaoKeyword.longitude), true)
        MyUtil.keyboardHide(edt_search)
        rv_search.visibility = View.GONE
        setMyPosition(View.GONE)
    }

    /**
     * [GET] 카카오지도 키워드 검색
     */
    private fun taskKakaoLocalSearch(query: String) {
        val params = RetrofitParams()
        params.put("query", query) // 검색을 원하는 질의어

        val request = RetrofitClient.getClientKaKao(RetrofitService.KAKAO_LOCAL).create(RetrofitService::class.java).kakaoLocalSearch(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        mKeywordList = ArrayList()
                        val jsonArray = it.getJSONArray("documents")

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val keyword = KaKaoKeyword()
                            keyword.address_name = jsonObject.getString("address_name")
                            keyword.place_name = jsonObject.getString("place_name")
                            keyword.latitude = jsonObject.getDouble("y")
                            keyword.longitude = jsonObject.getDouble("x")

                            mKeywordList.add(keyword)
                        }

                        mKeywordAdapter.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                onFailed = {

                }
        )
    }

    /**
     * 공지사항 목록 adapter
     */
    inner class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_kakao_keyword, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ViewHolder).update(position)
        }

        override fun getItemCount(): Int {
            return mKeywordList.size
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            @SuppressLint("SetTextI18n")
            fun update(position: Int) {
                itemView.tv_title.text = mKeywordList[position].place_name
                itemView.tv_sub.text = mKeywordList[position].address_name

                itemView.setOnClickListener {
                    setKakaoLocal(mKeywordList[position])
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        // 현재위치기준으로 중심점변경
        if (mIsMyPositionMove && SharedManager.getLatitude() > 0) {
            ObserverManager.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(SharedManager.getLatitude(), SharedManager.getLongitude()), false)
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


        val dialog = FinishDialog()
        dialog.show(supportFragmentManager, "FinishDialog")
    }

}