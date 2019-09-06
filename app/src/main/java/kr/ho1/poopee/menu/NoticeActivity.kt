package kr.ho1.poopee.menu

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_notice.*
import kotlinx.android.synthetic.main.item_notice.view.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.http.RetrofitClient
import kr.ho1.poopee.common.http.RetrofitJSONObject
import kr.ho1.poopee.common.http.RetrofitParams
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.common.util.MySpannableString
import kr.ho1.poopee.menu.model.Notice
import org.json.JSONException
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

@Suppress("DEPRECATION")
class NoticeActivity : BaseActivity() {

    private var mRecyclerAdapter: ListAdapter = ListAdapter()
    private var mNoticeList: ArrayList<Notice> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        setToolbar()

        init()
        setListener()
    }

    private fun init() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        mRecyclerAdapter = ListAdapter()
        recycler_view.adapter = mRecyclerAdapter
        taskNoticeList()
    }

    private fun setListener() {

    }

    /**
     * [GET] 공지사항목록
     */
    private fun taskNoticeList() {
        showLoading()
        val params = RetrofitParams()

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).noticeList(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            val jsonArray = it.getJSONArray("notices")

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val notice = Notice()
                                notice.notice_id = jsonObject.getString("notice_id")
                                notice.title = jsonObject.getString("title")
                                notice.content = jsonObject.getString("content")
                                notice.created = jsonObject.getString("created").substring(5, 10)

                                mNoticeList.add(notice)
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
     * 공지사항 목록 adapter
     */
    inner class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notice, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ViewHolder).update(position)
        }

        override fun getItemCount(): Int {
            return mNoticeList.size
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), Html.ImageGetter {

            fun update(position: Int) {
                val sizeString: Array<String> = arrayOf(mNoticeList[position].created)
                val colorString: Array<String> = arrayOf(mNoticeList[position].created)

                val span = MySpannableString(mNoticeList[position].title + " " + mNoticeList[position].created)
                span.setSize(sizeString, 10)
                span.setColor(colorString, "#999999")
                itemView.tv_title.text = span.getSpannableString()

                val spanned = Html.fromHtml(mNoticeList[position].content, this, null)
                itemView.tv_content.text = spanned
                itemView.tv_content.isClickable = true
                itemView.tv_content.movementMethod = LinkMovementMethod.getInstance()

                if (mNoticeList[position].openCheck) {
                    itemView.layout_content.visibility = View.VISIBLE
                } else {
                    itemView.layout_content.visibility = View.GONE
                }

                itemView.layout_title.setOnClickListener {
                    if (itemView.layout_content.visibility == View.VISIBLE) {
                        itemView.layout_content.visibility = View.GONE
                        mNoticeList[position].openCheck = false
                    } else {
                        itemView.layout_content.visibility = View.VISIBLE
                        mNoticeList[position].openCheck = true
                    }
                    notifyDataSetChanged()
                }
            }

            override fun getDrawable(source: String?): Drawable {
                val d = LevelListDrawable()
                val empty = ObserverManager.context!!.resources.getDrawable(R.mipmap.ic_launcher)
                d.addLevel(0, 0, empty)
                d.setBounds(0, 0, empty.intrinsicWidth, empty.intrinsicHeight)

                LoadImage().execute(source, d)

                return d
            }

            /**
             * HTML 이미지 로딩
             */
            @SuppressLint("StaticFieldLeak")
            internal inner class LoadImage : AsyncTask<Any, Void, Bitmap>() {
                private var mDrawable: LevelListDrawable? = null

                override fun doInBackground(vararg params: Any): Bitmap? {
                    val source = params[0] as String
                    mDrawable = params[1] as LevelListDrawable
                    try {
                        val `is` = URL(source).openStream()
                        return BitmapFactory.decodeStream(`is`)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    return null
                }

                override fun onPostExecute(bitmap: Bitmap?) {
                    if (bitmap != null) {
                        val width: Int
                        val height: Int

                        if (bitmap.width > itemView.tv_content.width) {
                            width = itemView.tv_content.width
                            height = getHeight(bitmap.width, bitmap.height, itemView.tv_content.width)
                        } else {
                            width = bitmap.width
                            height = bitmap.height
                        }

                        val d = BitmapDrawable(bitmap)
                        mDrawable!!.addLevel(1, 1, d)
                        mDrawable!!.setBounds(0, 0, width, height)
                        mDrawable!!.level = 1
                        val t = itemView.tv_content.text
                        itemView.tv_content.text = t
                    }
                }

                private fun getHeight(bmpWidth: Int, bmpHeight: Int, tvWidth: Int): Int {
                    val h = bmpWidth / tvWidth
                    return bmpHeight / h
                }
            }
        }
    }

    override fun setToolbar() {
        toolbar.setTitle(ObserverManager.context!!.resources.getString(R.string.nav_text_02))
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