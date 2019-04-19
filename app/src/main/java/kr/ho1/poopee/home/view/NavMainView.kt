package kr.ho1.poopee.home.view

import android.content.Context
import android.content.Intent
import android.support.design.widget.NavigationView
import android.util.AttributeSet
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.nav_main.view.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.data.SharedManager
import kr.ho1.poopee.common.util.EmailSender
import kr.ho1.poopee.login.LoginActivity
import kr.ho1.poopee.menu.MyInfoActivity
import kr.ho1.poopee.menu.NoticeActivity

class NavMainView : NavigationView {
    private var mContext: Context? = null

    constructor(context: Context) : super(context, null) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        LayoutInflater.from(context).inflate(R.layout.nav_main, this, true)

        refresh()
        setListener()
    }

    fun refresh() {
        if (SharedManager.isLoginCheck()) {
            tv_name.text = SharedManager.getMemberName()
            btn_login.text = ObserverManager.context!!.resources.getString(R.string.logout)
        } else {
            tv_name.text = ""
            btn_login.text = ObserverManager.context!!.resources.getString(R.string.login)
        }
    }

    private fun setListener() {
        btn_login.setOnClickListener {
            if (SharedManager.isLoginCheck()) {
                SharedManager.setLoginCheck(false)
                SharedManager.setMemberId("")
                SharedManager.setMemberUserId("")
                SharedManager.setMemberUserPw("")
                SharedManager.setMemberName("")
                SharedManager.setMemberGender("1")
                refresh()
            } else {
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        btn_my_info.setOnClickListener {
            if (SharedManager.isLoginCheck()) {
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, MyInfoActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            } else {
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        btn_notice.setOnClickListener {
            ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, NoticeActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
        btn_request.setOnClickListener {
            EmailSender.send("seohwjjang@gmail.com", ObserverManager.context!!.getString(R.string.nav_text_04))
        }
    }

}