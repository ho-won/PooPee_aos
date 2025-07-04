package kr.co.ho1.poopee.menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.dialog.BasicDialog
import kr.co.ho1.poopee.common.http.RetrofitClient
import kr.co.ho1.poopee.common.http.RetrofitJSONObject
import kr.co.ho1.poopee.common.http.RetrofitParams
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.databinding.ActivitySettingBinding
import kr.co.ho1.poopee.login.LoginActivity
import kr.co.ho1.poopee.login.TermsActivity
import org.json.JSONException

@Suppress("DEPRECATION")
class SettingActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        init()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun init() {

    }

    private fun refresh() {
        if (SharedManager.isLoginCheck) {
            binding.tvLogin.text = SharedManager.memberName
            binding.tvLogout.visibility = View.VISIBLE
            if (SharedManager.memberGender == "0") {
                binding.ivLogin.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_man_profile))
            } else {
                binding.ivLogin.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_woman_profile))
            }
        } else {
            binding.tvLogin.text = MyUtil.getString(R.string.menu_setting_01)
            binding.tvLogout.visibility = View.GONE
            binding.ivLogin.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_profile))
        }
        binding.switchPush.isChecked = SharedManager.isPush
    }

    private fun setListener() {
        binding.layoutLogin.setOnClickListener {
            if (SharedManager.isLoginCheck) {
                ObserverManager.logout()
                refresh()
            } else {
                ObserverManager.root!!.startActivity(
                    Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        binding.switchPush.setOnCheckedChangeListener { _, _ ->
            SharedManager.isPush = binding.switchPush.isChecked
        }
        binding.layoutTerms01.setOnClickListener {
            startActivity(
                Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_01)
            )
        }
        binding.layoutTerms02.setOnClickListener {
            startActivity(
                Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_02)
            )
        }
        binding.layoutTerms03.setOnClickListener {
            startActivity(
                Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_03)
            )
        }
        binding.layoutWithdraw.setOnClickListener {
            val dialog = BasicDialog(
                onLeftButton = {
                    taskWithdraw()
                },
                onCenterButton = {

                },
                onRightButton = {

                }
            )
            dialog.setTextContent(MyUtil.getString(R.string.menu_setting_08))
            dialog.setBtnLeft(MyUtil.getString(R.string.confirm))
            dialog.setBtnRight(MyUtil.getString(R.string.cancel))
            dialog.show(supportFragmentManager, "BasicDialog")
        }
    }

    /**
     * [DELETE] 회원탈퇴
     */
    private fun taskWithdraw() {
        showLoading()
        val params = RetrofitParams()
        params.put("member_id", SharedManager.memberId)

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).deleteUser(params.getParams())

        RetrofitJSONObject(request,
            onSuccess = {
                try {
                    if (it.getInt("rst_code") == 0) {
                        ObserverManager.logout()
                        Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_withdraw), Toast.LENGTH_SHORT).show()
                        refresh()
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
        binding.toolbar.setTitle(MyUtil.getString(R.string.nav_text_04))
        binding.toolbar.setImageLeftOne(MyUtil.getDrawable(R.drawable.ic_navigationbar_back))
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