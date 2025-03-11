package kr.co.ho1.poopee.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebViewClient
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
import kr.co.ho1.poopee.databinding.ActivityToiletBinding
import kr.co.ho1.poopee.databinding.ItemToiletCommentBinding
import kr.co.ho1.poopee.home.model.Comment
import kr.co.ho1.poopee.home.model.Toilet
import kr.co.ho1.poopee.home.view.*
import kr.co.ho1.poopee.login.LoginActivity
import org.json.JSONException

@Suppress("DEPRECATION")
class ToiletActivity : BaseActivity() {
    private lateinit var binding: ActivityToiletBinding

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
        binding = ActivityToiletBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        binding.mapView.removeAllViews()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        toilet = intent.getSerializableExtra(TOILET) as Toilet

        val layoutManager = LinearLayoutManager(this)
        layoutManager.isAutoMeasureEnabled = true
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.isNestedScrollingEnabled = false
        recyclerAdapter = ListAdapter()
        binding.recyclerView.adapter = recyclerAdapter

        if (SharedManager.getReviewCount() < REVIEW_COUNT) {
            SharedManager.setReviewCount(SharedManager.getReviewCount() + 1)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun refresh() {
        binding.mapView.start(object : MapLifeCycleCallback() {
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

        binding.mapView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.scrollView.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_UP -> {
                    binding.scrollView.requestDisallowInterceptTouchEvent(false)
                }

                else -> {
                }
            }
            super.onTouchEvent(event)
        }

        commentList = arrayListOf()

        binding.tvToiletName.text = toilet.name
        binding.tvToiletContent.text = toilet.content

        if (toilet.content.isNotEmpty()) {
            binding.tvToiletContent.visibility = View.VISIBLE
        } else {
            binding.tvToiletContent.visibility = View.GONE
        }

        if (toilet.member_id == SharedManager.getMemberId()) {
            binding.layoutBtnMine.visibility = View.VISIBLE
            binding.layoutBtnNormal.visibility = View.GONE
        } else {
            binding.layoutBtnMine.visibility = View.GONE
            binding.layoutBtnNormal.visibility = View.VISIBLE
        }
        binding.cbLike.isChecked = toilet.like_check

        if (toilet.type == "유저") {
            // 유저가 등록한 화장실
            binding.cbTapAddress.visibility = View.GONE
            binding.layoutDetailAddress.visibility = View.GONE
            binding.layoutDetailManagerTitle.visibility = View.GONE
            binding.layoutDetailManager.visibility = View.GONE

            binding.cbOption04.visibility = View.GONE
            binding.cbOption05.visibility = View.GONE
            binding.cbOption06.visibility = View.GONE
        } else {
            // 공공데이터포털 화장실
            binding.cbTapAddress.visibility = View.VISIBLE
            binding.layoutDetailAddress.visibility = View.VISIBLE
            binding.layoutDetailManagerTitle.visibility = View.VISIBLE
            binding.layoutDetailManager.visibility = View.GONE

            binding.cbOption04.visibility = View.VISIBLE
            binding.cbOption05.visibility = View.VISIBLE
            binding.cbOption06.visibility = View.VISIBLE
        }

        addressText = if (toilet.address_new.count() > 0) {
            toilet.address_new
        } else {
            toilet.address_old
        }
        StrManager.setAddressCopySpan(binding.tvAddress, addressText)

        // 남녀공용
        binding.cbOption01.isChecked = toilet.unisex == "Y"

        // 남자화장실
        try {
            val option02Count = toilet.m_poo.toInt() + toilet.m_pee.toInt()
            binding.cbOption02.isChecked = option02Count > 0
        } catch (e: Exception) {
            binding.cbOption02.isChecked = false
        }

        // 여자화장실
        try {
            val option03Count = toilet.w_poo.toInt()
            binding.cbOption03.isChecked = option03Count > 0
        } catch (e: Exception) {
            binding.cbOption03.isChecked = false
        }

        // 장애인화장실
        try {
            val option04Count = toilet.m_d_poo.toInt() + toilet.m_d_pee.toInt() + toilet.w_d_poo.toInt()
            binding.cbOption04.isChecked = option04Count > 0
        } catch (e: Exception) {
            binding.cbOption04.isChecked = false
        }

        // 남자어린이화장실
        try {
            val option05Count = toilet.m_c_poo.toInt() + toilet.m_c_pee.toInt()
            binding.cbOption05.isChecked = option05Count > 0
        } catch (e: Exception) {
            binding.cbOption05.isChecked = false
        }

        // 여자어린이화장실
        try {
            val option06Count = toilet.w_c_poo.toInt()
            binding.cbOption06.isChecked = option06Count > 0
        } catch (e: Exception) {
            binding.cbOption06.isChecked = false
        }

        binding.tvMPoo.text = toilet.m_poo
        binding.tvMPee.text = toilet.m_pee
        binding.tvMDPoo.text = toilet.m_d_poo
        binding.tvMDPee.text = toilet.m_d_pee
        binding.tvMCPoo.text = toilet.m_c_poo
        binding.tvMCPee.text = toilet.m_c_pee

        binding.tvWPoo.text = toilet.w_poo
        binding.tvWDPoo.text = toilet.w_d_poo
        binding.tvWCPoo.text = toilet.w_c_poo

        binding.tvManagerName.text = toilet.manager_name
        binding.tvManagerTel.text = toilet.manager_tel
        binding.tvOpenTime.text = toilet.open_time

        binding.tvCommentCount.text = toilet.comment_count

        taskCommentList()
        setCoupangAd()
    }

    private fun setListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.layoutReport.setOnClickListener {
            if (SharedManager.isLoginCheck()) {
                val dialog = ToiletReportDialog()
                dialog.setToilet(toilet)
                dialog.show(supportFragmentManager, "ToiletReportDialog")
            } else {
                ObserverManager.root!!.startActivity(
                    Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        binding.layoutDelete.setOnClickListener {
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
        binding.layoutUpdate.setOnClickListener {
            val dialog = ToiletUpdateDialog(
                toilet,
                onUpdate = {
                    toilet = it
                    refresh()
                }
            )
            dialog.show(supportFragmentManager, "ToiletCreateDialog")
        }
        binding.layoutSms.setOnClickListener {
            val dialog = ShareDialog()
            dialog.setAction(ShareDialog.ACTION_SHARE)
            dialog.setToilet(toilet)
            dialog.show(ObserverManager.root!!.supportFragmentManager, "ShareDialog")
        }
        binding.mapViewClick.setOnClickListener {
            finish()
        }
        binding.layoutLike.setOnClickListener {
            binding.cbLike.isChecked = !binding.cbLike.isChecked
            if (SharedManager.isLoginCheck()) {
                taskToiletLike()
            } else {
                binding.cbLike.isChecked = false
                ObserverManager.root!!.startActivity(
                    Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        binding.cbTapAddress.setOnClickListener {
            if (binding.cbTapAddress.isChecked) {
                binding.layoutDetailAddress.visibility = View.VISIBLE
            } else {
                binding.layoutDetailAddress.visibility = View.GONE
            }
        }
        binding.cbTapManager.setOnClickListener {
            if (binding.cbTapManager.isChecked) {
                binding.layoutDetailManager.visibility = View.VISIBLE
            } else {
                binding.layoutDetailManager.visibility = View.GONE
            }
        }
        binding.tvManagerTel.setOnClickListener {
            try {
                startActivity(
                    Intent(Intent.ACTION_DIAL)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setData(Uri.parse("tel:${toilet.manager_tel}"))
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.btnComment.setOnClickListener {
            if (SharedManager.isLoginCheck()) {
                val dialog = CommentCreateDialog(
                    onCreate = {
                        taskCommentCreate(it)
                    }
                )
                dialog.show(supportFragmentManager, "ToiletDialog")
            } else {
                ObserverManager.root!!.startActivity(
                    Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setCoupangAd() {
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.domStorageEnabled = true

        binding.webView.webViewClient = WebViewClient()

        val htmlData = """
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <script src="https://ads-partners.coupang.com/g.js"></script>
            </head>
            <body style="margin:0;padding:0;display:flex;justify-content:center;align-items:center;">
                <script>
                    new PartnersCoupang.G({
                        "id": 846359,
                        "template": "carousel",
                        "trackingCode": "AF3689916",
                        "width": "360",
                        "height": "60",
                        "tsource": ""
                    });
                </script>
            </body>
            </html>
        """.trimIndent()


        binding.webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
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
                        binding.tvLike.text = it.getString("like_count")
                        binding.cbLike.isChecked = it.getString("like_check") == "1"
                        binding.tvCommentCount.text = it.getString("comment_count")

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
                        binding.tvLike.text = it.getString("like_count")
                        binding.cbLike.isChecked = it.getString("like_check") == "1"
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
            val binding = ItemToiletCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CommentViewHolder(binding)
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

        inner class CommentViewHolder(private val binding: ItemToiletCommentBinding) : RecyclerView.ViewHolder(binding.root) {

            fun update(position: Int) {
                if (commentList[position].gender == "0") {
                    binding.ivGender.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_man_profile))
                } else {
                    binding.ivGender.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_woman_profile))
                }

                binding.tvName.text = commentList[position].name
                binding.tvDate.text = StrManager.getDateTime(commentList[position].created)
                binding.tvComment.text = commentList[position].content

                binding.btnMenu.setOnClickListener {
                    openMenu(binding.btnMenu, position)
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
                                ObserverManager.root!!.startActivity(
                                    Intent(ObserverManager.context!!, LoginActivity::class.java)
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
        binding.mapView.removeAllViews()
        super.finish()
    }

}