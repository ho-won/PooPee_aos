package kr.co.ho1.poopee.menu

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
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.common.util.StrManager
import kr.co.ho1.poopee.databinding.ActivityNoticeBinding
import kr.co.ho1.poopee.databinding.ItemNoticeBinding
import kr.co.ho1.poopee.menu.model.Notice
import org.json.JSONException
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

@Suppress("DEPRECATION")
class NoticeActivity : BaseActivity() {
    private lateinit var binding: ActivityNoticeBinding

    private var mRecyclerAdapter: ListAdapter = ListAdapter()
    private var mNoticeList: ArrayList<Notice> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        init()
        setListener()
    }

    private fun init() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerAdapter = ListAdapter()
        binding.recyclerView.adapter = mRecyclerAdapter
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
                        mNoticeList = ArrayList()
                        val jsonArray = it.getJSONArray("notices")

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val notice = Notice()
                            notice.notice_id = jsonObject.getString("notice_id")
                            notice.title = jsonObject.getString("title")
                            notice.content = jsonObject.getString("content")
                            notice.created = jsonObject.getString("created")
                            notice.openCheck = i == 0

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
            val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
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

        inner class ViewHolder(private val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root), Html.ImageGetter {

            fun update(position: Int) {
                binding.tvTitle.text = mNoticeList[position].title
                binding.tvDate.text = StrManager.getDateTime(mNoticeList[position].created)

                val spanned = Html.fromHtml(mNoticeList[position].content, this, null)
                binding.tvContent.text = spanned
                binding.tvContent.isClickable = true
                binding.tvContent.movementMethod = LinkMovementMethod.getInstance()

                if (mNoticeList[position].openCheck) {
                    binding.layoutContent.visibility = View.VISIBLE
                } else {
                    binding.layoutContent.visibility = View.GONE
                }
                binding.cbDetail.isChecked = mNoticeList[position].openCheck

                itemView.setOnClickListener {
                    binding.cbDetail.isChecked = !binding.cbDetail.isChecked
                    if (binding.cbDetail.isChecked) {
                        binding.layoutContent.visibility = View.VISIBLE
                        mNoticeList[position].openCheck = true
                    } else {
                        binding.layoutContent.visibility = View.GONE
                        mNoticeList[position].openCheck = false
                    }
                    notifyDataSetChanged()
                }
            }

            override fun getDrawable(source: String?): Drawable {
                val d = LevelListDrawable()
                val empty = MyUtil.getDrawable(R.mipmap.ic_launcher)
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

                        if (bitmap.width > binding.tvContent.width) {
                            width = binding.tvContent.width
                            height = getHeight(bitmap.width, bitmap.height, binding.tvContent.width)
                        } else {
                            width = bitmap.width
                            height = bitmap.height
                        }

                        val d = BitmapDrawable(bitmap)
                        mDrawable!!.addLevel(1, 1, d)
                        mDrawable!!.setBounds(0, 0, width, height)
                        mDrawable!!.level = 1
                        val t = binding.tvContent.text
                        binding.tvContent.text = t
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
        binding.toolbar.setTitle(MyUtil.getString(R.string.nav_text_02))
        binding.toolbar.setImageLeftOne(MyUtil.getDrawable(R.drawable.ic_navigationbar_back))
        binding.toolbar.setSelectedListener(
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