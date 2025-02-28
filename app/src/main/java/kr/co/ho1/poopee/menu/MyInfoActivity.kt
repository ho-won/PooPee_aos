package kr.co.ho1.poopee.menu

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
import kr.co.ho1.poopee.databinding.ActivityMyInfoBinding
import org.json.JSONException

@Suppress("DEPRECATION")
class MyInfoActivity : BaseActivity() {
    private lateinit var binding: ActivityMyInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        init()
        setListener()
    }

    private fun init() {
        binding.edtPassword.filters = StringFilter.getAlphanumeric(20)
        binding.edtPasswordConfirm.filters = StringFilter.getAlphanumeric(20)
        binding.edtName.filters = StringFilter.getAlphanumericHangul(20)

        binding.edtName.setText(SharedManager.getMemberName())
        if (SharedManager.getMemberGender() == "0") {
            binding.rbMan.isChecked = true
        } else {
            binding.rbWoman.isChecked = true
        }
    }

    private fun setListener() {
        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.edtName.text.isEmpty()) {
                    binding.btnNameDelete.visibility = View.GONE
                } else {
                    binding.btnNameDelete.visibility = View.VISIBLE
                }
            }
        })
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.edtPassword.text.isEmpty()) {
                    binding.btnPasswordDelete.visibility = View.GONE
                } else {
                    binding.btnPasswordDelete.visibility = View.VISIBLE
                }
                if (binding.edtPassword.text.toString() != binding.edtPasswordConfirm.text.toString()) {
                    binding.tvPasswordEx.text = MyUtil.getString(R.string.toast_join_condition_01)
                    binding.tvPasswordEx.setTextColor(Color.parseColor("#ff4a5c"))
                    binding.ivPasswordEx.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_alret_error))
                } else {
                    binding.tvPasswordEx.text = MyUtil.getString(R.string.login_14)
                    binding.tvPasswordEx.setTextColor(Color.parseColor("#d0d2d5"))
                    binding.ivPasswordEx.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_alret))
                }
            }
        })
        binding.edtPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.edtPasswordConfirm.text.isEmpty()) {
                    binding.btnPasswordConfirmDelete.visibility = View.GONE
                } else {
                    binding.btnPasswordConfirmDelete.visibility = View.VISIBLE
                }
                if (binding.edtPassword.text.toString() != binding.edtPasswordConfirm.text.toString()) {
                    binding.tvPasswordEx.text = MyUtil.getString(R.string.toast_join_condition_01)
                    binding.tvPasswordEx.setTextColor(Color.parseColor("#ff4a5c"))
                    binding.ivPasswordEx.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_alret_error))
                } else {
                    binding.tvPasswordEx.text = MyUtil.getString(R.string.login_14)
                    binding.tvPasswordEx.setTextColor(Color.parseColor("#d0d2d5"))
                    binding.ivPasswordEx.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_alret))
                }
            }
        })
        binding.btnNameDelete.setOnClickListener {
            binding.edtName.setText("")
        }
        binding.btnPasswordDelete.setOnClickListener {
            binding.edtPassword.setText("")
        }
        binding.btnPasswordConfirmDelete.setOnClickListener {
            binding.edtPasswordConfirm.setText("")
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
                        Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_update_complete), Toast.LENGTH_SHORT).show()
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
        binding.toolbar.setTitle(MyUtil.getString(R.string.nav_text_01))
        binding.toolbar.setImageLeftOne(MyUtil.getDrawable(R.drawable.ic_navigationbar_back))
        binding.toolbar.binding.tvRightOne.visibility = View.VISIBLE
        binding.toolbar.binding.tvRightOne.text = MyUtil.getString(R.string.modified)
        binding.toolbar.binding.tvRightOne.setOnClickListener {
            if (binding.edtName.text.isEmpty()) {
                Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_please_name_input), Toast.LENGTH_SHORT).show()
            } else if (binding.edtPassword.text.toString() != binding.edtPasswordConfirm.text.toString()) {
                Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_join_condition_01), Toast.LENGTH_SHORT).show()
            } else if (!binding.rbMan.isChecked && !binding.rbWoman.isChecked) {
                Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_join_condition_02), Toast.LENGTH_SHORT).show()
            } else {
                taskUpdateUser(binding.edtPassword.text.toString(), binding.edtName.text.toString(), if (binding.rbMan.isChecked) "0" else "1")
            }
        }
        binding.toolbar.setSelectedListener(
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