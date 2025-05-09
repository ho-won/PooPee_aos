package kr.co.ho1.poopee.manager.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.RecyclerViewPositionHelper
import kr.co.ho1.poopee.databinding.DialogToilet2ListBinding
import kr.co.ho1.poopee.databinding.ItemKakaoKeywordBinding
import kr.co.ho1.poopee.home.model.Toilet
import org.json.JSONException

class Toilet2ListDialog(private var onMove: ((toilet: Toilet) -> Unit)) : BaseDialog() {
    private var _binding: DialogToilet2ListBinding? = null
    private val binding get() = _binding!!

    private var mRecyclerAdapter: ListAdapter = ListAdapter()
    private var mToiletList: ArrayList<Toilet> = arrayListOf()

    private val pageSize = 20
    private var visibleThreshold = 15
    private var previousTotal = 0
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var loading = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogToilet2ListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    private fun init() {
        binding.recyclerView.layoutManager = LinearLayoutManager(ObserverManager.context)
        mRecyclerAdapter = ListAdapter()
        binding.recyclerView.adapter = mRecyclerAdapter
        taskToiletListAll(-1)
    }

    private fun setListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // 리스트뷰 페이징 로딩 처리
                val recyclerViewHelper = RecyclerViewPositionHelper(recyclerView)
                visibleItemCount = recyclerView.childCount
                totalItemCount = recyclerViewHelper.getItemCount()
                firstVisibleItem = recyclerViewHelper.findFirstVisibleItemPosition()
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }
                if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                    taskToiletListAll(mToiletList.get(mToiletList.size - 1).toilet_id)
                    loading = true
                }
            }
        })
    }

    /**
     * [GET] 화장실목록
     */
    private fun taskToiletListAll(last_id: Int) {
        val params = RetrofitParams()
        params.put("last_id", last_id)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).toiletListAll(params.getParams())

        RetrofitJSONObject(request,
            onSuccess = {
                try {
                    if (it.getInt("rst_code") == 0) {
                        val jsonArray = it.getJSONArray("toilets")

                        if (last_id == -1) {
                            mToiletList = arrayListOf()
                            previousTotal = 0
                            loading = true
                            visibleThreshold = 15

                            mRecyclerAdapter = ListAdapter()
                            binding.recyclerView.adapter = mRecyclerAdapter
                        }

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val toilet = Toilet()
                            toilet.toilet_id = jsonObject.getInt("toilet_id")
                            toilet.member_id = jsonObject.getString("member_id")
                            toilet.type = "유저"
                            toilet.m_name = jsonObject.getString("m_name")
                            toilet.name = jsonObject.getString("name")
                            toilet.content = jsonObject.getString("content")
                            toilet.address_new = jsonObject.getString("address_new")
                            toilet.address_old = jsonObject.getString("address_old")
                            toilet.unisex = if (jsonObject.getInt("unisex") == 1) "Y" else "N"
                            toilet.m_poo = jsonObject.getString("man")
                            toilet.w_poo = jsonObject.getString("woman")
                            toilet.latitude = jsonObject.getDouble("latitude")
                            toilet.longitude = jsonObject.getDouble("longitude")
                            toilet.created = jsonObject.getString("created")

                            if (toilet.address_new.isEmpty()) {
                                toilet.address_new = toilet.address_old
                            }
                            mToiletList.add(toilet)
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

    /**
     * 공지사항 목록 adapter
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
            return mToiletList.size
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        inner class ViewHolder(private val binding: ItemKakaoKeywordBinding) : RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun update(position: Int) {
                binding.tvTitle.maxLines = 1
                binding.tvSub.maxLines = 1
                binding.tvTitle.text = mToiletList[position].address_new
                binding.tvSub.text = mToiletList[position].created + " (" + mToiletList[position].m_name + ")"

                itemView.setOnClickListener {
                    onMove(mToiletList[position])
                    dismiss()
                }
            }
        }
    }

}