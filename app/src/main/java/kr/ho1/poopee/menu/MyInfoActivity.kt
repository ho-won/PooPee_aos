package kr.ho1.poopee.menu

import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_my_info.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.data.SharedManager
import kr.ho1.poopee.common.http.RetrofitClient
import kr.ho1.poopee.common.http.RetrofitJSONObject
import kr.ho1.poopee.common.http.RetrofitParams
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.common.util.StringFilter
import org.json.JSONException

@Suppress("DEPRECATION")
class MyInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)
        setToolbar()

        init()
        setListener()
    }

    private fun init() {
        edt_password.filters = StringFilter.getAlphanumeric(20)
        edt_password_confirm.filters = StringFilter.getAlphanumeric(20)
        edt_name.filters = StringFilter.getAlphanumericHangul(20)

        edt_name.setText(SharedManager.getMemberName())
        if (SharedManager.getMemberGender() == "1") {
            rb_man.isChecked = true
        } else {
            rb_woman.isChecked = true
        }
    }

    private fun setListener() {
        btn_modified.setOnClickListener {
            if (edt_password.text.toString() != edt_password_confirm.text.toString()) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_join_condition_01), Toast.LENGTH_SHORT).show()
            } else if (!rb_man.isChecked && !rb_woman.isChecked) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_join_condition_02), Toast.LENGTH_SHORT).show()
            } else {
                taskUpdateUser(edt_password.text.toString(), edt_name.text.toString(), if (rb_man.isChecked) "0" else "1")
            }
        }
    }

    /**
     * [PUT] 회원정보수정
     */
    private fun taskUpdateUser(password: String, name: String, gender: String) {
        showLoading()
        val params = RetrofitParams()
        params.put("member_id", SharedManager.getMemberId())

        var pw = SharedManager.getMemberPassword()
        if (password.isNotEmpty()) {
            pw = password
        }
        params.put("password", pw)

        params.put("name", name)
        params.put("gender", gender)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).updateUser(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            SharedManager.setMemberPassword(pw)
                            SharedManager.setMemberName(name)
                            SharedManager.setMemberGender(gender)
                            Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_update_complete), Toast.LENGTH_SHORT).show()
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
        toolbar.setTitle(ObserverManager.context!!.resources.getString(R.string.nav_text_01))
        toolbar.setImageLeftOne(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_navigationbar_back))
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