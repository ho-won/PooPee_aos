package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_toilet_create.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.MyUtil
import org.json.JSONException

@SuppressLint("ValidFragment")
class ToiletCreateDialog(private var latitude: Double, private var longitude: Double, private var onCreate: (() -> Unit)) : BaseDialog() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_toilet_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    override fun dismiss() {
        MyUtil.keyboardHide(edt_content)
        super.dismiss()
    }

    private fun init() {

    }

    private fun setListener() {
        edt_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty() && isGubunChecked()) {
                    btn_send.setTextColor(Color.parseColor("#2470ff"))
                    btn_send.isEnabled = true
                } else {
                    btn_send.setTextColor(Color.parseColor("#a0a4aa"))
                    btn_send.isEnabled = false
                }
            }
        })
        edt_content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tv_content_cnt.text = p0.toString().length.toString() + "/100"
            }
        })
        cb_public.setOnClickListener {
            setGubunCheck(cb_public)
        }
        cb_man.setOnClickListener {
            setGubunCheck(cb_man)
        }
        cb_woman.setOnClickListener {
            setGubunCheck(cb_woman)
        }
        btn_send.setOnClickListener {
            taskKakaoCoordToAddress()
        }
        btn_close.setOnClickListener {
            dismiss()
        }
    }

    private fun setGubunCheck(checkBox: CheckBox) {
        cb_public.isChecked = false
        cb_man.isChecked = false
        cb_woman.isChecked = false
        checkBox.isChecked = true
        if (edt_title.text.toString().isNotEmpty()) {
            btn_send.setTextColor(Color.parseColor("#2470ff"))
            btn_send.isEnabled = true
        } else {
            btn_send.setTextColor(Color.parseColor("#a0a4aa"))
            btn_send.isEnabled = false
        }
    }

    private fun isGubunChecked(): Boolean {
        return cb_public.isChecked || cb_man.isChecked || cb_woman.isChecked
    }

    /**
     * [GET] 카카오 좌표 -> 주소 변환
     */
    private fun taskKakaoCoordToAddress() {
        val params = RetrofitParams()
        params.put("x", longitude) // longitude
        params.put("y", latitude) // latitude

        val request = RetrofitClient.getClientKaKao(RetrofitService.KAKAO_LOCAL).create(RetrofitService::class.java).kakaoLocalCoordToAddress(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        val totalCount = it.getJSONObject("meta").getInt("total_count")
                        if (totalCount > 0) {
                            val jsonObject = it.getJSONArray("documents").getJSONObject(0)

                            var address_new = ""
                            var address_old = ""
                            if (!jsonObject.isNull("road_address")) {
                                address_new = jsonObject.getJSONObject("road_address").getString("address_name")
                            }
                            if (!jsonObject.isNull("address")) {
                                address_old = jsonObject.getJSONObject("address").getString("address_name")
                            }
                            taskCreateToilet(address_new, address_old)
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
     * [POST] 화장실추가
     */
    private fun taskCreateToilet(address_new: String, address_old: String) {
        showLoading()
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("name", edt_title.text) // 화장실명
        params.put("content", edt_content.text) // 화장실설명

        when {
            cb_man.isChecked -> params.put("type", 1) // 0(공용) 1(남자) 2(여자)
            cb_woman.isChecked -> params.put("type", 2) // 0(공용) 1(남자) 2(여자)
            else -> params.put("type", 0) // 0(공용) 1(남자) 2(여자)
        }

        params.put("latitude", latitude) // 위도
        params.put("longitude", longitude) // 경도
        params.put("address_new", address_new) // 도로명주소
        params.put("address_old", address_old) // 지번주소

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).createToilet(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toilet_create_text_13), Toast.LENGTH_SHORT).show()
                            onCreate()
                            dismiss()
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

}