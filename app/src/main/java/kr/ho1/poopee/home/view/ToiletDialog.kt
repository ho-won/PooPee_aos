package kr.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_toilet.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseDialog
import kr.ho1.poopee.common.http.RetrofitClient
import kr.ho1.poopee.common.http.RetrofitJSONObject
import kr.ho1.poopee.common.http.RetrofitParams
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.common.util.StrManager
import kr.ho1.poopee.home.ToiletActivity
import kr.ho1.poopee.home.model.Toilet
import org.json.JSONException


@SuppressLint("ValidFragment")
class ToiletDialog : BaseDialog() {
    private var mToilet: Toilet = Toilet()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_toilet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    private fun init() {
        tv_title.text = mToilet.name

        var addressText = ""

        if (mToilet.address_new.count() > 0) {
            addressText = mToilet.address_new
        } else if (mToilet.address_new.count() > 0) {
            addressText = mToilet.address_old
        } else {
            // 휴게소, 졸음쉼터는 주소정보가 없어서 Geocode 로 주소 찾기
            val geocode = Geocoder(ObserverManager.context)
            val list = geocode.getFromLocation(mToilet.latitude, mToilet.longitude, 1)
            if (list != null && list.size > 0) {
                addressText = list[0].getAddressLine(0)
                addressText = addressText.replace("대한민국 ", "")
                mToilet.address_new = addressText
            }
        }

        StrManager.setAddressCopySpan(tv_address, addressText)

        tv_comment_count.text = mToilet.comment_count
        tv_like_count.text = mToilet.like_count

        taskToiletCount()
    }

    private fun setListener() {
        tv_title.setOnClickListener {
            btn_detail.performClick()
        }
        btn_detail.setOnClickListener {
            ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, ToiletActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .putExtra(ToiletActivity.TOILET, mToilet)
            )
        }
        btn_close.setOnClickListener {
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

                            tv_comment_count.text = mToilet.comment_count
                            tv_like_count.text = mToilet.like_count
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