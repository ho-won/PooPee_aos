package kr.co.ho1.poopee.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.common.util.StringFilter
import kr.co.ho1.poopee.databinding.ActivityJoinBinding
import org.json.JSONException

@Suppress("DEPRECATION")
class JoinActivity : BaseActivity() {
    private lateinit var binding: ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListener()
    }

    private fun init() {
        binding.edtUsername.filters = StringFilter.getAlphanumeric(20)
        binding.edtPassword.filters = StringFilter.getAlphanumeric(20)
        binding.edtPasswordConfirm.filters = StringFilter.getAlphanumeric(20)
        binding.edtName.filters = StringFilter.getAlphanumericHangul(20)
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
                checkAllInput()
            }
        })
        binding.edtUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvIdEx.text = MyUtil.getString(R.string.login_11)
                binding.tvIdEx.setTextColor(Color.parseColor("#d0d2d5"))
                binding.ivIdEx.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_alret))
                binding.tvOverlap.isEnabled = binding.edtUsername.text.length > 5
                checkAllInput()
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
                checkAllInput()
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
                checkAllInput()
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
        binding.tvOverlap.setOnClickListener {
            taskOverlap(binding.edtUsername.text.toString())
        }
        binding.cbTerms01.setOnCheckedChangeListener { _, _ ->
            checkAllInput()
        }
        binding.cbTerms02.setOnCheckedChangeListener { _, _ ->
            checkAllInput()
        }
        binding.cbTerms03.setOnCheckedChangeListener { _, _ ->
            checkAllInput()
        }
        binding.tvTerms01Detail.setOnClickListener {
            startActivity(
                Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_01)
            )
        }
        binding.tvTerms02Detail.setOnClickListener {
            startActivity(
                Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_02)
            )
        }
        binding.tvTerms03Detail.setOnClickListener {
            startActivity(
                Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_03)
            )
        }
        binding.layoutBack.setOnClickListener {
            finish()
        }
        binding.layoutJoin.setOnClickListener {
            if (binding.edtUsername.text.toString().isEmpty() || binding.edtPassword.text.toString().isEmpty() || binding.edtPasswordConfirm.text.toString().isEmpty() || binding.edtName.text.toString().isEmpty()) {
                Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_please_all_input), Toast.LENGTH_SHORT).show()
            } else if (binding.edtPassword.text.toString() != binding.edtPasswordConfirm.text.toString()) {
                Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_join_condition_01), Toast.LENGTH_SHORT).show()
            } else if (!binding.rbMan.isChecked && !binding.rbWoman.isChecked) {
                Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_join_condition_02), Toast.LENGTH_SHORT).show()
            } else if (!binding.cbTerms01.isChecked || !binding.cbTerms02.isChecked || !binding.cbTerms03.isChecked) {
                Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_terms_check), Toast.LENGTH_SHORT).show()
            } else {
                taskJoin(binding.edtUsername.text.toString(), binding.edtPassword.text.toString(), binding.edtName.text.toString(), if (binding.rbMan.isChecked) "0" else "1")
            }
        }
    }

    private fun checkAllInput() {
        binding.layoutJoin.isEnabled = false
        binding.tvJoin.setTextColor(Color.parseColor("#6b9bff"))
        binding.ivJoin.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_join_nonext))

        if (binding.edtName.text.isEmpty()) {
            return
        }
        if (!binding.rbMan.isChecked && !binding.rbWoman.isChecked) {
            return
        }
        if (binding.edtUsername.text.isEmpty() || binding.edtUsername.text.length < 6) {
            return
        }
        if (binding.edtPassword.text.isEmpty() || binding.edtPasswordConfirm.text.isEmpty()) {
            return
        }
        if (binding.edtPassword.text.toString() != binding.edtPasswordConfirm.text.toString()) {
            return
        }
        if (!binding.cbTerms01.isChecked || !binding.cbTerms02.isChecked || !binding.cbTerms03.isChecked) {
            return
        }

        binding.layoutJoin.isEnabled = true
        binding.tvJoin.setTextColor(Color.parseColor("#ffffff"))
        binding.ivJoin.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_join_next))
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
                        binding.tvIdEx.text = MyUtil.getString(R.string.login_12)
                        binding.tvIdEx.setTextColor(Color.parseColor("#2470ff"))
                        binding.ivIdEx.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_alret_check))
                    } else if (it.getInt("rst_code") == 1) {
                        binding.tvIdEx.text = MyUtil.getString(R.string.toast_join_id_fail)
                        binding.tvIdEx.setTextColor(Color.parseColor("#ff4a5c"))
                        binding.ivIdEx.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_alret_error))
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
                        Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_join_complete), Toast.LENGTH_SHORT).show()
                        MyUtil.keyboardHide(binding.edtUsername)
                        finish()
                    } else if (it.getInt("rst_code") == 1) {
                        Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_join_id_fail), Toast.LENGTH_SHORT).show()
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