package kr.co.ho1.poopee.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.common.util.StringFilter
import kr.co.ho1.poopee.databinding.ActivityLoginBinding
import org.json.JSONException
import retrofit2.http.POST

@Suppress("DEPRECATION")
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListener()
    }

    private fun init() {
        binding.edtUsername.filters = StringFilter.getAlphanumeric(20)
        binding.edtPassword.filters = StringFilter.getAlphanumeric(20)
    }

    private fun setListener() {
        binding.btnClose.setOnClickListener {
            finish()
        }
        binding.btnLogin.setOnClickListener {
            if (binding.edtUsername.text.toString().isEmpty() || binding.edtPassword.text.toString().isEmpty()) {
                Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_login_condition), Toast.LENGTH_SHORT).show()
            } else {
                taskLogin(binding.edtUsername.text.toString(), binding.edtPassword.text.toString())
            }
        }
        binding.layoutJoin.setOnClickListener {
            startActivity(
                Intent(ObserverManager.context!!, JoinActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
        binding.edtUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkIdPw()
            }
        })
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkIdPw()
            }
        })
    }

    private fun checkIdPw() {
        binding.btnLogin.isEnabled = binding.edtUsername.text.isNotEmpty() && binding.edtPassword.text.isNotEmpty()
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
                        SharedManager.isLoginCheck = true
                        SharedManager.memberId = it.getString("member_id")
                        SharedManager.memberUsername = username
                        SharedManager.memberPassword = password
                        SharedManager.memberName = it.getString("name")
                        SharedManager.memberGender = it.getString("gender")
                        MyUtil.keyboardHide(binding.edtUsername)
                        finish()
                    } else {
                        Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_login_fail), Toast.LENGTH_SHORT).show()
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