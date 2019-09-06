package kr.ho1.poopee.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_kakao_keyword.view.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.data.SharedManager
import kr.ho1.poopee.common.http.RetrofitClient
import kr.ho1.poopee.common.http.RetrofitJSONObject
import kr.ho1.poopee.common.http.RetrofitParams
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.common.util.LocationManager
import kr.ho1.poopee.common.util.MySpannableString
import kr.ho1.poopee.common.util.MyUtil
import kr.ho1.poopee.database.ToiletSQLiteManager
import kr.ho1.poopee.home.model.KaKaoKeyword
import kr.ho1.poopee.home.model.Toilet
import kr.ho1.poopee.home.view.PopupDialog
import kr.ho1.poopee.home.view.ToiletDialog
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.json.JSONException

@Suppress("DEPRECATION")
class HomeActivity : BaseActivity(), MapView.POIItemEventListener, MapView.MapViewEventListener {

    private lateinit var mMapView: MapView

    private var mKeywordAdapter: ListAdapter = ListAdapter()
    private var mKeywordList: ArrayList<KaKaoKeyword> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setToolbar()

        init()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        LocationManager.setLocationListener() // 현재위치 리스너 추가
        nav_view.refresh()
    }

    override fun onPause() {
        super.onPause()
        LocationManager.removeLocationUpdate()
    }

    private fun init() {
        rv_search.layoutManager = LinearLayoutManager(this)
        mKeywordAdapter = ListAdapter()
        rv_search.adapter = mKeywordAdapter

        mMapView = MapView(this)
        map_view.addView(mMapView)

        // 현재위치기준으로 중심점변경
        if (SharedManager.getLatitude() > 0) {
            mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(SharedManager.getLatitude(), SharedManager.getLongitude()), true)
        }

        // mMapView.setZoomLevel(7, true); // 줌 레벨 변경
        // mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true); // 중심점 변경 + 줌 레벨 변경
        // mMapView.zoomIn(true); // 줌 인
        // mMapView.zoomOut(true); // 줌 아웃

        mMapView.setPOIItemEventListener(this)
        mMapView.setMapViewEventListener(this)

        checkPopup()
    }

    private fun setListener() {
        root_view.setOnClickListener {
            MyUtil.keyboardHide(edt_search)
        }
        btn_search_delete.setOnClickListener {
            edt_search.setText("")
            MyUtil.keyboardHide(edt_search)
            rv_search.visibility = View.GONE
        }
        edt_search.setOnTouchListener { _, _ ->
            edt_search.requestFocus()
            MyUtil.keyboardShow(edt_search)
            rv_search.visibility = View.VISIBLE
            true
        }
        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                taskKakaoLocalSearch(p0.toString())
            }
        })
        btn_current_location.setOnClickListener {
            if (SharedManager.getLatitude() > 0) {
                mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(SharedManager.getLatitude(), SharedManager.getLongitude()), true)
            }
        }
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    /**
     * 카카오지도 이동완료 콜백
     */
    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        val latitude = p0!!.mapCenterPoint.mapPointGeoCoord.latitude
        val longitude = p0.mapCenterPoint.mapPointGeoCoord.longitude
        Log.e("MapView_MoveFinished", "latitude: $latitude longitude: $longitude")

        mMapView.removeAllPOIItems()
        val toiletList = ToiletSQLiteManager.getInstance().getToiletList(latitude, longitude)
        for (toilet in toiletList) {
            addPOIItem(toilet, toilet.latitude, toilet.longitude)
        }
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

    /**
     * 카카오지도 아이템 클릭
     */
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        val toilet = Toilet()
        toilet.toilet_id = p1!!.tag
        toilet.name = p1.itemName

        val dialog = ToiletDialog()
        dialog.setToilet(toilet)
        dialog.show(supportFragmentManager, "ToiletDialog")
    }

    /**
     * 카카오지도 아이템 추가
     */
    private fun addPOIItem(toilet: Toilet, latitude: Double, longitude: Double) {
        val marker = MapPOIItem()
        marker.itemName = toilet.name
        marker.tag = toilet.toilet_id
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
        marker.markerType = MapPOIItem.MarkerType.CustomImage // 마커타입을 커스텀 마커로 지정.
        marker.customImageResourceId = R.drawable.ic_marker // 마커 이미지.
        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.customSelectedImageResourceId = R.drawable.ic_marker // 마커 이미지.
        marker.isShowCalloutBalloonOnTouch = false
        mMapView.addPOIItem(marker)
    }

    private fun checkPopup() {
        if (SharedManager.getNoticeImage().count() > 0) {
            val dialog = PopupDialog()
            dialog.show(supportFragmentManager, "PopupDialog")
        }
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
                val sizeString: Array<String> = arrayOf(" (" + mKeywordList[position].address_name + ")")
                val colorString: Array<String> = arrayOf(" (" + mKeywordList[position].address_name + ")")

                val span = MySpannableString(mKeywordList[position].place_name + " (" + mKeywordList[position].address_name + ")")
                span.setSize(sizeString, 12)
                span.setColor(colorString, "#999999")
                itemView.tv_title.text = span.getSpannableString()

                itemView.layout_title.setOnClickListener {
                    mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mKeywordList[position].latitude, mKeywordList[position].longitude), true)
                    MyUtil.keyboardHide(edt_search)
                    rv_search.visibility = View.GONE
                }
            }
        }
    }

    override fun setToolbar() {
        toolbar.setImageLeftOne(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_bar_menu))
        toolbar.setSelectedListener(
                onBtnLeftOne = {
                    drawer_layout.openDrawer(GravityCompat.START)
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

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        }

        if (rv_search.visibility == View.VISIBLE) {
            rv_search.visibility = View.GONE
            return
        }

        finish()
    }

}