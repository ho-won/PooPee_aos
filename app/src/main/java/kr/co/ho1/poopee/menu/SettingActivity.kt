package kr.co.ho1.poopee.menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_setting.*
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
import kr.co.ho1.poopee.login.LoginActivity
import kr.co.ho1.poopee.login.TermsActivity
import org.json.JSONException

@Suppress("DEPRECATION")
class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
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
        if (SharedManager.isLoginCheck()) {
            tv_login.text = SharedManager.getMemberName()
            tv_logout.visibility = View.VISIBLE
            if (SharedManager.getMemberGender() == "0") {
                iv_login.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_man_profile))
            } else {
                iv_login.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_woman_profile))
            }
        } else {
            tv_login.text = MyUtil.getString(R.string.menu_setting_01)
            tv_logout.visibility = View.GONE
            iv_login.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_profile))
        }
        switch_push.isChecked = SharedManager.isPush()
    }

    private fun setListener() {
        layout_login.setOnClickListener {
            if (SharedManager.isLoginCheck()) {
                ObserverManager.logout()
                refresh()
            } else {
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        switch_push.setOnCheckedChangeListener { _, _ ->
            SharedManager.setPush(switch_push.isChecked)
        }
        layout_terms_01.setOnClickListener {
            startActivity(Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_01)
            )
        }
        layout_terms_02.setOnClickListener {
            startActivity(Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_02)
            )
        }
        layout_terms_03.setOnClickListener {
            startActivity(Intent(ObserverManager.context!!, TermsActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .setAction(TermsActivity.ACTION_TERMS_03)
            )
        }
        layout_withdraw.setOnClickListener {
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
        params.put("member_id", SharedManager.getMemberId())

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
        toolbar.setTitle(MyUtil.getString(R.string.nav_text_04))
        toolbar.setImageLeftOne(MyUtil.getDrawable(R.drawable.ic_navigationbar_back))
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