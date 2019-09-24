package kr.ho1.poopee.home.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_main.view.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.data.SharedManager
import kr.ho1.poopee.common.util.EmailSender
import kr.ho1.poopee.common.util.MySpannableString
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
            btn_logout.visibility = View.VISIBLE
            iv_login.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.img_profile))
        } else {
            val lineString: Array<String> = arrayOf(ObserverManager.context!!.resources.getString(R.string.nav_text_06))
            val span = MySpannableString(ObserverManager.context!!.resources.getString(R.string.nav_text_06))
            span.setLine(lineString)
            tv_name.text = span.getSpannableString()
            btn_logout.visibility = View.INVISIBLE
            iv_login.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.img_logingo))
        }
    }

    private fun setListener() {
        layout_login.setOnClickListener {
            if (!SharedManager.isLoginCheck()) {
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        layout_my_info.setOnClickListener {
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
        layout_notice.setOnClickListener {
            ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, NoticeActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
        layout_request.setOnClickListener {
            EmailSender.send("seohwjjang@gmail.com", ObserverManager.context!!.getString(R.string.nav_text_05))
        }
        layout_setting.setOnClickListener {

        }
        btn_logout.setOnClickListener {
            ObserverManager.logout()
            refresh()
        }
    }

}