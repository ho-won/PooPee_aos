package kr.ho1.poopee.login

import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_join.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.http.RetrofitClient
import kr.ho1.poopee.common.http.RetrofitJSONObject
import kr.ho1.poopee.common.http.RetrofitParams
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.common.util.MyUtil
import kr.ho1.poopee.common.util.StringFilter
import org.json.JSONException

class JoinActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        setToolbar()

        init()
        setListener()
    }

    private fun init() {
        edt_user_id.filters = StringFilter.getAlphanumericHangul(10)
        edt_user_pw.filters = StringFilter.getAlphanumeric(20)
        edt_user_pw_confirm.filters = StringFilter.getAlphanumeric(20)
        edt_name.filters = StringFilter.getAlphanumericHangul(10)
    }

    private fun setListener() {
        btn_join.setOnClickListener {
            if (edt_user_id.text.toString().isEmpty() || edt_user_pw.text.toString().isEmpty() || edt_user_pw.text.toString().isEmpty() || edt_name.text.toString().isEmpty()) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_please_all_input), Toast.LENGTH_SHORT).show()
            } else if (edt_user_pw.text.toString() != edt_user_pw.text.toString()) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_join_condition_01), Toast.LENGTH_SHORT).show()
            } else if (!rb_man.isChecked && !rb_woman.isChecked) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_join_condition_02), Toast.LENGTH_SHORT).show()
            } else {
                taskJoin(edt_user_id.text.toString(), edt_user_pw.text.toString(), edt_name.text.toString(), if (rb_man.isChecked) "1" else "2")
            }
        }
    }

    private fun taskJoin(user_id: String, user_pw: String, name: String, gender: String) {
        showLoading()
        val params = RetrofitParams()
        params.put("user_id", user_id)
        params.put("user_pw", user_pw)
        params.put("name", name)
        params.put("gender", gender)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).join(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_join_complete), Toast.LENGTH_SHORT).show()
                            MyUtil.keyboardHide(edt_user_id)
                            finish()
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

    override fun setToolbar() {
        toolbar.setImageLeftOne(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_toolbar_back))
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