package kr.co.ho1.poopee.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import kotlinx.android.synthetic.main.activity_toilet.*
import kotlinx.android.synthetic.main.item_toilet_comment.view.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.dialog.BasicDialog
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.LogManager
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.common.util.StrManager
import kr.co.ho1.poopee.home.model.Comment
import kr.co.ho1.poopee.home.model.Toilet
import kr.co.ho1.poopee.home.view.*
import kr.co.ho1.poopee.login.LoginActivity
import org.json.JSONException

@Suppress("DEPRECATION")
class ToiletActivity : BaseActivity() {

    companion object {
        const val TOILET = "toilet"
        const val REVIEW_COUNT = 3
    }

    private var toilet: Toilet = Toilet()
    private var addressText: String = ""

    private var recyclerAdapter: ListAdapter = ListAdapter()
    private var commentList: ArrayList<Comment> = ArrayList()

    var kakaoMap: KakaoMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toilet)
        setToolbar()

        init()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onPause() {
        super.onPause()
        map_view.removeAllViews()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        toilet = intent.getSerializableExtra(TOILET) as Toilet

        val layoutManager = LinearLayoutManager(this)
        layoutManager.isAutoMeasureEnabled = true
        recycler_view.layoutManager = layoutManager
        recycler_view.isNestedScrollingEnabled = false
        recyclerAdapter = ListAdapter()
        recycler_view.adapter = recyclerAdapter

        if (SharedManager.getReviewCount() < REVIEW_COUNT) {
            SharedManager.setReviewCount(SharedManager.getReviewCount() + 1)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun refresh() {

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

                LogManager.e("HO_TEST", toilet.latitude.toString())
                kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(toilet.latitude, toilet.longitude)))
                addPOIItem(toilet)
                kakaoMap?.moveCamera(CameraUpdateFactory.zoomTo(15))
            }
        })

        map_view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    scroll_view.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP -> {
                    scroll_view.requestDisallowInterceptTouchEvent(false)
                }
                else -> {
                }
            }
            super.onTouchEvent(event)
        }

        commentList = arrayListOf()

        tv_toilet_name.text = toilet.name
        tv_toilet_content.text = toilet.content

        if (toilet.content.isNotEmpty()) {
            tv_toilet_content.visibility = View.VISIBLE
        } else {
            tv_toilet_content.visibility = View.GONE
        }

        if (toilet.member_id == SharedManager.getMemberId()) {
            layout_btn_mine.visibility = View.VISIBLE
            layout_btn_normal.visibility = View.GONE
        } else {
            layout_btn_mine.visibility = View.GONE
            layout_btn_normal.visibility = View.VISIBLE
        }
        cb_like.isChecked = toilet.like_check

        if (toilet.type == "유저") {
            // 유저가 등록한 화장실
            cb_tap_address.visibility = View.GONE
            layout_detail_address.visibility = View.GONE
            layout_detail_manager_title.visibility = View.GONE
            layout_detail_manager.visibility = View.GONE

            cb_option_04.visibility = View.GONE
            cb_option_05.visibility = View.GONE
            cb_option_06.visibility = View.GONE
        } else {
            // 공공데이터포털 화장실
            cb_tap_address.visibility = View.VISIBLE
            layout_detail_address.visibility = View.VISIBLE
            layout_detail_manager_title.visibility = View.VISIBLE
            layout_detail_manager.visibility = View.GONE

            cb_option_04.visibility = View.VISIBLE
            cb_option_05.visibility = View.VISIBLE
            cb_option_06.visibility = View.VISIBLE
        }

        addressText = if (toilet.address_new.count() > 0) {
            toilet.address_new
        } else {
            toilet.address_old
        }
        StrManager.setAddressCopySpan(tv_address, addressText)

        // 남녀공용
        cb_option_01.isChecked = toilet.unisex == "Y"

        // 남자화장실
        try {
            val option02Count = toilet.m_poo.toInt() + toilet.m_pee.toInt()
            cb_option_02.isChecked = option02Count > 0
        } catch (e: Exception) {
            cb_option_02.isChecked = false
        }

        // 여자화장실
        try {
            val option03Count = toilet.w_poo.toInt()
            cb_option_03.isChecked = option03Count > 0
        } catch (e: Exception) {
            cb_option_03.isChecked = false
        }

        // 장애인화장실
        try {
            val option04Count = toilet.m_d_poo.toInt() + toilet.m_d_pee.toInt() + toilet.w_d_poo.toInt()
            cb_option_04.isChecked = option04Count > 0
        } catch (e: Exception) {
            cb_option_04.isChecked = false
        }

        // 남자어린이화장실
        try {
            val option05Count = toilet.m_c_poo.toInt() + toilet.m_c_pee.toInt()
            cb_option_05.isChecked = option05Count > 0
        } catch (e: Exception) {
            cb_option_05.isChecked = false
        }

        // 여자어린이화장실
        try {
            val option06Count = toilet.w_c_poo.toInt()
            cb_option_06.isChecked = option06Count > 0
        } catch (e: Exception) {
            cb_option_06.isChecked = false
        }

        tv_m_poo.text = toilet.m_poo
        tv_m_pee.text = toilet.m_pee
        tv_m_d_poo.text = toilet.m_d_poo
        tv_m_d_pee.text = toilet.m_d_pee
        tv_m_c_poo.text = toilet.m_c_poo
        tv_m_c_pee.text = toilet.m_c_pee

        tv_w_poo.text = toilet.w_poo
        tv_w_d_poo.text = toilet.w_d_poo
        tv_w_c_poo.text = toilet.w_c_poo

        tv_manager_name.text = toilet.manager_name
        tv_manager_tel.text = toilet.manager_tel
        tv_open_time.text = toilet.open_time

        tv_comment_count.text = toilet.comment_count

        taskCommentList()
    }

    private fun setListener() {
        btn_back.setOnClickListener {
            finish()
        }
        layout_report.setOnClickListener {
            if (SharedManager.isLoginCheck()) {
                val dialog = ToiletReportDialog()
                dialog.setToilet(toilet)
                dialog.show(supportFragmentManager, "ToiletReportDialog")
            } else {
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        layout_delete.setOnClickListener {
            val dialog = BasicDialog(
                    onLeftButton = {
                        taskToiletDelete()
                    },
                    onCenterButton = {

                    },
                    onRightButton = {

                    }
            )
            dialog.setTextContent(MyUtil.getString(R.string.home_text_06))
            dialog.setBtnLeft(MyUtil.getString(R.string.confirm))
            dialog.setBtnRight(MyUtil.getString(R.string.cancel))
            dialog.show(supportFragmentManager, "BasicDialog")
        }
        layout_update.setOnClickListener {
            val dialog = ToiletUpdateDialog(
                    toilet,
                    onUpdate = {
                        toilet = it
                        refresh()
                    }
            )
            dialog.show(supportFragmentManager, "ToiletCreateDialog")
        }
        layout_sms.setOnClickListener {
            val dialog = ShareDialog()
            dialog.setAction(ShareDialog.ACTION_SHARE)
            dialog.setToilet(toilet)
            dialog.show(ObserverManager.root!!.supportFragmentManager, "ShareDialog")
        }
        map_view_click.setOnClickListener {
            finish()
        }
        layout_like.setOnClickListener {
            cb_like.isChecked = !cb_like.isChecked
            if (SharedManager.isLoginCheck()) {
                taskToiletLike()
            } else {
                cb_like.isChecked = false
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        cb_tap_address.setOnClickListener {
            if (cb_tap_address.isChecked) {
                layout_detail_address.visibility = View.VISIBLE
            } else {
                layout_detail_address.visibility = View.GONE
            }
        }
        cb_tap_manager.setOnClickListener {
            if (cb_tap_manager.isChecked) {
                layout_detail_manager.visibility = View.VISIBLE
            } else {
                layout_detail_manager.visibility = View.GONE
            }
        }
        tv_manager_tel.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_DIAL)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setData(Uri.parse("tel:${toilet.manager_tel}"))
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        btn_comment.setOnClickListener {
            if (SharedManager.isLoginCheck()) {
                val dialog = CommentCreateDialog(
                        onCreate = {
                            taskCommentCreate(it)
                        }
                )
                dialog.show(supportFragmentManager, "ToiletDialog")
            } else {
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
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
            it.labelManager!!.layer!!.addLabel(LabelOptions.from("${toilet.toilet_id}", LatLng.from(toilet.latitude, toilet.longitude)).setStyles(LabelStyle.from(imageResourceId).setApplyDpScale(false)))
        }
    }

    /**
     * [GET] 댓글목록
     */
    private fun taskCommentList() {
        showLoading()
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("toilet_id", toilet.toilet_id)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).commentList(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            tv_like.text = it.getString("like_count")
                            cb_like.isChecked = it.getString("like_check") == "1"
                            tv_comment_count.text = it.getString("comment_count")

                            val jsonArray = it.getJSONArray("comments")
                            commentList = ArrayList()

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val comment = Comment()
                                comment.comment_id = jsonObject.getString("comment_id")
                                comment.member_id = jsonObject.getString("member_id")
                                comment.gender = jsonObject.getString("gender")
                                comment.name = jsonObject.getString("name")
                                comment.content = jsonObject.getString("content")
                                comment.created = jsonObject.getString("created")
                                comment.view_type = Toilet.VIEW_COMMENT

                                commentList.add(comment)
                            }

                            recyclerAdapter.notifyDataSetChanged()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    hideLoading()
                },
                onFailed = {
                    hideLoading()
                }
        )
    }

    /**
     * [POST] 좋아요
     */
    private fun taskToiletLike() {
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("toilet_id", toilet.toilet_id)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).toiletLike(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            tv_like.text = it.getString("like_count")
                            cb_like.isChecked = it.getString("like_check") == "1"
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
     * [POST] 댓글작성
     */
    private fun taskCommentCreate(comment: String) {
        showLoading()
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("toilet_id", toilet.toilet_id)
        params.put("content", comment)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).commentCreate(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            taskCommentList()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    hideLoading()
                },
                onFailed = {
                    hideLoading()
                }
        )
    }

    /**
     * [DELETE] 댓글삭제
     */
    private fun taskCommentDelete(comment: Comment) {
        showLoading()
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("comment_id", comment.comment_id)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).commentDelete(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            taskCommentList()
                            Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_delete_complete), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    hideLoading()
                },
                onFailed = {
                    hideLoading()
                }
        )
    }

    /**
     * [DELETE] 화장실삭제
     */
    private fun taskToiletDelete() {
        showLoading()
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("toilet_id", toilet.toilet_id)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).toiletDelete(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_delete_complete), Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    hideLoading()
                },
                onFailed = {
                    hideLoading()
                }
        )
    }

    /**
     * Toilet adapter
     */
    inner class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_toilet_comment, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as CommentViewHolder).update(position)
        }

        override fun getItemCount(): Int {
            return commentList.size
        }

        override fun getItemViewType(position: Int): Int {
            return commentList[position].view_type
        }

        inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun update(position: Int) {
                if (commentList[position].gender == "0") {
                    itemView.iv_gender.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_man_profile))
                } else {
                    itemView.iv_gender.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_woman_profile))
                }

                itemView.tv_name.text = commentList[position].name
                itemView.tv_date.text = StrManager.getDateTime(commentList[position].created)
                itemView.tv_comment.text = commentList[position].content

                itemView.btn_menu.setOnClickListener {
                    openMenu(itemView.btn_menu, position)
                }
            }

            private fun openMenu(v: View, position: Int) {
                val popupMenu = PopupMenu(v.context, v, Gravity.END)
                val inflater = popupMenu.menuInflater
                inflater.inflate(R.menu.menu_comment, popupMenu.menu)
                popupMenu.menu.findItem(R.id.item_delete).title = resources.getString(R.string.delete)
                popupMenu.menu.findItem(R.id.item_update).title = resources.getString(R.string.modified)
                popupMenu.menu.findItem(R.id.item_report).title = resources.getString(R.string.report)

                if (commentList[position].member_id == SharedManager.getMemberId()) {
                    popupMenu.menu.findItem(R.id.item_delete).isVisible = true
                    popupMenu.menu.findItem(R.id.item_update).isVisible = true
                    popupMenu.menu.findItem(R.id.item_report).isVisible = false
                } else {
                    popupMenu.menu.findItem(R.id.item_delete).isVisible = false
                    popupMenu.menu.findItem(R.id.item_update).isVisible = false
                    popupMenu.menu.findItem(R.id.item_report).isVisible = true
                }

                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.item_delete -> {
                            val dialog = BasicDialog(
                                    onLeftButton = {
                                        taskCommentDelete(commentList[position])
                                    },
                                    onCenterButton = {

                                    },
                                    onRightButton = {

                                    }
                            )
                            dialog.setTextContent(MyUtil.getString(R.string.home_text_06))
                            dialog.setBtnLeft(MyUtil.getString(R.string.confirm))
                            dialog.setBtnRight(MyUtil.getString(R.string.cancel))
                            dialog.show(supportFragmentManager, "BasicDialog")
                            return@setOnMenuItemClickListener true
                        }
                        R.id.item_update -> {
                            val dialog = CommentUpdateDialog(
                                    onUpdate = { comment ->
                                        commentList[position] = comment
                                        notifyItemChanged(position)
                                    }
                            )
                            dialog.setComment(commentList[position])
                            dialog.show(supportFragmentManager, "ToiletDialog")
                            return@setOnMenuItemClickListener true
                        }
                        R.id.item_report -> {
                            if (SharedManager.isLoginCheck()) {
                                val dialog = CommentReportDialog()
                                dialog.setComment(commentList[position])
                                dialog.show(supportFragmentManager, "CommentReportDialog")
                            } else {
                                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                                )
                            }
                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener true
                    }
                }
                popupMenu.show()
            }
        }
    }

    override fun finish() {
        map_view.removeAllViews()
        super.finish()
    }

}