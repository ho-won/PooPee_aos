package kr.ho1.poopee.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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

@Suppress("DEPRECATION")
class JoinActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        init()
        setListener()
    }

    private fun init() {
        edt_username.filters = StringFilter.getAlphanumeric(20)
        edt_password.filters = StringFilter.getAlphanumeric(20)
        edt_password_confirm.filters = StringFilter.getAlphanumeric(20)
        edt_name.filters = StringFilter.getAlphanumericHangul(20)
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
                checkAllInput()
            }
        })
        edt_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tv_id_ex.text = ObserverManager.context!!.resources.getString(R.string.login_11)
                tv_id_ex.setTextColor(Color.parseColor("#d0d2d5"))
                iv_id_ex.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_alret))
                tv_overlap.isEnabled = edt_username.text.length > 5
                checkAllInput()
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
                checkAllInput()
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
                checkAllInput()
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
        tv_overlap.setOnClickListener {
            taskOverlap(edt_username.text.toString())
        }
        cb_terms_01.setOnCheckedChangeListener { _, _ ->
            checkAllInput()
        }
        cb_terms_02.setOnCheckedChangeListener { _, _ ->
            checkAllInput()
        }
        cb_terms_03.setOnCheckedChangeListener { _, _ ->
            checkAllInput()
        }
        tv_terms_01_detail.setOnClickListener {
            startActivity(Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_01)
            )
        }
        tv_terms_02_detail.setOnClickListener {
            startActivity(Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_02)
            )
        }
        tv_terms_03_detail.setOnClickListener {
            startActivity(Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_03)
            )
        }
        layout_back.setOnClickListener {
            finish()
        }
        layout_join.setOnClickListener {
            if (edt_username.text.toString().isEmpty() || edt_password.text.toString().isEmpty() || edt_password_confirm.text.toString().isEmpty() || edt_name.text.toString().isEmpty()) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_please_all_input), Toast.LENGTH_SHORT).show()
            } else if (edt_password.text.toString() != edt_password_confirm.text.toString()) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_join_condition_01), Toast.LENGTH_SHORT).show()
            } else if (!rb_man.isChecked && !rb_woman.isChecked) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_join_condition_02), Toast.LENGTH_SHORT).show()
            } else if (!cb_terms_01.isChecked || !cb_terms_02.isChecked || !cb_terms_03.isChecked) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_terms_check), Toast.LENGTH_SHORT).show()
            } else {
                taskJoin(edt_username.text.toString(), edt_password.text.toString(), edt_name.text.toString(), if (rb_man.isChecked) "0" else "1")
            }
        }
    }

    private fun checkAllInput() {
        layout_join.isEnabled = false
        tv_join.setTextColor(Color.parseColor("#6b9bff"))
        iv_join.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_join_nonext))

        if (edt_name.text.isEmpty()) {
            return
        }
        if (!rb_man.isChecked && !rb_woman.isChecked) {
            return
        }
        if (edt_username.text.isEmpty() || edt_username.text.length < 6) {
            return
        }
        if (edt_password.text.isEmpty() || edt_password_confirm.text.isEmpty()) {
            return
        }
        if (edt_password.text.toString() != edt_password_confirm.text.toString()) {
            return
        }
        if (!cb_terms_01.isChecked || !cb_terms_02.isChecked || !cb_terms_03.isChecked) {
            return
        }

        layout_join.isEnabled = true
        tv_join.setTextColor(Color.parseColor("#ffffff"))
        iv_join.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_join_next))
    }

    /**
     * [GET] 아이디 중복체크
     */
    private fun taskOverlap(username: String) {
        showLoading()
        val params = RetrofitParams()
        params.put("username", username)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).overLap(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            tv_id_ex.text = ObserverManager.context!!.resources.getString(R.string.login_12)
                            tv_id_ex.setTextColor(Color.parseColor("#2470ff"))
                            iv_id_ex.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_alret_check))
                        } else if (it.getInt("rst_code") == 1) {
                            tv_id_ex.text = ObserverManager.context!!.resources.getString(R.string.toast_join_id_fail)
                            tv_id_ex.setTextColor(Color.parseColor("#ff4a5c"))
                            iv_id_ex.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_alret_error))
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

    /**
     * [POST] 회원가입
     */
    private fun taskJoin(username: String, password: String, name: String, gender: String) {
        showLoading()
        val params = RetrofitParams()
        params.put("username", username)
        params.put("password", password)
        params.put("name", name)
        params.put("gender", gender)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).join(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_join_complete), Toast.LENGTH_SHORT).show()
                            MyUtil.keyboardHide(edt_username)
                            finish()
                        } else if (it.getInt("rst_code") == 1) {
                            Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_join_id_fail), Toast.LENGTH_SHORT).show()
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