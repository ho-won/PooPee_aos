package kr.ho1.poopee.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_toilet.*
import kotlinx.android.synthetic.main.item_toilet_comment.view.*
import kotlinx.android.synthetic.main.view_toolbar.view.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.data.SharedManager
import kr.ho1.poopee.common.dialog.BasicDialog
import kr.ho1.poopee.common.http.RetrofitClient
import kr.ho1.poopee.common.http.RetrofitJSONObject
import kr.ho1.poopee.common.http.RetrofitParams
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.common.util.LogManager
import kr.ho1.poopee.common.util.MyUtil
import kr.ho1.poopee.home.model.Comment
import kr.ho1.poopee.home.model.Toilet
import kr.ho1.poopee.home.view.CommentReportDialog
import kr.ho1.poopee.home.view.CommentUpdateDialog
import kr.ho1.poopee.login.LoginActivity
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.json.JSONException

@Suppress("DEPRECATION")
class ToiletActivity : BaseActivity() {

    companion object {
        const val TOILET = "toilet"
    }

    private var mToilet: Toilet = Toilet()

    private var mRecyclerAdapter: ListAdapter = ListAdapter()
    private var mCommentList: ArrayList<Comment> = ArrayList()

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

    private fun init() {
        mToilet = intent.getSerializableExtra(TOILET) as Toilet

        toolbar.setTitle(mToilet.name)

        tv_m_poo.text = mToilet.m_poo
        tv_m_pee.text = mToilet.m_pee
        tv_m_d_poo.text = mToilet.m_d_poo
        tv_m_d_pee.text = mToilet.m_d_pee
        tv_m_c_poo.text = mToilet.m_c_poo
        tv_m_c_pee.text = mToilet.m_c_pee

        tv_w_poo.text = mToilet.w_poo
        tv_w_d_poo.text = mToilet.w_d_poo
        tv_w_c_poo.text = mToilet.w_c_poo

//        val content = String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_unisex), mToilet.unisex) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_m_poo), mToilet.) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_m_pee), mToilet.) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_m_d_poo), mToilet.) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_m_d_pee), mToilet.) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_m_c_poo), mToilet.) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_m_c_pee), mToilet.) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_w_poo), mToilet.) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_w_d_poo), mToilet.) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_w_c_poo), mToilet.) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_manager_name), mToilet.manager_name) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_manager_tel), mToilet.manager_tel) +
//                "\n" + String.format(ObserverManager.context!!.resources.getString(R.string.toilet_db_open_time), mToilet.open_time)

        tv_comment_count.text = mToilet.comment_count
        btn_like.isChecked = mToilet.like_check

        val layoutManager = LinearLayoutManager(this)
        layoutManager.isAutoMeasureEnabled = true
        recycler_view.layoutManager = layoutManager
        recycler_view.isNestedScrollingEnabled = false
        mRecyclerAdapter = ListAdapter()
        recycler_view.adapter = mRecyclerAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun refresh() {
        ObserverManager.mapView = MapView(this)
        map_view.addView(ObserverManager.mapView)
        LogManager.e("HO_TEST", mToilet.latitude.toString())
        ObserverManager.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mToilet.latitude, mToilet.longitude), true)
        ObserverManager.addPOIItem(mToilet)
        ObserverManager.mapView.setZoomLevelFloat(2.2f, true)
        ObserverManager.mapView.setOnTouchListener { _, event ->
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

        taskCommentList()
    }

    private fun setListener() {
        btn_like.setOnClickListener {
            if (SharedManager.isLoginCheck()) {
                taskToiletLike()
            } else {
                btn_like.isChecked = false
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        btn_send.setOnClickListener {
            if (edt_content.text.isNotEmpty()) {
                if (SharedManager.isLoginCheck()) {
                    taskCommentCreate()
                } else {
                    ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    )
                }
            }
        }
    }

    /**
     * [GET] 댓글목록
     */
    private fun taskCommentList() {
        showLoading()
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("toilet_id", mToilet.toilet_id)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).commentList(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            toolbar.tv_like.text = it.getString("like_count")
                            btn_like.isChecked = it.getString("like_check") == "1"
                            tv_comment_count.text = it.getString("comment_count")

                            val jsonArray = it.getJSONArray("comments")
                            mCommentList = ArrayList()

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val comment = Comment()
                                comment.comment_id = jsonObject.getString("comment_id")
                                comment.member_id = jsonObject.getString("member_id")
                                comment.name = jsonObject.getString("name")
                                comment.content = jsonObject.getString("content")
                                comment.created = jsonObject.getString("created")
                                comment.view_type = Toilet.VIEW_COMMENT

                                mCommentList.add(comment)
                            }

                            mRecyclerAdapter.notifyDataSetChanged()
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
        params.put("toilet_id", mToilet.toilet_id)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).toiletLike(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            toolbar.tv_like.text = it.getString("like_count")
                            btn_like.isChecked = it.getString("like_check") == "1"
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
    private fun taskCommentCreate() {
        showLoading()
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("toilet_id", mToilet.toilet_id)
        params.put("content", edt_content.text)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).commentCreate(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            edt_content.setText("")
                            MyUtil.keyboardHide(edt_content)
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
    private fun taskCommentDelete(comment: Comment, position: Int) {
        showLoading()
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("comment_id", comment.comment_id)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).commentDelete(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            mCommentList.removeAt(position)
                            mRecyclerAdapter.notifyItemRemoved(position)
                            Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_delete_complete), Toast.LENGTH_SHORT).show()
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
            return mCommentList.size
        }

        override fun getItemViewType(position: Int): Int {
            return mCommentList[position].view_type
        }

        inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun update(position: Int) {
                itemView.tv_name.text = mCommentList[position].name
                itemView.tv_date.text = mCommentList[position].created
                itemView.tv_comment.text = mCommentList[position].content

                if (mCommentList[position].member_id == SharedManager.getMemberId()) {
                    itemView.tv_update.visibility = View.VISIBLE
                    itemView.tv_delete.visibility = View.VISIBLE
                    itemView.tv_report.visibility = View.GONE
                } else {
                    itemView.tv_update.visibility = View.GONE
                    itemView.tv_delete.visibility = View.GONE
                    itemView.tv_report.visibility = View.VISIBLE
                }

                itemView.tv_update.setOnClickListener {
                    val dialog = CommentUpdateDialog(
                            onUpdate = {
                                mCommentList[position] = it
                                notifyItemChanged(position)
                            }
                    )
                    dialog.setComment(mCommentList[position])
                    dialog.show(supportFragmentManager, "ToiletDialog")
                }

                itemView.tv_delete.setOnClickListener {
                    val dialog = BasicDialog(
                            onLeftButton = {
                                taskCommentDelete(mCommentList[position], position)
                            },
                            onCenterButton = {

                            },
                            onRightButton = {

                            }
                    )
                    dialog.setTextContent(ObserverManager.context!!.resources.getString(R.string.home_text_06))
                    dialog.setBtnLeft(ObserverManager.context!!.resources.getString(R.string.confirm))
                    dialog.setBtnRight(ObserverManager.context!!.resources.getString(R.string.cancel))
                    dialog.show(supportFragmentManager, "BasicDialog")
                }
                itemView.tv_report.setOnClickListener {
                    if (SharedManager.isLoginCheck()) {
                        val dialog = CommentReportDialog()
                        dialog.setComment(mCommentList[position])
                        dialog.show(supportFragmentManager, "CommentReportDialog")
                    } else {
                        ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                        )
                    }
                }
            }
        }
    }

    override fun setToolbar() {
        toolbar.setImageLeftOne(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_navigationbar_back))
        toolbar.layout_like.visibility = View.VISIBLE
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

}