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
import kr.co.ho1.poopee.home.model.Toilet
import org.json.JSONException

@SuppressLint("ValidFragment")
class ToiletUpdateDialog(private var toilet: Toilet, private var onUpdate: ((toilet: Toilet) -> Unit)) : BaseDialog() {

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
        btn_send.text = MyUtil.getString(R.string.modified)

        edt_title.setText(toilet.name)
        edt_content.setText(toilet.content)

        if (toilet.m_poo.toInt() > 0) {
            cb_man.isChecked = true
            cb_woman.isChecked = false
            cb_public.isChecked = false
        } else if (toilet.w_poo.toInt() > 0) {
            cb_man.isChecked = false
            cb_woman.isChecked = true
            cb_public.isChecked = false
        } else {
            cb_man.isChecked = false
            cb_woman.isChecked = false
            cb_public.isChecked = true
        }
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
            taskUpdateToilet()
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
     * [POST] 화장실추가
     */
    private fun taskUpdateToilet() {
        toilet.name = edt_title.text.toString()
        toilet.content = edt_content.text.toString()

        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())
        params.put("toilet_id", toilet.toilet_id)
        params.put("name", toilet.name) // 화장실명
        params.put("content", toilet.content) // 화장실설명

        when {
            cb_man.isChecked -> {
                params.put("type", 1) // 0(공용) 1(남자) 2(여자)
                toilet.m_poo = "1"
                toilet.w_poo = "0"
                toilet.unisex = "N"
            }
            cb_woman.isChecked -> {
                params.put("type", 2) // 0(공용) 1(남자) 2(여자)
                toilet.m_poo = "0"
                toilet.w_poo = "1"
                toilet.unisex = "N"
            }
            else -> {
                params.put("type", 0) // 0(공용) 1(남자) 2(여자)
                toilet.m_poo = "0"
                toilet.w_poo = "0"
                toilet.unisex = "Y"
            }
        }

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).toiletUpdate(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_update_complete), Toast.LENGTH_SHORT).show()
                            onUpdate(toilet)
                            dismiss()
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