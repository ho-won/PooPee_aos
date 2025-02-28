package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.StrManager
import kr.co.ho1.poopee.databinding.DialogToiletBinding
import kr.co.ho1.poopee.home.model.Toilet
import org.json.JSONException


@SuppressLint("ValidFragment")
class ToiletDialog(private var onDetail: ((toilet: Toilet) -> Unit)) : BaseDialog() {
    private var _binding: DialogToiletBinding? = null
    private val binding get() = _binding!!

    private var mToilet: Toilet = Toilet()
    private var mAddressText: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogToiletBinding.inflate(layoutInflater)
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
        binding.tvTitle.text = mToilet.name

        mAddressText = ""

        when {
            mToilet.address_new.count() > 0 -> mAddressText = mToilet.address_new
            mToilet.address_old.count() > 0 -> mAddressText = mToilet.address_old
            else -> // 휴게소, 졸음쉼터는 주소정보가 없어서 카카오 api 로 주소 찾기
                taskKakaoCoordToAddress()
        }

        StrManager.setAddressCopySpan(binding.tvAddress, mAddressText)

        binding.tvCommentCount.text = mToilet.comment_count
        binding.tvLikeCount.text = mToilet.like_count

        taskToiletCount()
    }

    private fun setListener() {
        binding.layoutNavi.setOnClickListener {
            val dialog = ShareDialog()
            dialog.setAction(ShareDialog.ACTION_NAVI)
            dialog.setToilet(mToilet)
            dialog.show(ObserverManager.root!!.supportFragmentManager, "ShareDialog")
        }
        binding.layoutShare.setOnClickListener {
            val dialog = ShareDialog()
            dialog.setAction(ShareDialog.ACTION_SHARE)
            dialog.setToilet(mToilet)
            dialog.show(ObserverManager.root!!.supportFragmentManager, "ShareDialog")
        }
        binding.tvTitle.setOnClickListener {
            binding.btnDetail.performClick()
        }
        binding.btnDetail.setOnClickListener {
            onDetail(mToilet)
            dismiss()
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    fun setToilet(toilet: Toilet) {
        this.mToilet = toilet
    }

    private fun taskToiletCount() {
        val params = RetrofitParams()
        params.put("toilet_id", mToilet.toilet_id)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).toiletInfo(params.getParams())

        RetrofitJSONObject(request,
            onSuccess = {
                try {
                    if (it.getInt("rst_code") == 0) {
                        mToilet.comment_count = it.getString("comment_count")
                        mToilet.like_count = it.getString("like_count")

                        binding.tvCommentCount.text = mToilet.comment_count
                        binding.tvLikeCount.text = mToilet.like_count
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
     * [GET] 카카오 좌표 -> 주소 변환
     */
    private fun taskKakaoCoordToAddress() {
        val params = RetrofitParams()
        params.put("x", mToilet.longitude) // longitude
        params.put("y", mToilet.latitude) // latitude

        val request = RetrofitClient.getClientKaKao(RetrofitService.KAKAO_LOCAL).create(RetrofitService::class.java).kakaoLocalCoordToAddress(params.getParams())

        RetrofitJSONObject(request,
            onSuccess = {
                try {
                    val totalCount = it.getJSONObject("meta").getInt("total_count")
                    if (totalCount > 0) {
                        val jsonObject = it.getJSONArray("documents").getJSONObject(0)

                        var addressText = ""
                        if (!jsonObject.isNull("road_address")) {
                            addressText = jsonObject.getJSONObject("road_address").getString("address_name")
                        } else if (!jsonObject.isNull("address")) {
                            addressText = jsonObject.getJSONObject("address").getString("address_name")
                        }
                        mToilet.address_new = addressText
                        StrManager.setAddressCopySpan(binding.tvAddress, addressText)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            onFailed = {

            }
        )
    }

}