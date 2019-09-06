package kr.ho1.poopee.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_toilet.*
import kotlinx.android.synthetic.main.item_toilet_comment.view.*
import kotlinx.android.synthetic.main.item_toilet_content.view.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.data.SharedManager
import kr.ho1.poopee.common.dialog.BasicDialog
import kr.ho1.poopee.common.http.RetrofitClient
import kr.ho1.poopee.common.http.RetrofitJSONObject
import kr.ho1.poopee.common.http.RetrofitParams
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.common.util.MyUtil
import kr.ho1.poopee.home.model.Comment
import kr.ho1.poopee.home.model.Toilet
import kr.ho1.poopee.home.view.CommentReportDialog
import kr.ho1.poopee.home.view.CommentUpdateDialog
import kr.ho1.poopee.login.LoginActivity
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

    private fun init() {
        mToilet = intent.getSerializableExtra(TOILET) as Toilet

        recycler_view.layoutManager = LinearLayoutManager(this)
        mRecyclerAdapter = ListAdapter()
        recycler_view.adapter = mRecyclerAdapter

        taskCommentList()
    }

    private fun setListener() {
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
                            mToilet.comment_count = it.getString("comment_count")
                            mToilet.like_count = it.getString("like_count")
                            mToilet.like_check = it.getString("like_check") == "1"

                            val jsonArray = it.getJSONArray("comments")
                            mCommentList = ArrayList()

                            val content = Comment()
                            content.view_type = Toilet.VIEW_CONTENT
                            mCommentList.add(content)

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
                            mToilet.like_count = it.getString("like_count")
                            mToilet.like_check = it.getString("like_check") == "1"
                            mRecyclerAdapter.notifyItemChanged(0)
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
            return if (viewType == Toilet.VIEW_CONTENT) {
                ContentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_toilet_content, parent, false))
            } else {
                CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_toilet_comment, parent, false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (mCommentList[position].view_type == Toilet.VIEW_CONTENT) {
                (holder as ContentViewHolder).update()
            } else {
                (holder as CommentViewHolder).update(position)
            }
        }

        override fun getItemCount(): Int {
            return mCommentList.size
        }

        override fun getItemViewType(position: Int): Int {
            return mCommentList[position].view_type
        }

        inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun update() {
                itemView.tv_title.text = mToilet.name
                itemView.tv_content.text = mToilet.name
                itemView.tv_comment_count.text = String.format(ObserverManager.context!!.resources.getString(R.string.home_text_03), mToilet.comment_count)
                itemView.tv_like_count.text = String.format(ObserverManager.context!!.resources.getString(R.string.home_text_04), mToilet.like_count)
                itemView.btn_like.isChecked = mToilet.like_check

                itemView.btn_like.setOnClickListener {
                    if (SharedManager.isLoginCheck()) {
                        taskToiletLike()
                    } else {
                        itemView.btn_like.isChecked = false
                        ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                        )
                    }
                }
            }
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
        toolbar.setTitle(ObserverManager.context!!.resources.getString(R.string.home_text_02))
        toolbar.setImageLeftOne(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_bar_back))
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