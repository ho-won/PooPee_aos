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
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.databinding.ActivityToiletSearchBinding
import kr.co.ho1.poopee.databinding.ItemKakaoKeywordBinding
import kr.co.ho1.poopee.home.model.KaKaoKeyword
import org.json.JSONException

class ToiletSearchActivity : BaseActivity() {
    private lateinit var binding: ActivityToiletSearchBinding

    companion object {
        const val RESULT_CREATE = 1001
    }

    private var keywordAdapter: ListAdapter = ListAdapter()
    private var keywordList: ArrayList<KaKaoKeyword> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToiletSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListener()
    }

    private fun init() {
        binding.rvSearch.layoutManager = LinearLayoutManager(this)
        keywordAdapter = ListAdapter()
        binding.rvSearch.adapter = keywordAdapter
    }

    private fun setListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnDelete.setOnClickListener {
            binding.edtSearch.setText("")
        }
        binding.btnMap.setOnClickListener {
            ObserverManager.root!!.startActivityForResult(
                Intent(ObserverManager.context!!, ToiletCreateActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION), RESULT_CREATE
            )
        }
        binding.btnBottom.setOnClickListener {
            binding.btnMap.performClick()
        }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) {
                    binding.rvSearch.visibility = View.GONE
                    binding.layoutNoList.visibility = View.VISIBLE
                    binding.btnDelete.visibility = View.GONE
                    binding.btnMap.visibility = View.VISIBLE
                } else {
                    binding.rvSearch.visibility = View.VISIBLE
                    binding.layoutNoList.visibility = View.GONE
                    binding.btnDelete.visibility = View.VISIBLE
                    binding.btnMap.visibility = View.GONE
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
                    keywordList = ArrayList()
                    val jsonArray = it.getJSONArray("documents")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val keyword = KaKaoKeyword()
                        keyword.address_name = jsonObject.getString("address_name")
                        keyword.place_name = jsonObject.getString("place_name")
                        keyword.latitude = jsonObject.getDouble("y")
                        keyword.longitude = jsonObject.getDouble("x")

                        keywordList.add(keyword)
                    }

                    keywordAdapter.notifyDataSetChanged()
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
            val binding = ItemKakaoKeywordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ViewHolder).update(position)
        }

        override fun getItemCount(): Int {
            return keywordList.size
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        inner class ViewHolder(private val binding: ItemKakaoKeywordBinding) : RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun update(position: Int) {
                binding.tvTitle.text = keywordList[position].place_name
                binding.tvSub.text = keywordList[position].address_name

                itemView.setOnClickListener {
                    ObserverManager.root!!.startActivityForResult(
                        Intent(ObserverManager.context!!, ToiletCreateActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                            .putExtra(ToiletCreateActivity.KAKAO_KEYWORD, keywordList[position]), RESULT_CREATE
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == RESULT_CREATE) {
            finish()
        }
    }

}