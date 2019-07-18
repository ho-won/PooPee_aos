package kr.ho1.poopee.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kr.ho1.poopee.home.model.Comment
import kr.ho1.poopee.home.model.Toilet
import kr.ho1.poopee.home.view.CommentUpdateDialog
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

    }

    private fun taskCommentList() {
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
                            mCommentList = arrayListOf()

                            val content = Comment()
                            content.view_type = Toilet.VIEW_CONTENT
                            mCommentList.add(content)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val comment = Comment()
                                comment.id = jsonObject.getString("id")
                                comment.member_id = jsonObject.getString("member_id")
                                comment.name = jsonObject.getString("name")
                                comment.content = jsonObject.getString("content")
                                comment.datetime = jsonObject.getString("datetime")
                                comment.view_type = Toilet.VIEW_COMMENT

                                mCommentList.add(comment)
                            }

                            mRecyclerAdapter.notifyDataSetChanged()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                onFailed = {

                }
        )
    }

    private fun taskToiletLike(like: Int) {
        val params = RetrofitParams()
        params.put("toilet_id", mToilet.toilet_id)
        params.put("like", like)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).toiletLike(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                onFailed = {

                }
        )
    }

    private fun taskCommentDelete(comment: Comment, position: Int) {
        val params = RetrofitParams()
        params.put("comment_id", comment.id)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).commentDelete(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            mCommentList.removeAt(position)
                            mRecyclerAdapter.notifyItemRemoved(position)
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
                itemView.tv_title.text = mToilet.title
                itemView.tv_content.text = mToilet.content
                itemView.tv_comment_count.text = String.format(ObserverManager.context!!.resources.getString(R.string.home_text_03), mToilet.comment_count)
                itemView.tv_like_count.text = String.format(ObserverManager.context!!.resources.getString(R.string.home_text_03), mToilet.like_count)
                itemView.btn_like.isChecked = mToilet.like_check

                itemView.btn_like.setOnClickListener {
                    taskToiletLike(if (itemView.btn_like.isChecked) 1 else 0)
                }
            }
        }

        inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun update(position: Int) {
                itemView.tv_name.text = mCommentList[position].name
                itemView.tv_date.text = mCommentList[position].datetime
                itemView.tv_comment.text = mCommentList[position].content

                if (mCommentList[position].member_id == SharedManager.getMemberId()) {
                    itemView.tv_update.visibility = View.VISIBLE
                    itemView.tv_delete.visibility = View.VISIBLE
                } else {
                    itemView.tv_update.visibility = View.GONE
                    itemView.tv_delete.visibility = View.GONE
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