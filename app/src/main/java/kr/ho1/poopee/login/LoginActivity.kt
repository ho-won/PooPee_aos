package kr.ho1.poopee.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.data.SharedManager
import kr.ho1.poopee.common.http.RetrofitClient
import kr.ho1.poopee.common.http.RetrofitJSONObject
import kr.ho1.poopee.common.http.RetrofitParams
import kr.ho1.poopee.common.http.RetrofitService
import kr.ho1.poopee.common.util.MyUtil
import org.json.JSONException
import retrofit2.http.POST

@Suppress("DEPRECATION")
class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setToolbar()

        init()
        setListener()
    }

    private fun init() {

    }

    private fun setListener() {
        btn_login.setOnClickListener {
            if (edt_username.text.toString().isEmpty() || edt_password.text.toString().isEmpty()) {
                Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_login_condition), Toast.LENGTH_SHORT).show()
            } else {
                taskLogin(edt_username.text.toString(), edt_password.text.toString())
            }
        }
        btn_join.setOnClickListener {
            startActivity(Intent(ObserverManager.context!!, JoinActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
    }

    /**
     * [POST] 로그인
     */
    private fun taskLogin(username: String, password: String) {
        showLoading()
        val params = RetrofitParams()
        params.put("username", username)
        params.put("password", password)
        params.put("pushkey", "test")
        params.put("os", "aos")

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).login(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            SharedManager.setLoginCheck(true)
                            SharedManager.setMemberId(it.getString("member_id"))
                            SharedManager.setMemberUsername(username)
                            SharedManager.setMemberPassword(password)
                            SharedManager.setMemberName(it.getString("name"))
                            SharedManager.setMemberGender(it.getString("gender"))
                            MyUtil.keyboardHide(edt_username)
                            finish()
                        } else {
                            Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_login_fail), Toast.LENGTH_SHORT).show()
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
        toolbar.setTitle(ObserverManager.context!!.resources.getString(R.string.login))
        toolbar.setImageLeftOne(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_bar_back))
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