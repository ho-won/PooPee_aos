package kr.co.ho1.poopee.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_toilet_search.*
import kotlinx.android.synthetic.main.item_kakao_keyword.view.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.home.model.KaKaoKeyword
import org.json.JSONException

class ToiletSearchActivity : BaseActivity() {
    private var mKeywordAdapter: ListAdapter = ListAdapter()
    private var mKeywordList: ArrayList<KaKaoKeyword> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toilet_search)

        ad_view.loadAd(AdRequest.Builder().build())

        init()
        setListener()
    }

    private fun init() {
        rv_search.layoutManager = LinearLayoutManager(this)
        mKeywordAdapter = ListAdapter()
        rv_search.adapter = mKeywordAdapter
    }

    private fun setListener() {
        btn_back.setOnClickListener {
            finish()
        }
        btn_delete.setOnClickListener {
            edt_search.setText("")
        }
        btn_map.setOnClickListener {
            ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, ToiletCreateActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
        btn_bottom.setOnClickListener {
            btn_map.performClick()
        }
        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) {
                    rv_search.visibility = View.GONE
                    layout_no_list.visibility = View.VISIBLE
                    btn_delete.visibility = View.GONE
                    btn_map.visibility = View.VISIBLE
                } else {
                    rv_search.visibility = View.VISIBLE
                    layout_no_list.visibility = View.GONE
                    btn_delete.visibility = View.VISIBLE
                    btn_map.visibility = View.GONE
                    taskKakaoLocalSearch(p0.toString())
                }
            }
        })
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
     * 주소검색 목록 adapter
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
                    ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, ToiletCreateActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                            .putExtra(ToiletCreateActivity.KAKAO_KEYWORD, mKeywordList[position])
                    )
                }
            }
        }
    }

}