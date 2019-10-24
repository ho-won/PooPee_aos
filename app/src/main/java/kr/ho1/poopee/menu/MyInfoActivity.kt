package kr.ho1.poopee.menu

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_my_info.*
import kotlinx.android.synthetic.main.view_toolbar.view.*
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
        if (SharedManager.getMemberGender() == "0") {
            rb_man.isChecked = true
        } else {
            rb_woman.isChecked = true
        }
    }

    private fun setListener() {
        edt_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (edt_name.text.isEmpty()) {
                    btn_name_delete.visibility = View.GONE
                } else {
                    btn_name_delete.visibility = View.VISIBLE
                }
            }
        })
        edt_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (edt_password.text.isEmpty()) {
                    btn_password_delete.visibility = View.GONE
                } else {
                    btn_password_delete.visibility = View.VISIBLE
                }
                if (edt_password.text.toString() != edt_password_confirm.text.toString()) {
                    tv_password_ex.text = ObserverManager.context!!.resources.getString(R.string.toast_join_condition_01)
                    tv_password_ex.setTextColor(Color.parseColor("#ff4a5c"))
                    iv_password_ex.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_alret_error))
                } else {
                    tv_password_ex.text = ObserverManager.context!!.resources.getString(R.string.login_14)
                    tv_password_ex.setTextColor(Color.parseColor("#d0d2d5"))
                    iv_password_ex.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_alret))
                }
            }
        })
        edt_password_confirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (edt_password_confirm.text.isEmpty()) {
                    btn_password_confirm_delete.visibility = View.GONE
                } else {
                    btn_password_confirm_delete.visibility = View.VISIBLE
                }
                if (edt_password.text.toString() != edt_password_confirm.text.toString()) {
                    tv_password_ex.text = ObserverManager.context!!.resources.getString(R.string.toast_join_condition_01)
                    tv_password_ex.setTextColor(Color.parseColor("#ff4a5c"))
                    iv_password_ex.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_alret_error))
                } else {
                    tv_password_ex.text = ObserverManager.context!!.resources.getString(R.string.login_14)
                    tv_password_ex.setTextColor(Color.parseColor("#d0d2d5"))
                    iv_password_ex.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_alret))
                }
            }
        })
        btn_name_delete.setOnClickListener {
            edt_name.setText("")
        }
        btn_password_delete.setOnClickListener {
            edt_password.setText("")
        }
        btn_password_confirm_delete.setOnClickListener {
            edt_password_confirm.setText("")
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
        toolbar.tv_right_one.visibility = View.VISIBLE
        toolbar.tv_right_one.text = ObserverManager.context!!.resources.getString(R.string.modified)
        toolbar.tv_right_one.setOnClickListener {
            if (edt_name.text.isEmpty()) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_please_name_input), Toast.LENGTH_SHORT).show()
            } else if (edt_password.text.toString() != edt_password_confirm.text.toString()) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_join_condition_01), Toast.LENGTH_SHORT).show()
            } else if (!rb_man.isChecked && !rb_woman.isChecked) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_join_condition_02), Toast.LENGTH_SHORT).show()
            } else {
                taskUpdateUser(edt_password.text.toString(), edt_name.text.toString(), if (rb_man.isChecked) "0" else "1")
            }
        }
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