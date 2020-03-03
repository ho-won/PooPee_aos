package kr.co.ho1.poopee.menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_setting.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.login.LoginActivity
import kr.co.ho1.poopee.login.TermsActivity

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
                iv_login.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_man_profile))
            } else {
                iv_login.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_woman_profile))
            }
        } else {
            tv_login.text = ObserverManager.context!!.resources.getString(R.string.menu_setting_01)
            tv_logout.visibility = View.GONE
            iv_login.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_profile))
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
    }

    override fun setToolbar() {
        toolbar.setTitle(ObserverManager.context!!.resources.getString(R.string.nav_text_04))
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